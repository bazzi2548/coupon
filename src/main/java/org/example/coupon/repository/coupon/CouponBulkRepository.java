package org.example.coupon.repository.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.domain.coupon.CouponIssuance;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<CouponIssuance> couponIssuances) {
        long start = System.currentTimeMillis();
        log.info("커넥션 시작 = {}", System.currentTimeMillis());
        String sql = "INSERT INTO COUPON_ISSUANCE (COUPON_ID, MEMBER_ID) VALUES(?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CouponIssuance couponIssuance = couponIssuances.get(i);
                ps.setLong(1, couponIssuance.getCouponId());
                ps.setLong(2, couponIssuance.getMemberId());
            }

            @Override
            public int getBatchSize() {
                return couponIssuances.size();
            }
        });
        log.info("끝 = {}초", (System.currentTimeMillis()-start)/1000 );
    }
}
