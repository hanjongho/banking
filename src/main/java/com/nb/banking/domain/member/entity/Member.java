package com.nb.banking.domain.member.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

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

	@OneToOne(cascade = CascadeType.ALL)
	private Account account;

	private String loginId;

	private String password;

	@JoinTable(name = "connection",
			joinColumns = {@JoinColumn(name = "member_id")},
			inverseJoinColumns = {@JoinColumn(name = "follower_id")})
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Member> friendList = new HashSet<>();

	public void addFriend(Member member) {
		friendList.add(member);
		member.getFriendList().add(this);
	}

	public void deleteFriend(Member member) {
		friendList.remove(member);
		member.getFriendList().remove(this);
	}

	public void setAccount(Account account) {
		this.account = account;
		account.setOwner(this);
	}

	public Member(String loginId, String password) {
		this.loginId = loginId;
		this.password = password;
	}

}
