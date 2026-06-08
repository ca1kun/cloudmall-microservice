package edu.scau.mis.common.dto;

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
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        if (pageSize == null || pageSize == 0) {
            result.setPages(0L);
        } else {
            result.setPages((total + pageSize - 1) / pageSize);
        }

        return result;
    }
}