package com.nb.banking.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.nb.banking.domain.account.entity.Account;
import com.nb.banking.global.config.entity.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
	private List<Account> accountList = new ArrayList<>();

	private String loginId;

	private String password;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
	private List<Member> friendList;

}
