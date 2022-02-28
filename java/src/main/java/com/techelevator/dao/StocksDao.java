package com.techelevator.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techelevator.model.Account;
import com.techelevator.model.BuyOrder;
import com.techelevator.model.SellOrder;
import com.techelevator.model.Stock;

import java.util.List;

public interface StocksDao {

    public List<Stock> listStocks(int accountId);

    public void buyNewStock(BuyOrder buyOrder);

    public void buyExistingStock(BuyOrder buyOrder);

    public List<Stock> listCurrentPricesFromWeb() throws JsonProcessingException;

    public void updateCurrentPrices(List<Stock> stockList);

    public List<Stock> retrieveSavedPrices();

    public void updateStockValue(List<Integer> accountIdList, List<Stock> stockPrices);

    public void sellStock(SellOrder sellOrder);

    public void updateForTransaction(List<Stock> stockList, List<Integer> accountIdList);

    public void closeAll(List<Account> accountList);

}
