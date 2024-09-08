package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.Member;
import org.example.coupon.dto.request.SignupMemberRequest;
import org.example.coupon.dto.response.ResponseDTO;
import org.example.coupon.dto.response.SignUpResponse;
import org.example.coupon.exception.DuplicatedEmailException;
import org.example.coupon.exception.DuplicatedNickNameException;
import org.example.coupon.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.coupon.exception.CustomExceptionMessage.DUPLICATE_EMAIL;
import static org.example.coupon.exception.CustomExceptionMessage.DUPLICATE_NICKNAME;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public ResponseDTO<SignUpResponse> signUp(SignupMemberRequest request) {

        validateDuplicatedEmail(request.email());
        validateDuplicatedNickname(request.nickname());

        Member member = new Member(request, bCryptPasswordEncoder);
        memberRepository.save(member);

        return ResponseDTO.getSuccess(SignUpResponse.of(member));
    }

    public void validateDuplicatedEmail(String email) {
        boolean isDuplicated = memberRepository.existsByEmail(email);
        if (isDuplicated) {
            throw new DuplicatedEmailException(DUPLICATE_EMAIL);
        }
    }

    public void validateDuplicatedNickname(String nickname) {
        boolean isDuplicated = memberRepository.existsByNickname(nickname);
        if (isDuplicated) {
            throw new DuplicatedNickNameException(DUPLICATE_NICKNAME);
        }
    }
}
