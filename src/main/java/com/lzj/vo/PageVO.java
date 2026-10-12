package com.lzj.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** @author 李兆基 */
@Data
public class PageVO<T> implements Serializable {
    private Long pageNumber;
    private Long pageSize;
    private Long totalRow;
    private Long totalPage;
    private List<T> records;
}
