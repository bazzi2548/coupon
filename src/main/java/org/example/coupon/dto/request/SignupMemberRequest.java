package org.example.coupon.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

public record SignupMemberRequest(
        @NotBlank(message = "이메일은 필수 항목입니다.") @Email String email,
        @NotBlank(message = "비밀번호는 필수 항목입니다.") String password,
        @NotBlank(message = "닉네임은 필수 항목입니다.") String nickName,
        @NotEmpty LocalDate birthday
) {
}
