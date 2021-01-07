package com.personal.sagapattern.common.elasticsearch_query.dto;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilterOperatorTest {
    @Test
    void query_shouldReturnBoolQueryBuilderWithMatchQuery_whenFilterRequestIsEqual() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FilterRequest filterRequest = FilterRequest.builder().key("name").value("john").operator(FilterOperator.EQUAL)
                .build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(filterRequest.getKey(), filterRequest.getValue()));

        BoolQueryBuilder actualQuery = filterRequest.getOperator().query(filterRequest, queryBuilder);

        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void query_shouldReturnBoolQueryBuilderWithRangeQueryGte_whenFilterRequestIsGreaterThanOrEqualTo() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FilterRequest filterRequest = FilterRequest.builder().key("balance").value("10000")
                .operator(FilterOperator.GREATER_THAN_OR_EQUAL_TO).build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery(filterRequest.getKey()).gte(filterRequest.getValue()));

        BoolQueryBuilder actualQuery = filterRequest.getOperator().query(filterRequest, queryBuilder);

        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void query_shouldReturnBoolQueryBuilderWithRangeQueryLte_whenFilterRequestIsLessThanOrEqualTo() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FilterRequest filterRequest = FilterRequest.builder().key("balance").value("10000")
                .operator(FilterOperator.LESS_THAN_OR_EQUAL_TO).build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery(filterRequest.getKey()).lte(filterRequest.getValue()));

        BoolQueryBuilder actualQuery = filterRequest.getOperator().query(filterRequest, queryBuilder);

        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void query_shouldReturnBoolQueryBuilderWithRangeQueryGt_whenFilterRequestIsGreaterThan() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FilterRequest filterRequest = FilterRequest.builder().key("balance").value("10000")
                .operator(FilterOperator.GREATER_THAN).build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery(filterRequest.getKey()).gt(filterRequest.getValue()));

        BoolQueryBuilder actualQuery = filterRequest.getOperator().query(filterRequest, queryBuilder);

        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void query_shouldReturnBoolQueryBuilderRangeQueryLt_whenFilterRequestIsLessThan() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FilterRequest filterRequest = FilterRequest.builder().key("balance").value("10000")
                .operator(FilterOperator.LESS_THAN).build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery(filterRequest.getKey()).lt(filterRequest.getValue()));

        BoolQueryBuilder actualQuery = filterRequest.getOperator().query(filterRequest, queryBuilder);

        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void query_shouldReturnBoolQueryBuilderWithMatchPhraseQuery_whenFilterRequestIsPartialMatch() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FilterRequest filterRequest = FilterRequest.builder().key("name").value("john")
                .operator(FilterOperator.PARTIAL_MATCH).build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(filterRequest.getKey(), filterRequest.getValue()));

        BoolQueryBuilder actualQuery = filterRequest.getOperator().query(filterRequest, queryBuilder);

        Assertions.assertEquals(expectedQuery, actualQuery);
    }
}
