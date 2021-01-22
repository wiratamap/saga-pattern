package com.personal.sagapattern.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.exception.AccountNotFoundException;
import com.personal.sagapattern.core.exception.ExceededBalanceException;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.AccountDetail;
import com.personal.sagapattern.core.model.event.EventTransactionAccountInformation;
import com.personal.sagapattern.core.model.event.EventTransactionRequest;
import com.personal.sagapattern.core.model.event.TransactionType;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private AccountService accountService;

    @Mock
    private SagaOrchestrationService sagaOrchestrationService;

    @Mock
    private AccountRepository accountRepository;

    private List<String> failedTransactionEventTopics = Collections.singletonList("EVENT_FAILED_TRANSACTION_RESPONSE");
    private List<String> successTransactionEventTopics = Collections
            .singletonList("EVENT_SUCCESS_TRANSACTION_RESPONSE");
    private List<String> accountUpdatedEventTopics = Collections.singletonList("EVENT_ACCOUNT_UPDATED");

    private ObjectMapper objectMapper = new ObjectMapper();

    private String sourceExternalAccountNumber = "00000000";
    private long sourceAccountBalance = 1_000_000;
    private String destinationExternalAccountNumber = "00000001";
    private long destinationAccountBalance = 1_000_000;
    private UUID mockEventId = UUID.fromString("7b5f770a-68e9-4723-bcad-8cb8c12f362d");
    private int amount = 100_000;
    private String meBankAccountProvider = "MeBank";

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, sagaOrchestrationService, objectMapper,
                failedTransactionEventTopics, successTransactionEventTopics, accountUpdatedEventTopics);
    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(this.accountRepository);
    }

    private void mockSourceAccountWhenFindByExternalAccountNumberIsExist() {
        AccountDetail sourceAccountDetail = AccountDetail.builder()
                .externalAccountNumber(this.sourceExternalAccountNumber).build();
        Account sourceAccount = Account.builder().balance(this.sourceAccountBalance).accountDetail(sourceAccountDetail)
                .build();
        Mockito.when(accountRepository.findByAccountDetailExternalAccountNumber(this.sourceExternalAccountNumber))
                .thenReturn(sourceAccount);
    }

    private void mockDestinationAccountWhenFindByExternalAccountNumberIsExist() {
        AccountDetail destinationAccountDetail = AccountDetail.builder()
                .externalAccountNumber(this.destinationExternalAccountNumber).build();
        Account destinationAccount = Account.builder().balance(this.destinationAccountBalance)
                .accountDetail(destinationAccountDetail).build();
        Mockito.when(accountRepository.findByAccountDetailExternalAccountNumber(this.destinationExternalAccountNumber))
                .thenReturn(destinationAccount);
    }

    private void mockSaveOnAccountRepository() {
        Mockito.when(this.accountRepository.save(any(Account.class))).then(new Answer<Account>() {
            @Override
            public Account answer(InvocationOnMock invocation) throws Throwable {
                AccountDetail accountDetail = new AccountDetail();
                Account account = invocation.getArgument(0);
                account.setId(mockEventId);
                account.setAccountDetail(accountDetail);

                return account;
            }
        });
    }

    @Test
    void processTransaction_shouldDeductBalanceAndOrchestrateAccountUpdatedEvent_whenSourceAccountIsExist()
            throws JsonProcessingException {
        long expectedSourceAccountBalance = 900_000;
        EventTransactionAccountInformation sourceAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider(this.meBankAccountProvider).externalAccountNumber(this.sourceExternalAccountNumber)
                .build();
        EventTransactionAccountInformation destinationAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider("GO-PAY").externalAccountNumber(this.destinationExternalAccountNumber).build();
        EventTransactionRequest eventTransactionRequest = EventTransactionRequest.builder().amount(this.amount)
                .currency("IDR").id(this.mockEventId).eventId(this.mockEventId)
                .sourceAccountInformation(sourceAccountInformation)
                .destinationAccountInformation(destinationAccountInformation).build();
        ArgumentCaptor<Account> account = ArgumentCaptor.forClass(Account.class);
        this.mockSourceAccountWhenFindByExternalAccountNumberIsExist();
        this.mockSaveOnAccountRepository();

        this.accountService.processTransaction(eventTransactionRequest);

        Mockito.verify(this.accountRepository).save(account.capture());
        Assertions.assertEquals(expectedSourceAccountBalance, account.getValue().getBalance());
        Mockito.verify(this.sagaOrchestrationService).orchestrate(Mockito.anyString(),
                Mockito.eq(accountUpdatedEventTopics));
        Mockito.verify(this.sagaOrchestrationService).orchestrate(Mockito.anyString(),
                Mockito.eq(successTransactionEventTopics));
    }

    @Test
    void processTransaction_shouldDeductSourceAndAddDestinationBalanceThenOrchestrateAccountUpdatedEvent_whenSourceAccountAndDestinationIsExist()
            throws JsonProcessingException {
        long expectedSourceAccountBalance = 900_000;
        long expectedDestinationAccountBalance = 1_100_000;
        EventTransactionAccountInformation sourceAccountInformation = EventTransactionAccountInformation.builder()
                .accountHolderName("John Doe").accountProvider(this.meBankAccountProvider)
                .externalAccountNumber(this.sourceExternalAccountNumber).transactionType(TransactionType.DEBIT).build();
        EventTransactionAccountInformation destinationAccountInformation = EventTransactionAccountInformation.builder()
                .accountHolderName("Bertha Doe").accountProvider(this.meBankAccountProvider)
                .externalAccountNumber(this.destinationExternalAccountNumber).transactionType(TransactionType.CREDIT)
                .build();
        EventTransactionRequest eventTransactionRequest = EventTransactionRequest.builder().amount(this.amount)
                .currency("IDR").id(this.mockEventId).eventId(this.mockEventId)
                .sourceAccountInformation(sourceAccountInformation)
                .destinationAccountInformation(destinationAccountInformation).build();
        ArgumentCaptor<Account> account = ArgumentCaptor.forClass(Account.class);
        this.mockSourceAccountWhenFindByExternalAccountNumberIsExist();
        this.mockDestinationAccountWhenFindByExternalAccountNumberIsExist();
        this.mockSaveOnAccountRepository();

        this.accountService.processTransaction(eventTransactionRequest);

        Mockito.verify(this.accountRepository, Mockito.times(2)).save(account.capture());
        Assertions.assertEquals(expectedSourceAccountBalance, account.getAllValues().get(0).getBalance());
        Assertions.assertEquals(expectedDestinationAccountBalance, account.getAllValues().get(1).getBalance());
        Mockito.verify(this.sagaOrchestrationService, Mockito.times(2)).orchestrate(Mockito.anyString(),
                Mockito.eq(accountUpdatedEventTopics));
    }

    @Test
    void processTransaction_shouldNotDeductBalanceAndOrchestrateTransferEvent_whenAccountIsNotFound()
            throws JsonProcessingException {
        EventTransactionAccountInformation sourceAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider(this.meBankAccountProvider).externalAccountNumber(this.sourceExternalAccountNumber)
                .build();
        EventTransactionAccountInformation destinationAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider("GO-PAY").externalAccountNumber(this.destinationExternalAccountNumber).build();
        EventTransactionRequest eventTransactionRequest = EventTransactionRequest.builder().amount(this.amount)
                .currency("IDR").id(this.mockEventId).eventId(this.mockEventId)
                .sourceAccountInformation(sourceAccountInformation)
                .destinationAccountInformation(destinationAccountInformation).build();
        Mockito.when(this.accountRepository.findByAccountDetailExternalAccountNumber(this.sourceExternalAccountNumber))
                .thenReturn(null);

        Executable processTransactionAction = () -> accountService.processTransaction(eventTransactionRequest);

        Mockito.verify(this.accountRepository, Mockito.never()).save(any(Account.class));
        Assertions.assertThrows(AccountNotFoundException.class, processTransactionAction);
        verify(sagaOrchestrationService).orchestrate(Mockito.anyString(), Mockito.eq(failedTransactionEventTopics));
    }

    @Test
    void processTransaction_shouldNotDeductBalanceAndOrchestrateTransactionFailedEvent_whenTransactionAmountExceedsAccountBalance()
            throws JsonProcessingException {
        int exceedingAmount = 50_000_000;
        EventTransactionAccountInformation sourceAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider(this.meBankAccountProvider).externalAccountNumber(this.sourceExternalAccountNumber)
                .build();
        EventTransactionAccountInformation destinationAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider("GO-PAY").externalAccountNumber(this.destinationExternalAccountNumber).build();
        EventTransactionRequest eventTransactionRequest = EventTransactionRequest.builder().amount(exceedingAmount)
                .currency("IDR").id(this.mockEventId).eventId(this.mockEventId)
                .sourceAccountInformation(sourceAccountInformation)
                .destinationAccountInformation(destinationAccountInformation).build();
        this.mockSourceAccountWhenFindByExternalAccountNumberIsExist();

        Executable processTransactionAction = () -> this.accountService.processTransaction(eventTransactionRequest);

        Mockito.verify(this.accountRepository, Mockito.never()).save(any(Account.class));
        Assertions.assertThrows(ExceededBalanceException.class, processTransactionAction);
        Mockito.verify(sagaOrchestrationService).orchestrate(Mockito.anyString(),
                Mockito.eq(failedTransactionEventTopics));
    }
}
