package com.nb.banking.domain.account;

import static com.nb.banking.global.config.ApiResult.*;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nb.banking.domain.account.dto.AccountDto;
import com.nb.banking.domain.account.dto.TransferDto;
import com.nb.banking.global.config.ApiResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping
	@PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
	public ApiResult<TransferDto> transfer(@Valid @RequestBody TransferDto transferDto) {
		String senderId = SecurityContextHolder.getContext().getAuthentication().getName();

		accountService.transfer(senderId, transferDto.getTransferAmount(), transferDto.getReceiverLoginId());
		return OK(transferDto);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
	public ApiResult<AccountDto> getMyAccountInfo() {
		String loginId = SecurityContextHolder.getContext().getAuthentication().getName();

		return OK(AccountDto.from(accountService.getMyAccountInfo(loginId)));
	}

}
