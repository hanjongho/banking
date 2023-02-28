package com.nb.banking.domain.account.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.config.entity.BaseTimeEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Account extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String accountNo;

	private Long amount;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL)
	@JsonIgnore
	private Member owner;

	public Account(Long amount) {
		this.accountNo =
				RandomStringUtils.randomNumeric(3)
						+ "-"
						+ RandomStringUtils.randomNumeric(3)
						+ "-"
						+ RandomStringUtils.randomNumeric(6);
		this.amount = amount;
	}

	public void increaseAmount(Long value) {
		this.amount += value;
	}

	public void decreaseAmount(Long value) {
		this.amount -= value;
	}

	public void setOwner(Member owner) {
		this.owner = owner;
	}
}
