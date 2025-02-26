package com.infinity.ai.service.impl;

import com.infinity.ai.domain.model.PlayerExtend;
import com.infinity.ai.domain.model.TGUser;
import com.infinity.ai.service.IPlayerRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class PlayerRepositoryImpl implements IPlayerRepository {
    private final JdbcTemplate jdbcTemplate;

    public PlayerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long findIdByName(String loginName) {
        String sql = "select `id` from `player` p where p.name=? limit 1";
        try {
            Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql, new Object[]{loginName});
            Object id = resultMap.get("id");
            return id == null ? 0L : (Long) id;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public String findNameById(Long playerId) {
        String sql = "select `name` from `player` p where p.id=? limit 1";
        try {
            Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql, new Object[]{playerId});
            Object name = resultMap.get("name");
            return name == null ? "" : (String) name;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public PlayerExtend findById(long id) {
        try {
            String sql = "select * from `user` p where p.id = ?";
            return jdbcTemplate.queryForObject(sql, new PlayerExtendRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public PlayerExtend findByUserId(String userId) {
        try {
            String sql = "select * from `user` p where p.user_id = ? limit 1";
            return jdbcTemplate.queryForObject(sql, new PlayerExtendRowMapper(), userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int addPlayerExtend(PlayerExtend player) {
        final String sql = "INSERT INTO `user`(`id`, `name`,`user_id`,`invite_code`,`invite`,`createdate`) VALUES (?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,
                player.getId(),
                player.getName(),
                player.getUserId(),
                player.getInviteCode(),
                player.getInvite(),
                player.getCreatedate());
    }

    @Override
    public int delete(String name) {
        String sql = "DELETE FROM `user` WHERE name=?";
        return jdbcTemplate.update(sql, name);
    }

    @Override
    public Integer countSource(Long chatId) {
        String sql = "select count(1) from `telegram_user` where chat_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, chatId);
    }

    @Override
    public int addPlayerSource(TGUser user) {
        final String sql = "INSERT INTO `telegram_user`(`chat_id`,`name`,`source`,`state`,`create_date`,`update_time`,`deleted`) " +
                "VALUES (?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,
                user.getChatId(),
                user.getName(),
                user.getSource(),
                user.getState(),
                user.getCreatedate(),
                user.getUpdateTime(),
                user.getDeleted());
    }

    @Override
    public String findSourceByUnitIdType(Long chatId) {
        String sql = "select `source` from `telegram_user` where `chat_id`=? limit 1";
        try {
            Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql, new Object[]{chatId});
            Object source = resultMap.get("source");
            return source == null ? "" : (String) source;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Integer countUnSendTGUser() {
        String sql = "select count(1) from `telegram_user` p where `state`=0";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public List<TGUser> queryTGUserList(Integer pageIndex, Integer pageSize) {
        final String sql = "select `id`,`chat_id`,`name`,`source` from `telegram_user` where `state`=0 limit ?, ?";
        return jdbcTemplate.query(sql, new TGUserRowMapper(), pageIndex, pageSize);
    }

    @Override
    public int updateTGUserState(List<Long> ids) {
        String sql = "update `telegram_user` set `state`= 1 where id in(:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        NamedParameterJdbcTemplate jdbcTemplateObject = new
                NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        return jdbcTemplateObject.update(sql, params);
    }

    static class PlayerExtendRowMapper implements RowMapper<PlayerExtend> {
        @Override
        public PlayerExtend mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerExtend extend = new PlayerExtend();
            extend.setId(rs.getLong("id"));
            extend.setName(rs.getString("name"));
            extend.setUserId(rs.getString("user_id"));
            extend.setInviteCode(rs.getString("invite_code"));
            extend.setInvite(rs.getString("invite"));
            extend.setCreatedate(rs.getLong("createdate"));
            return extend;
        }
    }

    private class TGUserRowMapper implements RowMapper<TGUser> {
        @Override
        public TGUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            TGUser entity = new TGUser();
            entity.setId(rs.getLong("id"));
            entity.setChatId(rs.getLong("chat_id"));
            entity.setName(rs.getString("name"));
            entity.setSource(rs.getString("source"));
            return entity;
        }
    }
}