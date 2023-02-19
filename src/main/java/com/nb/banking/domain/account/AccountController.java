package com.nb.banking.domain.account;

import static com.nb.banking.global.config.ApiResult.*;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nb.banking.domain.account.dto.TransferRequestDto;
import com.nb.banking.global.config.ApiResult;

import lombok.RequiredArgsConstructor;

@RestController("/account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping
	public ApiResult<Void> transfer(@Valid @RequestBody TransferRequestDto transferRequestDto) {
		accountService.transfer(
				transferRequestDto.getSenderAccountId(),
				transferRequestDto.getRecipientAccountId(),
				transferRequestDto.getTransferAmount());
		return OK(null);
	}

}
