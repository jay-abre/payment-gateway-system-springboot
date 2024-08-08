package com.electric_titans.accountservice.service.impl;

import com.electric_titans.accountservice.dto.request.AccountRequest;
import com.electric_titans.accountservice.dto.request.AccountStatusUpdateRequest;
import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.dto.response.AccountStatusUpdateResponse;
import com.electric_titans.accountservice.dto.response.BalanceResponse;
import com.electric_titans.accountservice.entity.Account;
import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.exception.KycNotFullyVerifiedException;
import com.electric_titans.accountservice.exception.ResourceNotFoundException;
import com.electric_titans.accountservice.mapper.AccountMapper;
import com.electric_titans.accountservice.repository.AccountRepository;
import com.electric_titans.accountservice.service.AccountService;
import com.electric_titans.common.event.PaymentSuccessEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final WebClient webClient;

    @Override
    public AccountResponse createAccount(AccountRequest accountRequest, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);

        boolean isKycVerified = webClient.post()
                .uri(String.format("http://localhost:8081/api/v1/users/%s/kyc-status", accountRequest.getUserId()))
                .header("Authorization", String.format("Bearer %s", jwt))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (!isKycVerified) {
            throw new KycNotFullyVerifiedException(accountRequest.getUserId());
        }

        Account account = AccountMapper.INSTANCE.accountRequestToAccount(accountRequest);
        account.setStatus(accountRequest.getStatus());
        account.setAccountType(accountRequest.getAccountType());
        Account savedAccount = accountRepository.save(account);
        log.info("Account created with account id : {}", savedAccount.getId());
        return AccountMapper.INSTANCE.accountToAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountByAccountId(Long accountId) {
        return accountRepository.findById(accountId)
                .map(AccountMapper.INSTANCE::accountToAccountResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));
    }

    @Override
    public List<AccountResponse> getAllAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountMapper.INSTANCE::accountToAccountResponse)
                .toList();
    }

    @Override
    public AccountResponse updateAccountByAccountId(Long accountId, AccountRequest accountRequest) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        account.setUserId(accountRequest.getUserId());
        account.setAccountType(accountRequest.getAccountType());
        account.setBalance(accountRequest.getBalance());
        account.setCurrency(accountRequest.getCurrency());
        account.setStatus(accountRequest.getStatus());
        Account updatedAccount = accountRepository.save(account);
        log.info("Account updated with account account id : {}", updatedAccount.getId());
        return AccountMapper.INSTANCE.accountToAccountResponse(updatedAccount);
    }

    @Override
    public void deactivateAccountByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        account.setStatus(AccountStatusEnum.INACTIVE);
        accountRepository.save(account);
        log.info("Account deactivated with id : {}", accountId);
    }

    @Override
    public BalanceResponse getCurrentBalanceByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        return AccountMapper.INSTANCE.accountToBalanceResponse(account);
    }

    @Override
    public AccountStatusUpdateResponse updateAccountStatusByAccountId(Long accountId, AccountStatusUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        account.setStatus(request.getStatus());
        Account updatedAccount = accountRepository.save(account);
        log.info("Account status updated with id : {}", updatedAccount.getId());
        AccountStatusUpdateResponse accountStatusUpdateResponse = AccountStatusUpdateResponse.builder()
                .message("Account status updated successfully.")
                .status(updatedAccount.getStatus())
                .build();

        return accountStatusUpdateResponse;
    }

    @KafkaListener(topics = "DEPOSIT-SUCCESS", groupId = "${spring.kafka.consumer.group-id}")
    public void handleDepositSuccessEvent(PaymentSuccessEvent paymentSuccessEvent) {
        Account account = accountRepository.findById(paymentSuccessEvent.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", paymentSuccessEvent.getFromAccountId().toString()));

        Long balance = account.getBalance();
        Long transactionAmount = paymentSuccessEvent.getTransactionAmount();
        Long newBalance = Long.sum(balance, transactionAmount);
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @KafkaListener(topics = "WITHDRAW-SUCCESS", groupId = "${spring.kafka.consumer.group-id}")
    public void handleWithdrawSuccessEvent(PaymentSuccessEvent paymentSuccessEvent) {
        Account account = accountRepository.findById(paymentSuccessEvent.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", paymentSuccessEvent.getFromAccountId().toString()));

        Long balance = account.getBalance();
        Long transactionAmount = paymentSuccessEvent.getTransactionAmount();
        Long newBalance = balance - transactionAmount;
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @KafkaListener(topics = "TRANSFER-SUCCESS", groupId = "${spring.kafka.consumer.group-id}")
    public void handleTransferSuccessEvent(PaymentSuccessEvent paymentSuccessEvent) {

        log.info("From Account Id: {}", paymentSuccessEvent.getFromAccountId());
        log.info("To Account Id: {}", paymentSuccessEvent.getToAccountId());
        Account fromAccount = accountRepository.findById(paymentSuccessEvent.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", paymentSuccessEvent.getFromAccountId().toString()));

        Account toAccount = accountRepository.findById(paymentSuccessEvent.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", paymentSuccessEvent.getToAccountId().toString()));

        Long transactionAmount = paymentSuccessEvent.getTransactionAmount();
        Long fromCurrentBalance = fromAccount.getBalance();
        Long toCurrentBalance = toAccount.getBalance();
        Long fromNewBalance = fromCurrentBalance - transactionAmount;
        Long toNewBalance = Long.sum(toCurrentBalance, transactionAmount);
        fromAccount.setBalance(fromNewBalance);
        toAccount.setBalance(toNewBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
