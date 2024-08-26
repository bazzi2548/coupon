package org.example.coupon.dto.response;

import org.example.coupon.domain.Member;

import java.time.LocalDate;

public record SignUpResponse(
        String email,
        String nickName,
        LocalDate birthday
) {
    public static SignUpResponse of(Member member) {
        return new SignUpResponse(
                member.getEmail(),
                member.getNickname(),
                member.getBirthday()
        );
    }
}
