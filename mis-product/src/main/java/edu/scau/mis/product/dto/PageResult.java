package edu.scau.mis.product.dto;

import lombok.Data;

import java.util.List;

/**
 * 前端分页返回结构。
 * 同时保留 records 和 list，兼容不同列表组件。
 */
@Data
public class PageResult<T> {

    private List<T> records;

    private List<T> list;

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private Long pages;

    public static <T> PageResult<T> of(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setList(records);
        result.setTotal(total == null ? 0L : total);
        result.setPageNum(pageNum == null ? 1 : pageNum);
        result.setPageSize(pageSize == null ? 10 : pageSize);

        long size = result.getPageSize() == null || result.getPageSize() <= 0 ? 10 : result.getPageSize();
        long pages = result.getTotal() == 0 ? 0 : (result.getTotal() + size - 1) / size;
        result.setPages(pages);
        return result;
    }
}
