package com.acesif.scholarsync.base;

import com.acesif.scholarsync.utils.response.Response;
import com.acesif.scholarsync.utils.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseController<T extends BaseEntity> {

    protected final BaseService<T> service;
    protected final BaseUtil util;

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> findById(@PathVariable Long id) {

        T response = service.findById(id);
        if (response == null) {
            return Response.builder()
                    .status(Status.NO_DATA)
                    .message("No data found")
                    .data(null)
                    .build();
        }

        return Response.builder()
                .status(Status.RETRIEVED)
                .message("entity found")
                .data(util.getDTO(response))
                .build();
    }

    @PostMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<?>> findAll() {
        List<T> response = service.findAll();

        if (response == null || response.isEmpty()) {
            return Response.<List<?>>builder()
                    .status(Status.NO_DATA)
                    .message("No data found")
                    .data(Collections.emptyList())
                    .build();
        }

        List<?> dtoList = response.stream()
                .map(util::getDTO)
                .collect(Collectors.toList());

        return Response.<List<?>>builder()
                .status(Status.RETRIEVED)
                .message("Entities found")
                .data(dtoList)
                .build();
    }


    @PostMapping(value = "/findPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Page<?>> findAll(int page, int size) {
        Page<T> response = service.findAll(page, size);

        if (response == null || response.isEmpty()) {
            return Response.<Page<?>>builder()
                    .status(Status.NO_DATA)
                    .message("No data found")
                    .data(Page.empty())
                    .build();
        }

        Page<?> dtoPage = response.map(util::getDTO);

        return Response.<Page<?>>builder()
                .status(Status.RETRIEVED)
                .message("Entities found")
                .data(dtoPage)
                .build();
    }


    @PostMapping(value = "/sortByPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Page<?>> findAllByFieldsAndDirection(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam Sort.Direction sortDirection,
            @RequestParam String... sortField
    ) {
        Page<T> response = service.findAll(page, size, sortDirection, sortField);
        if (response == null || response.isEmpty()) {
            return Response.<Page<?>>builder()
                    .status(Status.NO_DATA)
                    .message("No data found by query or sort direction")
                    .data(Page.empty())
                    .build();
        }

        Page<?> dtoPage = response.map(util::getDTO);

        return Response.<Page<?>>builder()
                .status(Status.RETRIEVED)
                .message("entities found by query or sort direction")
                .data(dtoPage)
                .build();
    }

    @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> delete(@PathVariable Long id) {
        T response = service.delete(id);

        if (response == null) {
            return Response.builder()
                    .status(Status.NO_DATA)
                    .message("No data found")
                    .data(null)
                    .build();
        }

        return Response.builder()
                .status(Status.DELETED)
                .message("entity deleted")
                .data(util.getDTO(response))
                .build();
    }

    @PostMapping(value = "/hardDelete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void hardDelete(@PathVariable Long id) {
        service.hardDelete(id);
    }
}
