package com.personal.servicedlqplatform.core.deadletter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterDeleteRequestDto;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeadLetterController {
    private final DeadLetterService deadLetterService;

    @GetMapping("/dead-letters")
    public ResponseEntity<List<DeadLetterResponseDto>> fetchAll() {
        List<DeadLetter> availableDeadLetters = this.deadLetterService.fetchAll();
        List<DeadLetterResponseDto> deadLetters = availableDeadLetters.stream()
                .map(DeadLetterResponseDto::convertFromEntity).collect(Collectors.toList());

        return new ResponseEntity<>(deadLetters, HttpStatus.OK);
    }

    @DeleteMapping("/dead-letters/{id}")
    public void delete(@PathVariable UUID id, @RequestBody DeadLetterDeleteRequestDto deadLetterDeleteRequestDto) {
        this.deadLetterService.delete(id, deadLetterDeleteRequestDto);
    }
}