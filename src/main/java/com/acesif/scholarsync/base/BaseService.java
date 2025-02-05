package com.acesif.scholarsync.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class BaseService<T extends BaseEntity> {

    protected final BaseRepository<T> baseRepository;

    public T findById(Long id) {
        T entity = baseRepository.findById(id).orElse(null);
        if (entity == null) {
            log.warn("Could not find entity with id {}", id);
            return null;
        }
        log.info("Found entity with id {}", id);
        return entity;
    }

    public List<T> findAll() {
        List<T> entityList = baseRepository.findAllByFlagIsTrue();
        if (entityList.isEmpty()) {
            log.warn("Could not find any entity");
            return null;
        }
        log.info("Found list of entities");
        return entityList;
    }

    public Page<T> findAll(int page, int size) {
        Page<T> entityPage = baseRepository.findAllByFlagIsTrue(PageRequest.of(page, size));
        return getPages(entityPage, page, size);
    }

    public Page<T> findAll(int page, int size, Sort.Direction sortDirection, String... sortField) {
        Page<T> entityPage = baseRepository.findAllByFlagIsTrue(PageRequest.of(page, size, sortDirection, sortField));
        return getPages(entityPage, page, size, sortDirection, sortField);
    }

    public T save(T entity) {
        entity.setCreatedAt(new Date());
        entity.setFlag(true);
        entity.setUpdatedAt(new Date());
        log.info("Saving entity {}", entity);
        return baseRepository.saveAndFlush(entity);
    }

    public T update(T entity) {
        if (entity.getId() == null || findById(entity.getId()) == null) {
            log.warn("Could not update entity with id {}, entity does not exist", entity.getId());
            return null;
        }
        entity.setUpdatedAt(new Date());
        log.info("Updating entity with id {}", entity.getId());
        return baseRepository.save(entity);
    }

    public T delete(Long id) {
        T entity = findById(id);
        if (entity != null) {
            entity.setFlag(false);
            entity.setUpdatedAt(new Date());
            log.info("Deleting entity with id {}", id);
            return update(entity);
        }
        return null;
    }

    public void hardDelete(Long id) {
        log.info("Purging entity with id {}", id);
        baseRepository.deleteById(id);
    }

    private Page<T> getPages(Page<T> entityPage, int page, int size) {
        if (entityPage.hasContent()) {
            log.info("Found entities with page {} and size {}", page, size);
            return entityPage;
        } else {
            log.warn("Could not find any entities");
            return null;
        }
    }

    private Page<T> getPages(Page<T> entityPage, int page, int size, Sort.Direction sortDirection, String... sortField) {
        if (entityPage.hasContent()) {
            log.info("Found entities with page {}, size {}, direction {} and fields {}", page, size, sortDirection, sortField);
            return entityPage;
        } else {
            log.warn("Could not find any entities with the specified fields");
            return null;
        }
    }

}
