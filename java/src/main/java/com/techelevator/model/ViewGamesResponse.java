package com.techelevator.model;

import java.util.List;

public class ViewGamesResponse {

    private List<Game> gamesList;
    private List<Account> accountsList;

    public List<Game> getGamesList() {
        return gamesList;
    }

    public void setGamesList(List<Game> gamesList) {
        this.gamesList = gamesList;
    }

    public List<Account> getAccountsList() {
        return accountsList;
    }

    public void setAccountsList(List<Account> accountsList) {
        this.accountsList = accountsList;
    }
}
