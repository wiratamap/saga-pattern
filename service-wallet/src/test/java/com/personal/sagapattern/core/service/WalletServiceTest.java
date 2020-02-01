package com.personal.sagapattern.core.service;

import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.TopUpAction;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.core.repository.TopUpActionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    private WalletService walletService;

    @Mock
    private TopUpActionRepository topUpActionRepository;

    private void mockSaveOnTopUpActionRepository() {
        when(topUpActionRepository.save(any(TopUpAction.class))).then(new Answer<TopUpAction>() {
            @Override
            public TopUpAction answer(InvocationOnMock invocation) throws Throwable {
                TopUpAction topUpAction = invocation.getArgument(0);
                topUpAction.setId(UUID.randomUUID());

                return topUpAction;
            }
        });
    }

    @BeforeEach
    void setUp() {
        walletService = new WalletService(topUpActionRepository);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(topUpActionRepository);
    }

    @Test
    void topUp_shouldReturnTopUpResponseWithPendingStatus_whenTopUpIsInvoked() {
        mockSaveOnTopUpActionRepository();
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif("000000001")
                .amount(10000)
                .wallet("GO-PAY")
                .build();

        TopUpResponse topUpResponse = walletService.topUp(topUpRequest);

        verify(topUpActionRepository).save(any(TopUpAction.class));
        assertEquals(Status.PENDING, topUpResponse.getStatus());
    }
}
