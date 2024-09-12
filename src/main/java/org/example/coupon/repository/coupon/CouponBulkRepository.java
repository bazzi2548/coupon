package org.example.coupon.repository.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.coupon.dto.CouponIssuanceDTO;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<CouponIssuanceDTO> couponIssuances) {
        String sql = "INSERT INTO COUPON_ISSUANCE (COUPON_ID, MEMBER_ID) VALUES(?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CouponIssuanceDTO couponIssuance = couponIssuances.get(i);
                ps.setLong(1, couponIssuance.couponId());
                ps.setLong(2, couponIssuance.memberId());
            }

            @Override
            public int getBatchSize() {
                return couponIssuances.size();
            }
        });
    }
}
