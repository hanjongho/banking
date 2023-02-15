package com.nb.banking.domain.account.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.config.entity.BaseTimeEntity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Account extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String accountId;

	private Long amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member owner;
}
