package com.infinity.common.core.page;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 自定义分页对象
 */
@Data
public class PageInfo<T> implements Serializable {

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    private long total = 0;

    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;

    /***
     * 总页数
     */
    private long totalPage = 0;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 计算总页数
     */
    public void calcTotalPage() {
        totalPage = (total % size == 0) ? total / size : (total / size + 1);
    }
}
