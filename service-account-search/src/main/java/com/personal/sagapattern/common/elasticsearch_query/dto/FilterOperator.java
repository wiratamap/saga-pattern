package com.personal.sagapattern.common.elasticsearch_query.dto;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public enum FilterOperator {
    EQUAL {
        @Override
        public BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder) {
            String fieldName = filterRequest.getKey();
            String value = filterRequest.getValue();
            return queryBuilder.must(QueryBuilders.matchQuery(fieldName, value));
        }
    },
    GREATER_THAN_OR_EQUAL_TO {
        @Override
        public BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder) {
            String fieldName = filterRequest.getKey();
            String value = filterRequest.getValue();
            return queryBuilder.must(QueryBuilders.rangeQuery(fieldName).gte(value));
        }
    },
    LESS_THAN_OR_EQUAL_TO {
        @Override
        public BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder) {
            String fieldName = filterRequest.getKey();
            String value = filterRequest.getValue();
            return queryBuilder.must(QueryBuilders.rangeQuery(fieldName).lte(value));
        }
    },
    GREATER_THAN {
        @Override
        public BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder) {
            String fieldName = filterRequest.getKey();
            String value = filterRequest.getValue();
            return queryBuilder.must(QueryBuilders.rangeQuery(fieldName).gt(value));
        }
    },
    LESS_THAN {
        @Override
        public BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder) {
            String fieldName = filterRequest.getKey();
            String value = filterRequest.getValue();
            return queryBuilder.must(QueryBuilders.rangeQuery(fieldName).lt(value));
        }
    },
    PARTIAL_MATCH {
        @Override
        public BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder) {
            String fieldName = filterRequest.getKey();
            String value = filterRequest.getValue();
            return queryBuilder.must(QueryBuilders.matchPhraseQuery(fieldName, value));
        }
    };

    public abstract BoolQueryBuilder query(FilterRequest filterRequest, BoolQueryBuilder queryBuilder);
}
