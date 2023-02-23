package com.nb.banking.domain.member;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nb.banking.domain.member.entity.Member;

@Repository
@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByLoginId(String loginId);

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("select m from Member m where m.loginId =:loginId")
	Optional<Member> findByWithPessimisticLock(@Param("loginId") String loginId);

	@EntityGraph(attributePaths = "authorities")
	Optional<Member> findOneWithAuthoritiesByLoginId(String loginId);

}
