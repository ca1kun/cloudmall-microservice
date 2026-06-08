package edu.scau.mis.product.service;




import com.baomidou.mybatisplus.extension.service.IService;
import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.product.domain.OmsMemberAddress;
import edu.scau.mis.product.dto.AddressQueryDTO;
import edu.scau.mis.product.dto.AddressSaveDTO;
import edu.scau.mis.product.vo.AddressVO;


/**
 * 会员收货地址 Service
 */
public interface OmsMemberAddressService extends IService<OmsMemberAddress> {

    PageResult<AddressVO> pageList(AddressQueryDTO query);

    AddressVO detail(Long id);

    Long add(AddressSaveDTO dto);

    void updateAddress(AddressSaveDTO dto);

    void deleteAddress(Long id);

    void setDefault(Long id);
}
