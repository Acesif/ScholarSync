package com.acesif.scholarsync.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseController<T extends BaseEntity> {

    protected final BaseService<T> service;

    @GetMapping("/{id}")
    public T findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("all")
    public List<T> findAll() {
        return service.findAll();
    }

    public Page<T> findAll(int page, int size) {
        return service.findAll(page, size);
    }

    public Page<T> findAllByFieldsAndDirection(int page, int size, Sort.Direction sortDirection, String... sortField) {
        return service.findAll(page, size, sortDirection, sortField);
    }

    public T delete(Long id) {
        return service.delete(id);
    }

    public void hardDelete(Long id) {
        service.hardDelete(id);
    }
}
