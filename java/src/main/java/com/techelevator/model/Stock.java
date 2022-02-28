package com.techelevator.model;

import java.math.BigDecimal;

public class Stock {

    private int accountId;
    private String stockSymbol;
    private double numberOfShares;
    private BigDecimal currentPrice;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public double getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares (double numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "accountId=" + accountId +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", numberOfShares=" + numberOfShares +
                ", currentPrice=" + currentPrice +
                '}';
    }
}
