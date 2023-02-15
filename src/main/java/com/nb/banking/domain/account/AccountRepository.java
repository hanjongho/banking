package com.nb.banking.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nb.banking.domain.account.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
