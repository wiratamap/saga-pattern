package com.personal.sagapattern.core.model.dto;

import java.util.List;

import com.personal.sagapattern.common.model.Disposable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeadLetterMessage<T extends Disposable> {
    private List<String> originTopics;
    private T message;
}
