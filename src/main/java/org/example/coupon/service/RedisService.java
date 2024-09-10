package org.example.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.coupon.domain.coupon.CouponInventory;
import org.example.coupon.repository.coupon.CouponInventoryRepository;
import org.example.coupon.utils.LuaScriptExecutor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final LuaScriptExecutor luaScriptExecutor;
    private final CouponInventoryRepository couponInventoryRepository;

    public Set<Object> getIssueMembers(Long couponId) {
        return redisTemplate.opsForSet().members("Issuance:" + couponId);
    }

    public Long getCouponInventory(Long couponId) {
        Integer redisInventory = (Integer) redisTemplate.opsForValue().get("Inventory:" + couponId);
        if (redisInventory == null) {
            CouponInventory inventory = couponInventoryRepository.findByCouponId(couponId);
            setCouponInventory(couponId, inventory.getAmount());
            return inventory.getAmount();
        }
        return Long.valueOf(redisInventory);
    }

    public void setCouponInventory(Long couponId, Long amount) {
        String key = "Inventory:" + couponId;
        redisTemplate.opsForSet().add(key, amount);
    }

    public Long luaScriptSetCoupon(Long couponId, Long amount, Set<Long> set) {
        String key = "Inventory:" + couponId;
        String luaScript = """
                    local key = KEYS[1]
                    local amount = tonumber(ARGV[1])
                    local data = cjson.decode(ARGV[2])
    
                    for i, value in ipairs(data) do
                        if redis.call('SCARD', key) < amount then
                            redis.call('SADD', key, value)
                        else
                            end
                    end
                    return redis.call('SCARD', key)
                """;
        return luaScriptExecutor.executeLuaScript(luaScript, key, amount, set);
    }

    public void pipeLinedSetCoupon(Long couponId, Set<Long> data) {
        String key = "Inventory:" + couponId;
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        RedisSerializer<Long> valueSerializer = (RedisSerializer<Long>) redisTemplate.getValueSerializer();
        redisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
            data.forEach(value ->
                    connection.sAdd(stringSerializer.serialize(key),
                            valueSerializer.serialize(Long.valueOf(value)))
            );
            return null;
        });
    }
}
