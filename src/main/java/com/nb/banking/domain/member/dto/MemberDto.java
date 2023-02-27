package com.nb.banking.domain.member.dto;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb.banking.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

	@NotNull
	@Size(min = 3, max = 50)
	private String loginId;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotNull
	@Size(min = 3, max = 100)
	private String password;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotNull
	private Long amount;

	private Set<AuthorityDto> authorityDtoSet;

	public static MemberDto from(Member member) {
		if (member == null)
			return null;

		return MemberDto.builder()
				.loginId(member.getLoginId())
				.authorityDtoSet(member.getAuthorities().stream()
						.map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
						.collect(Collectors.toSet()))
				.build();
	}
}