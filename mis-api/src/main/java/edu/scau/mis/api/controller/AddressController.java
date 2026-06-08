package edu.scau.mis.api.controller;


import edu.scau.mis.common.domain.ApiResult;

import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.product.dto.AddressQueryDTO;
import edu.scau.mis.product.dto.AddressSaveDTO;
import edu.scau.mis.product.service.OmsMemberAddressService;
import edu.scau.mis.product.vo.AddressVO;
import org.springframework.web.bind.annotation.*;

/**
 * 收货地址接口
 *
 * 前端访问路径：
 * GET    /api/system/address/list
 * GET    /api/system/address/{id}
 * POST   /api/system/address
 * PUT    /api/system/address
 * DELETE /api/system/address/{id}
 * PUT    /api/system/address/{id}/default
 */
@RestController
@RequestMapping("/system/address")
public class AddressController {

    private final OmsMemberAddressService addressService;

    public AddressController(OmsMemberAddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 查询收货地址列表
     */
    @GetMapping("/list")
    public ApiResult<PageResult<AddressVO>> list(AddressQueryDTO query) {
        return ApiResult.success(addressService.pageList(query));
    }

    /**
     * 查询收货地址详情
     */
    @GetMapping("/{id}")
    public ApiResult<AddressVO> detail(@PathVariable Long id) {
        return ApiResult.success(addressService.detail(id));
    }

    /**
     * 新增收货地址
     */
    @PostMapping
    public ApiResult<Long> add(@RequestBody AddressSaveDTO dto) {
        return ApiResult.success(addressService.add(dto));
    }

    /**
     * 修改收货地址
     */
    @PutMapping
    public ApiResult<Object> update(@RequestBody AddressSaveDTO dto) {
        addressService.updateAddress(dto);
        return ApiResult.success(null);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/{id}")
    public ApiResult<Object> delete(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResult.success(null);
    }

    /**
     * 设置默认收货地址
     */
    @PutMapping("/{id}/default")
    public ApiResult<Object> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return ApiResult.success(null);
    }
}
