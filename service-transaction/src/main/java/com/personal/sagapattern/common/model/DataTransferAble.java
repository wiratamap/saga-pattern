package com.personal.sagapattern.common.model;

import org.modelmapper.ModelMapper;

public interface DataTransferAble {
    default <T> T convertTo(Class<T> targetClass) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, targetClass);
    }
}
