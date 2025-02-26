package com.infinity.ai.service.impl;

import com.infinity.ai.domain.model.ActionLog;
import com.infinity.ai.service.IActionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ActionRepositoryImpl implements IActionRepository {
    private final JdbcTemplate jdbcTemplate;

    public ActionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int add(ActionLog actionLog) {
        final String sql = "INSERT INTO `action`(`id`, `npc_id`, `aid`, `paid`, `start_time`, `end_time`, `content`, `create_time`) VALUES (?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, actionLog.getId(),
                actionLog.getNpcId(),
                actionLog.getAid(),
                actionLog.getPaid(),
                actionLog.getStartTime(),
                actionLog.getEndTime(),
                actionLog.getContent(),
                actionLog.getCreateTime());
    }
}