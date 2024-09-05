package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.Member;
import org.example.coupon.dto.request.SignupMemberRequest;
import org.example.coupon.dto.response.ResponseDTO;
import org.example.coupon.dto.response.SignUpResponse;
import org.example.coupon.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public ResponseDTO<SignUpResponse> signUp(SignupMemberRequest request) {
        validateDuplicatedEmail(request.email());
        Member member = new Member(request, bCryptPasswordEncoder);
        memberRepository.save(member);
        return ResponseDTO.getSuccess(SignUpResponse.of(member));
    }

    public void validateDuplicatedEmail(String email) {
        boolean isDuplicated = memberRepository.existsByEmail(email);
        if (isDuplicated) {
            throw new IllegalArgumentException("Email already in use");
            // 추후 Custom Exception Class 구현 예정
        }
    }
}
