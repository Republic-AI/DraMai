package com.infinity.ai.service.impl;

import com.infinity.ai.service.IChatRepository;
import com.infinity.ai.domain.model.Chat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class ChatRepositoryImpl implements IChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public ChatRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int add(Chat chat) {
        final String sql = "INSERT INTO `chat`(`chat_id`, `sender_id`,`target_id`,`msg_type`,`content`,`barrage`,`sname`,`tname`,`create_time`) VALUES (?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, chat.getChatId(), chat.getSenderId(), chat.getTargetId(), chat.getMsgType(), chat.getContent(), chat.getBarrage(),chat.getSname(),chat.getTname(), chat.getCreateTime());
    }

    @Override
    public List<Chat> queryPageList(Integer pageIndex, Integer pageSize, Chat chat) {
        int pIndex = (pageIndex == null) ? 1 : pageIndex;
        int pSize = (pageSize == null) ? 20 : pageSize;

        int index = (pIndex - 1) * pSize;
        //只查询前100条
        if (index > 100) {
            return Collections.emptyList();
        }

        final String sql = "select `sender_id`,`target_id`,`msg_type`,`content`,`sname`, `create_time` from `chat` p where `chat_id`=?  and deleted=0 order by p.create_time desc limit ?, ?";
        return jdbcTemplate.query(sql, new ChatFieldRowMapper(), chat.getChatId(), index, pSize);
    }

    @Override
    public Integer count(Chat chat) {
        String sql = "select count(1) from `chat` where `chat_id`= ? and deleted=0";
        return jdbcTemplate.queryForObject(sql, Integer.class, chat.getChatId());
    }


    private class ChatFieldRowMapper implements RowMapper<Chat> {
        @Override
        public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
            Chat entity = new Chat();
            entity.setSenderId(rs.getLong("sender_id"));
            entity.setTargetId(rs.getLong("target_id"));
            entity.setMsgType(rs.getInt("msg_type"));
            entity.setContent(rs.getString("content"));
            entity.setSname(rs.getString("sname"));
            entity.setCreateTime(rs.getLong("create_time"));
            return entity;
        }
    }
}