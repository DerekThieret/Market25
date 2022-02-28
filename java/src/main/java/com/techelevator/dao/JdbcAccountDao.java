package com.techelevator.dao;

import com.techelevator.model.Account;
import com.techelevator.model.Game;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate template;

    public JdbcAccountDao(DataSource datasource) {
        template = new JdbcTemplate(datasource);
    }

    private Account mapRowToAccount(SqlRowSet rs) {

        Account account = new Account();
        account.setUserBalance(rs.getBigDecimal("user_balance"));
        account.setStockValue(rs.getBigDecimal("stock_value"));
        account.setDollarAmount(rs.getBigDecimal("dollar_amount"));
        account.setGameId(rs.getInt("game_id"));
        account.setAccountId(rs.getInt("account_id"));

        return account;
    }

    @Override
    public int createAccount() {
        String sql = "INSERT INTO account (stock_value, dollar_amount, user_balance) " +
                "VALUES (0, 100000, 100000) RETURNING account_id;";
            return template.queryForObject(sql, Integer.class);
    }

    @Override
    public Account getAccount(int userId, int gameId) {
        Account account = new Account();
        String sql = "SELECT a.account_id, a.user_balance, a.stock_value, a.dollar_amount, " +
                "gd.game_id AS game_id FROM game_data gd JOIN account a ON a.account_id = gd.account_id WHERE " +
                "gd.game_id = ? AND gd.user_id = ?;";
        SqlRowSet results = template.queryForRowSet(sql, gameId, userId);
        if(results.next()){
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public List<Account> listAccounts(List<Game> games, int userId) {
        List<Account> accountList = new ArrayList<>();
        for(Game userGame : games) {
            Account account = getAccount(userId, userGame.getGameId());
            accountList.add(account);
        }
        return accountList;
    }

    @Override
    public List<Integer> getActiveAccounts() {
        List<Integer> accountIds = new ArrayList<>();
        String sql = "SELECT account_id FROM account";
        SqlRowSet results = template.queryForRowSet(sql);
        while(results.next()) {
            Integer id = results.getInt("account_id");
            accountIds.add(id);
        }
        return accountIds;
    }

    @Override
    public List<Account> getAccountsWithinGame(int gameId) {
        String sql = "SELECT a.account_id, a.user_balance, a.dollar_amount, a.stock_value, u.username" +
                " FROM game_data gd JOIN account a ON gd.account_id = a.account_id JOIN users u ON" +
                " u.user_id = gd.user_id WHERE gd.game_id = ? ORDER BY a.user_balance DESC;";
        SqlRowSet results = template.queryForRowSet(sql, gameId);
        List<Account> accountList = new ArrayList<>();
        while(results.next()) {
            Account account = new Account();
            account.setUserBalance(results.getBigDecimal("user_balance"));
            account.setStockValue(results.getBigDecimal("stock_value"));
            account.setDollarAmount(results.getBigDecimal("dollar_amount"));
            account.setAccountId(results.getInt("account_id"));
            account.setUsername(results.getString("username"));
            accountList.add(account);
        }
        return accountList;
    }


}
