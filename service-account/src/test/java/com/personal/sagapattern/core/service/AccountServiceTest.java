package com.personal.sagapattern.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.exception.AccountNotFoundException;
import com.personal.sagapattern.core.exception.ExceededBalanceException;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.dto.TopUpEventResponse;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TransferRequest;
import com.personal.sagapattern.core.repository.AccountRepository;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private AccountService accountService;

    @Mock
    private SagaOrchestrationService sagaOrchestrationService;

    @Mock
    private AccountRepository accountRepository;

    private List<String> transferEventTopics = transferEventTopics();

    private List<String> failedTopUpEventTopics = failedTopUpEventTopics();

    private List<String> successTopUpEventTopics = successTopUpEventTopics();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String cif = "00000001";
    private int balance = 100000;
    private int topUpAmount = 10000;
    private String destinationOfFund = "00000000";
    private String wallet = "GO-PAY";

    private List<String> transferEventTopics() {
        return Collections.singletonList("EVENT_TRANSFER_REQUEST");
    }

    private List<String> failedTopUpEventTopics() {
        return Collections.singletonList("EVENT_FAILED_TOP_UP_RESPONSE");
    }

    private List<String> successTopUpEventTopics() {
        return Collections.singletonList("EVENT_SUCCESS_TOP_UP_RESPONSE");
    }

    @BeforeEach
    void setUp() {
        accountService = new AccountService(
                accountRepository,
                sagaOrchestrationService,
                transferEventTopics,
                failedTopUpEventTopics,
                successTopUpEventTopics
        );
    }

    @AfterEach
    void tearDown() {
        clearInvocations(accountRepository);
    }

    private void mockAccountIsExistWithFindByIdOnAccountRepository() {
        Account account = Account.builder()
                .cif(cif)
                .balance(balance)
                .build();
        when(accountRepository.findByCif(anyString())).thenReturn(account);
    }

    @Test
    void topUp_shouldDeductBalanceAndOrchestrateTransferEvent_whenAccountIsExist() throws JsonProcessingException {
        int expectedNewBalance = 90000;
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif(cif)
                .amount(topUpAmount)
                .wallet(wallet)
                .destinationOfFund(destinationOfFund)
                .build();
        TransferRequest transferRequest = TransferRequest.builder()
                .cif(cif)
                .amount(topUpAmount)
                .destinationOfFund(destinationOfFund)
                .build();
        String transferEventRequest = objectMapper.writeValueAsString(transferRequest);
        ArgumentCaptor<Account> account = ArgumentCaptor.forClass(Account.class);
        mockAccountIsExistWithFindByIdOnAccountRepository();

        accountService.topUp(topUpRequest);

        verify(accountRepository).save(account.capture());
        assertEquals(expectedNewBalance, account.getValue().getBalance());
        verify(sagaOrchestrationService).orchestrate(transferEventRequest, transferEventTopics);
    }

    @Test
    void topUp_shouldNotDeductBalanceAndOrchestrateTransferEvent_whenAccountIsNotFound() throws JsonProcessingException {
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif(cif)
                .amount(topUpAmount)
                .wallet(wallet)
                .destinationOfFund(destinationOfFund)
                .build();
        TopUpEventResponse failedTopUp = TopUpEventResponse.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .destinationOfFund(topUpRequest.getDestinationOfFund())
                .reason("Account not found")
                .build();
        String failedTopUpEvent = objectMapper.writeValueAsString(failedTopUp);
        when(accountRepository.findByCif(cif)).thenReturn(null);

        Executable topUpAction = () -> accountService.topUp(topUpRequest);

        verify(accountRepository, never()).save(any(Account.class));
        assertThrows(AccountNotFoundException.class, topUpAction);
        verify(sagaOrchestrationService).orchestrate(failedTopUpEvent, failedTopUpEventTopics);
    }

    @Test
    void topUp_shouldNotDeductBalanceAndOrchestrateTransferEvent_whenTopUpAmountExceedsAccountBalance() throws JsonProcessingException {
        int topUpAmount = 500000;
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif(cif)
                .amount(topUpAmount)
                .wallet(wallet)
                .destinationOfFund(destinationOfFund)
                .build();
        TopUpEventResponse failedTopUp = TopUpEventResponse.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .destinationOfFund(topUpRequest.getDestinationOfFund())
                .reason("Top-up amount exceeds account balance")
                .build();
        String failedTopUpEvent = objectMapper.writeValueAsString(failedTopUp);
        mockAccountIsExistWithFindByIdOnAccountRepository();

        Executable topUpAction = () -> accountService.topUp(topUpRequest);

        verify(accountRepository, never()).save(any(Account.class));
        assertThrows(ExceededBalanceException.class, topUpAction);
        verify(sagaOrchestrationService).orchestrate(failedTopUpEvent, failedTopUpEventTopics);
    }

    @Test
    void topUp_shouldDeductBalanceAndOrchestrateSuccessTopUpEvent_whenAccountIsExist() throws JsonProcessingException {
        int expectedNewBalance = 90000;
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif(cif)
                .amount(topUpAmount)
                .wallet(wallet)
                .destinationOfFund(destinationOfFund)
                .build();
        TopUpEventResponse successTopUp = TopUpEventResponse.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .destinationOfFund(topUpRequest.getDestinationOfFund())
                .reason("SUCCESS")
                .build();
        String successTopUpEvent = objectMapper.writeValueAsString(successTopUp);
        ArgumentCaptor<Account> account = ArgumentCaptor.forClass(Account.class);
        mockAccountIsExistWithFindByIdOnAccountRepository();

        accountService.topUp(topUpRequest);

        verify(accountRepository).save(account.capture());
        assertEquals(expectedNewBalance, account.getValue().getBalance());
        verify(sagaOrchestrationService).orchestrate(successTopUpEvent, successTopUpEventTopics);
    }

}
