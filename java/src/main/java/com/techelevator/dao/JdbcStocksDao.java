package com.techelevator.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.model.Account;
import com.techelevator.model.BuyOrder;
import com.techelevator.model.SellOrder;
import com.techelevator.model.Stock;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcStocksDao implements StocksDao{

    private JdbcTemplate template;
    private String[] stockSymbols = new String[]{"MSFT","AAPL",
            "AMZN","GOOGL","BABA","FB", "VOD","V","JPM", "MA","TSM",
            "CHT","UNH","HD","INTC","KO", "DIS", "NVS", "NFLX",
            "ORCL","NVDA","TM","CVX","ADBE","CSCO"
            };

    public JdbcStocksDao(DataSource datasource) {
        template = new JdbcTemplate(datasource);
    }

    @Override
    public List<Stock> listStocks(int accountId) {
        List<Stock> stockList = new ArrayList<>();
        String sql = "SELECT sa.account_id, sa.stock_symbol, sa.total_shares, sp.stock_price" +
                " FROM stock_amount sa JOIN stock_price sp ON sa.stock_symbol = sp.stock_symbol" +
                " WHERE account_id = ?;";
        SqlRowSet results = template.queryForRowSet(sql, accountId);
        while(results.next()) {
            Stock stock = new Stock();
            stock.setAccountId(results.getInt("account_id"));
            stock.setNumberOfShares(results.getDouble("total_shares"));
            stock.setStockSymbol(results.getString("stock_symbol"));
            stock.setCurrentPrice(results.getBigDecimal("stock_price"));
            stockList.add(stock);
        }
        return stockList;
    }

    @Override
    public void buyNewStock(BuyOrder buyOrder) {
        String sqlStock = "INSERT INTO stock_amount (account_id, stock_symbol, total_shares) VALUES (?, ?, ?);";
        template.update(sqlStock, buyOrder.getAccountId(), buyOrder.getStockSymbol(), buyOrder.getSharesToAdd());
        String sqlAccount = "UPDATE account SET dollar_amount = dollar_amount - ? WHERE account_id = ?;";
        String sharesString = buyOrder.getSharesToAdd() + "";
        BigDecimal shares = new BigDecimal(sharesString);
        template.update(sqlAccount, buyOrder.getCurrentPrice().multiply(shares), buyOrder.getAccountId());
    }

    @Override
    public void buyExistingStock(BuyOrder buyOrder) {
        String sqlStock = "UPDATE stock_amount SET total_shares = total_shares + ? WHERE account_id = ? AND stock_symbol = ?;";
        template.update(sqlStock, buyOrder.getSharesToAdd(), buyOrder.getAccountId(), buyOrder.getStockSymbol());
        String sqlAccount = "UPDATE account SET dollar_amount = dollar_amount - ? WHERE account_id = ?;";
        String sharesString = buyOrder.getSharesToAdd() + "";
        BigDecimal shares = new BigDecimal(sharesString);
        template.update(sqlAccount, buyOrder.getCurrentPrice().multiply(shares), buyOrder.getAccountId());
    }

    @Override
    public List<Stock> listCurrentPricesFromWeb() throws JsonProcessingException {
        List<Stock> stocksList = new ArrayList<>();
        for(String stockSymbol : stockSymbols) {
            String url = "http://api.marketstack.com/v1/intraday?access_key=28d01c87a292d3ebe6d86949d6462031&symbols=" +
                    stockSymbol + "&interval=1min";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String lastPrice = jsonNode.path("data").path(0).path("last").asText();
            if (lastPrice != "null") {
                BigDecimal stockPrice = new BigDecimal(lastPrice);
                Stock stock = new Stock();
                stock.setCurrentPrice(stockPrice);
                stock.setStockSymbol(stockSymbol);
                stocksList.add(stock);
            }
        }
        return stocksList;
    }

    @Override
    public void updateCurrentPrices(List<Stock> stockList) {
        String sql = "UPDATE stock_price SET stock_price = ? WHERE stock_symbol = ?";
        for (Stock stock : stockList) {
            template.update(sql, stock.getCurrentPrice(), stock.getStockSymbol());
        }
    }

    @Override
    public List<Stock> retrieveSavedPrices() {
        List<Stock> stockList = new ArrayList<>();
        String sql = "SELECT * FROM stock_price;";
        SqlRowSet results = template.queryForRowSet(sql);
        while(results.next()) {
            Stock stock = new Stock();
            stock.setStockSymbol(results.getString("stock_symbol"));
            stock.setCurrentPrice(results.getBigDecimal("stock_price"));
            stockList.add(stock);
        }
        return stockList;
    }

    @Override
    public void updateStockValue(List<Integer> accountIdList, List<Stock> stockPrices) {
        String sqlQuery = "SELECT * FROM stock_amount WHERE account_id = ?;";
        String sqlUpdate = "UPDATE account SET stock_value = ?, user_balance = dollar_amount + stock_value WHERE account_id = ?;";
        for(Integer accountId : accountIdList) {
            BigDecimal updatedStockValue = new BigDecimal("0");
            List<Stock> accountStocks = new ArrayList<>();
            SqlRowSet results = template.queryForRowSet(sqlQuery, accountId);
            while(results.next()) {
                Stock stock = new Stock();
                stock.setNumberOfShares(results.getDouble("total_shares"));
                stock.setAccountId(results.getInt("account_id"));
                stock.setStockSymbol(results.getString("stock_symbol"));
                accountStocks.add(stock);
            }
            for(Stock stockDetails : accountStocks) {
                for(Stock price : stockPrices) {
                    if (stockDetails.getStockSymbol().equals(price.getStockSymbol())) {
                        String shares = stockDetails.getNumberOfShares() + "";
                        BigDecimal sharesNumber = new BigDecimal(shares);
                        BigDecimal multiplied = price.getCurrentPrice().multiply(sharesNumber);
                        updatedStockValue = updatedStockValue.add(multiplied);
                    }
                }
            }
            template.update(sqlUpdate, updatedStockValue, accountId);
        }
    }



    @Override
    public void sellStock(SellOrder sellOrder) {
        String sqlAccount = "UPDATE account SET dollar_amount = dollar_amount + ? WHERE account_id = ?;";
        String sharesString = sellOrder.getSharesToSubtract() + "";
        BigDecimal shares = new BigDecimal(sharesString);
        template.update(sqlAccount, sellOrder.getCurrentPrice().multiply(shares), sellOrder.getAccountId());

        String sqlStock = "UPDATE stock_amount SET total_shares = total_shares - ? WHERE account_id = ? AND stock_symbol = ?;";
        template.update(sqlStock, sellOrder.getSharesToSubtract(), sellOrder.getAccountId(), sellOrder.getStockSymbol());

        if (sellOrder.isAllShares()) {
            String sqlDel = "DELETE FROM stock_amount WHERE total_shares = 0;";
            template.update(sqlDel);
        }

}


    @Override
    public void updateForTransaction(List<Stock> stockList, List<Integer> accountIdList) {
        String sqlQuery = "SELECT * FROM stock_amount WHERE account_id = ?;";
        String sqlUpdateStock = "UPDATE account SET stock_value = ? WHERE account_id = ?;";
        String sqlUpdateBalance = "UPDATE account SET user_balance = (stock_value + dollar_amount) WHERE account_id = ?;";
        for (Integer accountId : accountIdList) {
            BigDecimal updatedStockValue = new BigDecimal("0");
            List<Stock> accountStocks = new ArrayList<>();
            SqlRowSet results = template.queryForRowSet(sqlQuery, accountId);
            while (results.next()) {
                Stock stock = new Stock();
                stock.setNumberOfShares(results.getDouble("total_shares"));
                stock.setAccountId(results.getInt("account_id"));
                stock.setStockSymbol(results.getString("stock_symbol"));
                accountStocks.add(stock);
            }
            for (Stock stockDetails : accountStocks) {
                for (Stock price : stockList) {
                    if (stockDetails.getStockSymbol().equals(price.getStockSymbol())) {
                        String shares = stockDetails.getNumberOfShares() + "";
                        BigDecimal sharesNumber = new BigDecimal(shares);
                        BigDecimal multiplied = price.getCurrentPrice().multiply(sharesNumber);
                        updatedStockValue = updatedStockValue.add(multiplied);
                    }
                }
            }
            template.update(sqlUpdateStock, updatedStockValue, accountId);
            template.update(sqlUpdateBalance, accountId);
        }
    }

    @Override
    public void closeAll(List<Account> accountList) {
        List<Stock> stockList = new ArrayList<>();
        for(Account account : accountList) {
            String sql = "SELECT sa.account_id, sa.stock_symbol, sa.total_shares, sp.stock_price" +
                    " FROM stock_amount sa JOIN stock_price sp ON sa.stock_symbol = sp.stock_symbol" +
                    " WHERE account_id = ?;";
            SqlRowSet results = template.queryForRowSet(sql, account.getAccountId());
            while(results.next()) {
                Stock stock = new Stock();
                stock.setAccountId(results.getInt("account_id"));
                stock.setNumberOfShares(results.getDouble("total_shares"));
                stock.setStockSymbol(results.getString("stock_symbol"));
                stock.setCurrentPrice(results.getBigDecimal("stock_price"));
                stockList.add(stock);
            }
            for(Stock stock : stockList) {
                String sqlAccount = "UPDATE account SET dollar_amount = (dollar_amount + ?)," +
                        " stock_value = (stock_value - ?) WHERE account_id = ?;";
                String sharesString = stock.getNumberOfShares() + "";
                BigDecimal shares = new BigDecimal(sharesString);
                template.update(sqlAccount, stock.getCurrentPrice().multiply(shares),
                        stock.getCurrentPrice().multiply(shares), stock.getAccountId());

                String sqlStock = "UPDATE stock_amount SET total_shares = total_shares - ? WHERE account_id = ? AND stock_symbol = ?;";
                template.update(sqlStock, stock.getNumberOfShares(), stock.getAccountId(), stock.getStockSymbol());
            }
            String sqlUpdate = "UPDATE account SET user_balance = dollar_amount + stock_value WHERE account_id = ?;";
            template.update(sqlUpdate, account.getAccountId());

        }
        String sqlDel = "DELETE FROM stock_amount WHERE total_shares = 0;";
        template.update(sqlDel);
    }


    private Stock mapRowToStock(SqlRowSet results) {

        Stock stock = new Stock();
        stock.setAccountId(results.getInt("account_id"));
        stock.setNumberOfShares(results.getDouble("total_shares"));
        stock.setStockSymbol(results.getString("stock_symbol"));
        stock.setCurrentPrice(results.getBigDecimal("stock_price"));
        return stock;
    }
}
