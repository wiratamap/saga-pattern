package com.personal.sagapattern.common.elasticsearch_query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    private String key;
    private String value;
    private FilterOperator operator;
}
