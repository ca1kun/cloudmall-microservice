package edu.scau.mis.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
        if (result.getPageSize() == null || result.getPageSize() <= 0) {
            result.setPages(0L);
        } else {
            result.setPages((result.getTotal() + result.getPageSize() - 1) / result.getPageSize());
        }
        return result;
    }
}
