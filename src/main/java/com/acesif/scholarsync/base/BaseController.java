package com.acesif.scholarsync.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseController<T extends BaseEntity> {

    protected final BaseService<T> service;

    public T findById(Long id) {
        return service.findById(id);
    }

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
