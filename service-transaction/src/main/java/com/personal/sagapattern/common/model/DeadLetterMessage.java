package com.personal.sagapattern.common.model;

import java.util.List;

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
    private T originalMessage;
    private String reason;
}
