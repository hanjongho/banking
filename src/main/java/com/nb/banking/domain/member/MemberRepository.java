package com.nb.banking.domain.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nb.banking.domain.member.dto.MemberResponseDto;
import com.nb.banking.domain.member.entity.ConnectedMember;
import com.nb.banking.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByLoginId(String loginId);

	List<ConnectedMember> findAllConnectedMemberByLoginId(String loginId);
}
