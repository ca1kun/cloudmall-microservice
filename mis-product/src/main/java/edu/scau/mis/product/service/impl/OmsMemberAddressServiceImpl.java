package edu.scau.mis.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.product.domain.OmsMemberAddress;
import edu.scau.mis.product.dto.AddressQueryDTO;
import edu.scau.mis.product.dto.AddressSaveDTO;
import edu.scau.mis.product.mapper.OmsMemberAddressMapper;
import edu.scau.mis.product.service.OmsMemberAddressService;
import edu.scau.mis.product.utils.CurrentMemberUtil;
import edu.scau.mis.product.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 会员收货地址 Service 实现
 */
@Service
public class OmsMemberAddressServiceImpl
        extends ServiceImpl<OmsMemberAddressMapper, OmsMemberAddress>
        implements OmsMemberAddressService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;
    private static final int DEFAULT = 1;
    private static final int NOT_DEFAULT = 0;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern ZIP_PATTERN = Pattern.compile("^\\d{6}$");

    private final CurrentMemberUtil currentMemberUtil;

    public OmsMemberAddressServiceImpl(CurrentMemberUtil currentMemberUtil) {
        this.currentMemberUtil = currentMemberUtil;
    }

    @Override
    public PageResult<AddressVO> pageList(AddressQueryDTO query) {
        if (query == null) {
            query = new AddressQueryDTO();
        }

        int pageNum = normalizePageNum(query.getPageNum());
        int pageSize = normalizePageSize(query.getPageSize());
        Long memberId = currentMemberUtil.getCurrentMemberId();

        LambdaQueryWrapper<OmsMemberAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsMemberAddress::getMemberId, memberId)
                .eq(OmsMemberAddress::getDeleteStatus, NOT_DELETED)
                .like(StringUtils.hasText(query.getReceiverName()), OmsMemberAddress::getReceiverName, query.getReceiverName())
                .like(StringUtils.hasText(query.getReceiverPhone()), OmsMemberAddress::getReceiverPhone, query.getReceiverPhone())
                .orderByDesc(OmsMemberAddress::getIsDefault)
                .orderByDesc(OmsMemberAddress::getUpdateTime)
                .orderByDesc(OmsMemberAddress::getCreateTime)
                .orderByDesc(OmsMemberAddress::getId);

        Page<OmsMemberAddress> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<AddressVO> records = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public AddressVO detail(Long id) {
        OmsMemberAddress address = getOwnedAddress(id);
        return toVO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(AddressSaveDTO dto) {
        validateSaveDTO(dto, false);

        Long memberId = currentMemberUtil.getCurrentMemberId();
        LocalDateTime now = LocalDateTime.now();

        long existingCount = countActiveAddress(memberId);
        boolean shouldDefault = existingCount == 0 || Integer.valueOf(DEFAULT).equals(dto.getIsDefault());

        if (shouldDefault) {
            clearDefault(memberId, null);
        }

        OmsMemberAddress address = new OmsMemberAddress();
        copyFromDTO(dto, address);
        address.setId(null);
        address.setMemberId(memberId);
        address.setIsDefault(shouldDefault ? DEFAULT : NOT_DEFAULT);
        address.setDeleteStatus(NOT_DELETED);
        address.setCreateTime(now);
        address.setUpdateTime(now);

        save(address);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(AddressSaveDTO dto) {
        validateSaveDTO(dto, true);

        OmsMemberAddress oldAddress = getOwnedAddress(dto.getId());
        Long memberId = currentMemberUtil.getCurrentMemberId();

        if (Integer.valueOf(DEFAULT).equals(dto.getIsDefault())) {
            clearDefault(memberId, dto.getId());
        }

        copyFromDTO(dto, oldAddress);
        oldAddress.setMemberId(memberId);
        oldAddress.setDeleteStatus(NOT_DELETED);
        oldAddress.setUpdateTime(LocalDateTime.now());
        oldAddress.setIsDefault(Integer.valueOf(DEFAULT).equals(dto.getIsDefault()) ? DEFAULT : NOT_DEFAULT);

        updateById(oldAddress);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id) {
        OmsMemberAddress address = getOwnedAddress(id);
        Long memberId = currentMemberUtil.getCurrentMemberId();
        boolean wasDefault = Integer.valueOf(DEFAULT).equals(address.getIsDefault());

        OmsMemberAddress update = new OmsMemberAddress();
        update.setId(id);
        update.setDeleteStatus(DELETED);
        update.setIsDefault(NOT_DEFAULT);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);

        if (wasDefault) {
            setFirstAddressAsDefault(memberId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        OmsMemberAddress address = getOwnedAddress(id);
        Long memberId = currentMemberUtil.getCurrentMemberId();

        clearDefault(memberId, id);

        OmsMemberAddress update = new OmsMemberAddress();
        update.setId(address.getId());
        update.setIsDefault(DEFAULT);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    private void clearDefault(Long memberId, Long excludeId) {
        LambdaQueryWrapper<OmsMemberAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsMemberAddress::getMemberId, memberId)
                .eq(OmsMemberAddress::getDeleteStatus, NOT_DELETED)
                .eq(OmsMemberAddress::getIsDefault, DEFAULT)
                .ne(excludeId != null, OmsMemberAddress::getId, excludeId);

        OmsMemberAddress update = new OmsMemberAddress();
        update.setIsDefault(NOT_DEFAULT);
        update.setUpdateTime(LocalDateTime.now());
        update(update, wrapper);
    }

    private void setFirstAddressAsDefault(Long memberId) {
        LambdaQueryWrapper<OmsMemberAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsMemberAddress::getMemberId, memberId)
                .eq(OmsMemberAddress::getDeleteStatus, NOT_DELETED)
                .orderByDesc(OmsMemberAddress::getUpdateTime)
                .orderByDesc(OmsMemberAddress::getId)
                .last("LIMIT 1");

        OmsMemberAddress first = getOne(wrapper, false);
        if (first == null) {
            return;
        }

        OmsMemberAddress update = new OmsMemberAddress();
        update.setId(first.getId());
        update.setIsDefault(DEFAULT);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    private long countActiveAddress(Long memberId) {
        LambdaQueryWrapper<OmsMemberAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsMemberAddress::getMemberId, memberId)
                .eq(OmsMemberAddress::getDeleteStatus, NOT_DELETED);
        return count(wrapper);
    }

    private OmsMemberAddress getOwnedAddress(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }

        Long memberId = currentMemberUtil.getCurrentMemberId();

        LambdaQueryWrapper<OmsMemberAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsMemberAddress::getId, id)
                .eq(OmsMemberAddress::getMemberId, memberId)
                .eq(OmsMemberAddress::getDeleteStatus, NOT_DELETED);

        OmsMemberAddress address = getOne(wrapper, false);
        if (address == null) {
            throw new IllegalArgumentException("收货地址不存在或无权操作");
        }
        return address;
    }

    private void validateSaveDTO(AddressSaveDTO dto, boolean update) {
        if (dto == null) {
            throw new IllegalArgumentException("地址信息不能为空");
        }
        if (update && dto.getId() == null) {
            throw new IllegalArgumentException("地址ID不能为空");
        }
        if (!StringUtils.hasText(dto.getReceiverName())) {
            throw new IllegalArgumentException("请输入收货人姓名");
        }
        String receiverName = dto.getReceiverName().trim();
        if (receiverName.length() < 2 || receiverName.length() > 20) {
            throw new IllegalArgumentException("收货人姓名长度必须为2-20个字符");
        }
        if (!StringUtils.hasText(dto.getReceiverPhone()) || !PHONE_PATTERN.matcher(dto.getReceiverPhone().trim()).matches()) {
            throw new IllegalArgumentException("请输入正确的11位手机号");
        }
        if (!StringUtils.hasText(dto.getProvince()) || !StringUtils.hasText(dto.getCity()) || !StringUtils.hasText(dto.getDistrict())) {
            throw new IllegalArgumentException("请选择完整的省市区");
        }
        if (!StringUtils.hasText(dto.getDetailAddress())) {
            throw new IllegalArgumentException("请输入详细地址");
        }
        String detailAddress = dto.getDetailAddress().trim();
        if (detailAddress.length() < 5 || detailAddress.length() > 100) {
            throw new IllegalArgumentException("详细地址长度必须为5-100个字符");
        }
        if (StringUtils.hasText(dto.getZipCode()) && !ZIP_PATTERN.matcher(dto.getZipCode().trim()).matches()) {
            throw new IllegalArgumentException("邮政编码为6位数字");
        }
        if (dto.getIsDefault() == null) {
            dto.setIsDefault(NOT_DEFAULT);
        }
        if (!Integer.valueOf(DEFAULT).equals(dto.getIsDefault())) {
            dto.setIsDefault(NOT_DEFAULT);
        }
    }

    private void copyFromDTO(AddressSaveDTO dto, OmsMemberAddress address) {
        address.setReceiverName(trim(dto.getReceiverName()));
        address.setReceiverPhone(trim(dto.getReceiverPhone()));
        address.setProvince(trim(dto.getProvince()));
        address.setCity(trim(dto.getCity()));
        address.setDistrict(trim(dto.getDistrict()));
        address.setDetailAddress(trim(dto.getDetailAddress()));
        address.setZipCode(StringUtils.hasText(dto.getZipCode()) ? dto.getZipCode().trim() : null);
    }

    private AddressVO toVO(OmsMemberAddress address) {
        if (address == null) {
            return null;
        }
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum <= 0 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
