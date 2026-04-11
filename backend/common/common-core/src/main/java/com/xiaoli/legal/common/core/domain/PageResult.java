package com.xiaoli.legal.common.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {
    }

    public PageResult(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
        this.pages = total % size == 0 ? total / size : total / size + 1;
    }

    /**
     * 构建空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(1L, 0L, 0L, List.of());
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long current, Long size, Long total, List<T> records) {
        return new PageResult<>(current, size, total, records);
    }

    // Getters and Setters
    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }
}