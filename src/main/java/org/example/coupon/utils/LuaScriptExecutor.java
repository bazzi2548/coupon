package org.example.coupon.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LuaScriptExecutor {

    private final RedisTemplate<String, Object> redisTemplate;

    public Long executeLuaScript(final String luaScript, String key, Long amount, Set<Long> data) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        return redisTemplate.execute(redisScript, List.of(key), amount,
                data.stream()
                        .map(String::valueOf)
                        .toArray(String[]::new));
    }
}
