package com.acesif.scholarsync.utils.mapper;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GlobalUtil {

    private final ModelMapper modelMapper = new ModelMapper();

    public <T, E> E map(T current, Class<E> targetClass) {
        try {
            log.info("Source class {} -> Target Class {}", current.getClass().getName(), targetClass.getName());
            return modelMapper.map(current, targetClass);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Source or Target class not supported");
        }
    }

    public <E, T> List<E> mapList(List<T> dataList, Class<E> eClass) {
        try {
            log.info("Source list class {} -> Target list Class {}", dataList.getClass().getName(), eClass.getName());
            return dataList.stream()
                    .map(data -> modelMapper.map(data, eClass))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Source list or Target list class not supported");
        }


    }
}
