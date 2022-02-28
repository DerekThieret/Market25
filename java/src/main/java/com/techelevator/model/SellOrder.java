package com.techelevator.model;

import java.math.BigDecimal;

public class SellOrder {

    private int sharesToSubtract;
    private String stockSymbol;
    private int accountId;
    private BigDecimal currentPrice;
    private boolean allShares;
    private int gameId;
    private int userId;

    public int getSharesToSubtract() {
        return sharesToSubtract;
    }

    public void setSharesToSubtract(int sharesToSubtract) {
        this.sharesToSubtract = sharesToSubtract;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isAllShares() {
        return allShares;
    }

    public void setAllShares(boolean allShares) {
        this.allShares = allShares;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
