package org.example.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.coupon.dto.request.SignupMemberRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(indexes =
@Index(name = "idx_member", columnList = "nickname"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birthday;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Member(SignupMemberRequest request, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.email = request.email().trim();
        this.password = bCryptPasswordEncoder.encode(request.password().trim());
        this.nickname = request.nickname().trim();
        this.birthday = request.birthday();
    }
}


