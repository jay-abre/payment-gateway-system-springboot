package com.electric_titans.accountservice.mapper;

import com.electric_titans.accountservice.dto.request.AccountRequest;
import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.dto.response.BalanceResponse;
import com.electric_titans.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    Account accountRequestToAccount(AccountRequest accountDto);

    AccountResponse accountToAccountResponse(Account account);

    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "updatedAt", target = "lastUpdated")
    BalanceResponse accountToBalanceResponse(Account account);
}
