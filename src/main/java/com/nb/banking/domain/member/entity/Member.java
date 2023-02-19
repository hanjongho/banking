package com.nb.banking.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.nb.banking.domain.account.entity.Account;
import com.nb.banking.global.config.entity.BaseTimeEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 이게 Member가 업데이트가 안됨.. -> 근본적으로 Account만 업데이트 되는데 그건
	@OneToOne(cascade = CascadeType.ALL)
	private Account account;

	private String loginId;

	private String password;

	@ManyToOne(cascade = CascadeType.ALL)
	private Member member;

	@OneToMany(mappedBy = "member")
	private List<Member> friendList = new ArrayList<>();

	public void setAccount(Account account) {
		this.account = account;
		account.setOwner(this);
	}

	public void addFriend(Member member) {
		this.friendList.add(member);

		if (!member.getFriendList().contains(this)) {
			member.getFriendList().add(this);
		}
	}

	public Member(String loginId, String password) {
		this.loginId = loginId;
		this.password = password;
	}

}
