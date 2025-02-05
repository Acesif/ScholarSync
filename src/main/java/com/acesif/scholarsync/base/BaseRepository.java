package com.acesif.scholarsync.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>,
        JpaSpecificationExecutor<T>,
        PagingAndSortingRepository<T, Long> {

    Page<T> findAllByFlagIsTrue(Pageable pageable);
    List<T> findAllByFlagIsTrue();
}
