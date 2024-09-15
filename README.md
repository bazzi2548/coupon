# Coupon

---
선착순 쿠폰 발급을 위한 프로젝트

# 프로젝트 목표

---
- 쿠폰 발급 무결성을 위한 구현 (ex. 중복 발급, 쿠폰 수량 초과 발급)
- 단일 서버에서 대용량 트래픽을 견디기 위한 서버 구조 설계
- 분산 서버로 전환 시 최소한의 리팩토링을 염두해 둔 설계
- 이유와 타당한 근거에 따른 기술 사용

# 쿠폰 발급 시퀀스 다이어그램

---
``` mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant RateLimiter
    participant Queue
    participant Cache
    participant Redis
    participant MySQL
    
    Client->>Controller: 쿠폰 발급 요청
    Controller->>RateLimiter: 요청 횟수 체크
    alt 정상 요청
        RateLimiter-->>Controller: 요청 허용
        Controller ->> Cache: 중복발급 및 쿠폰 잔여량 확인
        alt 정상 요청  
            Controller->>Queue: 요청 대기열에 추가
            Controller-->>Client: 요청 처리 완료 응답
        else 중복 발급시
            Controller-->>Client: 중복발급 거부 알림
        else 쿠폰 소진시
            Controller-->>Client: 쿠폰소진 알림
        end
    else Too Many Requests
        RateLimiter-->>Controller: 429 Too Many Requests
        Controller-->>Client: 429 응답       
    end

    loop 1초
        alt 재고 여유시
            Queue->>Cache: 발급 요청 처리
        end
    end
    
    Note right of Cache: 발급내역 저장이 비동기적으로 처리됨
    loop 1초
        alt 재고 여유 시
            Cache->>Redis: 발급 완료
            Redis-->>Cache: 중복 체크를 위한 내역 추가
        end
    end
    Note right of Redis: 발급내역 저장이 비동기적으로 처리됨
    loop 5초
        alt 쿠폰 전량 발급 시
            Redis->>MySQL: 발급내역 저장 및 쿠폰 재고 설정
        end
    end
```

# 기술 스택

---
- Java 17, Spring Boot, Redis, MySql, Hibernate, Caffeine cache, Docker-Compose

