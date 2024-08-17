package org.example.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.example.coupon.dto.request.SignupMemberRequest;
import org.example.coupon.dto.response.ResponseDTO;
import org.example.coupon.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO signUp(@RequestBody SignupMemberRequest request) {
        return memberService.signUp(request);
    }
}
