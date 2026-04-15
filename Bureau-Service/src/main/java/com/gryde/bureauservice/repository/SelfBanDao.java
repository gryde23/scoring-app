package com.gryde.bureauservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SelfBanDao {

    private final JdbcTemplate jdbcTemplate;

    public boolean isBanned(UUID userId) {
        Boolean result = jdbcTemplate.queryForObject(
                """
                select exists (
                    select 1
                    from self_ban
                    where user_id = ? and is_ban = true
                )
                """,
                Boolean.class,
                userId
        );

        return Boolean.TRUE.equals(result);
    }
}
