package com.techelevator.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.techelevator.dao.AccountDao;
import com.techelevator.dao.GameDao;
import com.techelevator.dao.InviteDao;
import com.techelevator.dao.StocksDao;
import com.techelevator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
@RequestMapping ("/")
public class AppController {

    @Autowired
    GameDao gameDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    StocksDao stocksDao;
    @Autowired
    InviteDao inviteDao;

    // CHECK ABOUT PATHS AND BEST PRACTICE

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ViewGamesResponse viewGames(@PathVariable("id") int userId) {
        ViewGamesResponse viewGamesResponse = new ViewGamesResponse();
        List<Game> gameList = gameDao.getGames(userId);
        List<Account> accountList = accountDao.listAccounts(gameList, userId);
        viewGamesResponse.setGamesList(gameList);
        viewGamesResponse.setAccountsList(accountList);
        return viewGamesResponse;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.POST)
    public int createGame(@RequestBody Game newGame) {
        int id = accountDao.createAccount();
        return gameDao.saveGame(newGame, id);
    }

    @RequestMapping(path = "/portfolio/{id}", method = RequestMethod.GET)
    public Portfolio getPortfolio(@PathVariable("id") int accountId) {
        Portfolio portfolio = new Portfolio();
        portfolio.setStockList(stocksDao.listStocks(accountId));
        return portfolio;
    }

    @RequestMapping(path = "/stocks/buyNew", method = RequestMethod.POST)
    public Account buyNewStock(@RequestBody BuyOrder buyOrder) {

        stocksDao.buyNewStock(buyOrder);

        List<Stock> stockList = stocksDao.retrieveSavedPrices();
        List<Integer> accountIdList = accountDao.getActiveAccounts();
        stocksDao.updateForTransaction(stockList, accountIdList);

        Account namedAccount = accountDao.getAccount(buyOrder.getUserId(), buyOrder.getGameId());
        return namedAccount;
    }

    @RequestMapping(path = "/stocks/buy", method = RequestMethod.PUT)
    public Account buyStock(@RequestBody BuyOrder buyOrder) {

        stocksDao.buyExistingStock(buyOrder);

        List<Stock> stockList = stocksDao.retrieveSavedPrices();
        List<Integer> accountIdList = accountDao.getActiveAccounts();
        stocksDao.updateForTransaction(stockList, accountIdList);
        Account namedAccount = accountDao.getAccount(buyOrder.getUserId(), buyOrder.getGameId());
        return namedAccount;
    }

    @RequestMapping(path = "/currentPrices", method = RequestMethod.GET)
    public Object[] getAndUpdateStockData() throws JsonProcessingException {
        List<Stock> pricesList = stocksDao.listCurrentPricesFromWeb();

        if (pricesList.size() > 0) {
            stocksDao.updateCurrentPrices(pricesList);
            List<Integer> accountIdList = accountDao.getActiveAccounts();
            stocksDao.updateStockValue(accountIdList, pricesList);
        }
        String updateTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_LOCAL_TIME);
        Object[] obj = new Object[]{updateTime, stocksDao.retrieveSavedPrices()};
        return obj;
    }

    @RequestMapping(path = "/leaderboard/{id}", method = RequestMethod.GET)
    public List<Account> getLeaderboard(@PathVariable("id") int gameId) {
        return accountDao.getAccountsWithinGame(gameId);
    }

    @RequestMapping(path = "/invite", method = RequestMethod.POST)
    public void invitePlayer(@RequestBody Invite invite) {
        User user = inviteDao.getUserByEmail(invite.getUserEmail());
        inviteDao.invitePlayer(user.getId(), invite.getGameId());
    }

    @RequestMapping(path = "/displayInvites/{userId}", method = RequestMethod.GET)
    public List<Invite> displayUsers(@PathVariable("userId") int userId) {

        return inviteDao.displayInvites(userId);
    }

    @RequestMapping(path = "/confirm", method = RequestMethod.PUT)
    public void confirmInvite(@RequestBody Invite invite) {
        inviteDao.confirmInvite(invite.getUserId(), invite.getGameId());
        int accountId = accountDao.createAccount();
        gameDao.addUser(invite.getGameId(), invite.getUserId(), accountId);
    }

    @RequestMapping(path = "/declineInvite/{userId}/{gameId}", method = RequestMethod.DELETE)
    public void declineInvite(@PathVariable("userId") int userId,
                              @PathVariable("gameId") int gameId) {
        inviteDao.declineInvite(userId, gameId);
    }

    @RequestMapping(path = "/stocks/sell", method = RequestMethod.PUT)
    public Account sellStock(@RequestBody SellOrder sellOrder) {

        stocksDao.sellStock(sellOrder);

        List<Stock> stockList = stocksDao.retrieveSavedPrices();
        List<Integer> accountIdList = accountDao.getActiveAccounts();
        stocksDao.updateForTransaction(stockList, accountIdList);
        Account namedAccount = accountDao.getAccount(sellOrder.getUserId(), sellOrder.getGameId());
        return namedAccount;
    }

    @RequestMapping(path = "/endGame/{gameId}", method = RequestMethod.PUT)
    public void closeOut(@PathVariable("gameId") int gameId) {
        List<Account> accountList = accountDao.getAccountsWithinGame(gameId);
        stocksDao.closeAll(accountList);
    }

    @RequestMapping(path = "/ref/{userId}/{gameId}", method = RequestMethod.GET)
    public Account refreshAccount(@PathVariable("userId") int userId,
                                  @PathVariable("gameId") int gameId) {
        return accountDao.getAccount(userId, gameId);
    }

    @RequestMapping(path = "/game/{gameId}", method = RequestMethod.GET)
    public Game refreshGame(@PathVariable("gameId") int gameId) {
        return gameDao.getGame(gameId);
    }

}
