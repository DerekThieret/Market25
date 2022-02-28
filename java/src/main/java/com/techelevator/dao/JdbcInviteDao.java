package com.techelevator.dao;
import com.techelevator.model.Invite;
import com.techelevator.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcInviteDao implements InviteDao{

    private JdbcTemplate template;

    public JdbcInviteDao(DataSource datasource) {
        template = new JdbcTemplate(datasource);
    }

    @Override
    public void invitePlayer(Long userId, int gameId) {
        String sql = "INSERT INTO invite (user_id, game_id) VALUES (?, ?);";
        template.update(sql, userId, gameId);
    }

    @Override
    public void confirmInvite(int userId, int gameId) {
        String sql = "UPDATE invite SET accepted = true WHERE user_id = ? AND game_id = ?;";
        template.update(sql, userId, gameId);
    }

    @Override
    public List<Invite> displayInvites(int userId) {
        String sql = "SELECT g.game_name, g.end_date, g.start_date, g.game_id" +
                " FROM invite i JOIN game g ON g.game_id = i.game_id" +
                " WHERE i.user_id = ? AND i.accepted = false";
        List<Invite> inviteList = new ArrayList<>();
        SqlRowSet results = template.queryForRowSet(sql, userId);
        while(results.next()) {
            Invite invite = new Invite();
            invite.setEndDate(results.getDate("end_date").toLocalDate());
            invite.setStartDate(results.getDate("start_date").toLocalDate());
            invite.setGameName(results.getString("game_name"));
            invite.setGameId(results.getInt("game_id"));
            inviteList.add(invite);
        }
        return inviteList;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?;";
        SqlRowSet results = template.queryForRowSet(sql, email);
        User user = new User();
        while(results.next()) {
            user.setId(results.getLong("user_id"));
        }
        return user;
    }

    @Override
    public void declineInvite(int userId, int gameId) {
        String sql = "DELETE FROM invite WHERE user_id = ? AND game_id = ?";
        template.update(sql, userId, gameId);
    }
}
