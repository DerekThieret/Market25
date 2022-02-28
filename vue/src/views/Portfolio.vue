<template>
  <div class="main">
         <loading-image v-if="$store.state.isLoading"></loading-image>
    <div class="overview">
    </div>
    
    <div section id="tables">
      <table class="money">
        
        <thead>
          <tr>
            <th>Stock Ticker</th>
            <th>Shares</th>
            <th>Total Value</th>
          </tr>
        </thead>
        
        <tbody>
          <tr class="clickable" v-for="stock in assets" 
          v-bind:key="stock.stockSymbol" 
          v-on:click="populateFields(stock.stockSymbol, stock.numberOfShares)">
            <td class="leftTable">{{ stock.stockSymbol }}</td>
            <td class="leftTable">{{ stock.numberOfShares }}</td>
            <td class="leftTable">${{ Number(getAssetLineValue(stock)).toLocaleString()}}</td>
          </tr>
          <!-- adds a bottom space that flexes -->
          <tr><td></td><td></td><td></td></tr>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="2">Total Stock Value: </th>
            <th>${{ Number(account.stockValue).toLocaleString() }}</th>
          </tr>
        </tfoot>
      </table>
      <table class="trade">
        <thead>
          <tr >
            <th colspan="2">Funds for Purchase: ${{ Number(account.dollarAmount).toLocaleString() }} </th>
           </tr>
        </thead>
        <tbody class="middleColumns">
          <td class="midSpacing">Enter stock ticker:<br><br>
            <input type="text" id='tickerInput' max="50px" style="width: 50px"><br>
            <button class="btnBuy" v-on:click="buyStock(generateBuyOrder())"> Buy </button>
          </td >
            <td class="midSpacing">Enter number of shares:<br><br><input type="text" id='sharesInput' style="width: 50px"><br>
            <button class="btnSell" v-on:click="sellStock(generateSellOrder())"> Sell </button>
            </td >
        </tbody>
        <tbody>
          <td class="seeLeader">
            <button class="btnleaderboard" v-on:click="goToLeaderBoard">View Leaderboard: </button>
          </td>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="1">Total Portfolio Value: ${{ Number(account.userBalance).toLocaleString() }}</th>
          </tr>
        </tfoot>
      </table>
      <table class="options">
        <thead>
          <tr>
            <th colspan="2">Stock Ticker</th>
            <th >Price</th>
          </tr>
        </thead>
        <tbody id="rightTable">
          <tr class="clickable" 
          v-for="stock in this.$store.state.stockPrices" 
          v-bind:key="stock.stockSymbol" 
          v-on:click="populateFields(stock.stockSymbol, 10)">
            <td colspan="2" class="rowCheck">{{ stock.stockSymbol }}</td>
            <td class="rowCheck">${{ Number(stock.currentPrice).toLocaleString()}}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="3"> Current as of: {{ this.$store.state.lastRefreshed }}</th>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
</template>

<script>
import GameService from "../services/GameService.js";
import LoadingImage from "../components/LoadingImage";
export default {
  name: "portfolio",
  components:{
    LoadingImage
  },
  data() {
    return {
      game: {
        gameName: this.$store.state.game.gameName,
        endDate: this.$store.state.game.endDate,
        startDate: this.$store.state.game.startDate,
        organizerId: this.$store.state.game.organizerId,
        gameId: this.$store.state.game.gameId
      },
      account: {
        accountId: this.$store.state.account.accountId,
        dollarAmount: this.$store.state.account.dollarAmount,
        stockValue: this.$store.state.account.stockValue,
        userBalance: this.$store.state.account.userBalance,
      },
      
      lastRefreshed: this.$store.state.lastRefreshed,

      assets: [],
    };
  },

  computed: {},
  methods: {
    getAssets(accountId) {
      GameService.getPortfolio(accountId).then((response) => {
        console.log(response.data);
        this.$store.commit('SET_PORTFOLIO', response.data.stockList);
      this.assets = this.$store.state.portfolio;
      });
    },
    populateFields(stockSymbol, numberOfShares){
      document.getElementById('tickerInput').setAttribute("value", stockSymbol);
      document.getElementById('sharesInput').setAttribute("value", numberOfShares);
    },
    generateBuyOrder(){
      let price = 0;
      let stockSymbol = (document.getElementById('tickerInput').value).toUpperCase();
      let sharesToAdd = 0;
      let pricesArr = this.$store.state.stockPrices;

      for(let i = 0; i < pricesArr.length; i++){
        if(stockSymbol === pricesArr[i].stockSymbol){
       price = pricesArr[i].currentPrice;
        }
       }
       if((price * document.getElementById('sharesInput').value) > this.account.dollarAmount){
        sharesToAdd = (this.account.dollarAmount/price)-1 ;
       } else {
         sharesToAdd = document.getElementById('sharesInput').value;
       }

      let buyOrder = {
      sharesToAdd: (sharesToAdd <= 0) ? 1 : sharesToAdd, 
      stockSymbol: stockSymbol, 
      accountId: this.account.accountId, 
      currentPrice: price,
      userId: this.$store.state.user.id,
      gameId: this.game.gameId
      }
      
      return buyOrder;
    },
    buyStock(buyOrder){
      let isFound = false;
      for(let i = 0; i < this.assets.length; i++){
        if(buyOrder.stockSymbol === this.assets[i].stockSymbol){
          isFound = true;
        }
      }
      if(isFound){
        GameService.buyStock(buyOrder).then((response) => {
          console.log(response.data)
          this.$store.commit('SET_ACCOUNT', response.data)
        });
        this.$router.push('/gameDetails');
      } else {
        GameService.buyNewStock(buyOrder).then((response) => {
          console.log(response.data);
          this.$store.commit('SET_ACCOUNT', response.data)
        });
        this.$router.push('/gameDetails');
      }
    },
    generateSellOrder(){
      let price = 0;
      let maxSharesToSubtract = 0;
      let sellQuantity = 0;
      let allShares = false;
      let stockSymbol = (document.getElementById('tickerInput').value).toUpperCase();
      let portfolioArr = this.assets;

      for(let i = 0; i < portfolioArr.length; i++) {
        if(stockSymbol === portfolioArr[i].stockSymbol){
        price = portfolioArr[i].currentPrice;
        maxSharesToSubtract = portfolioArr[i].numberOfShares
        }
       }
        if(document.getElementById('sharesInput').value < maxSharesToSubtract || 
        isNaN(document.getElementById('sharesInput').value)){
       sellQuantity = document.getElementById('sharesInput').value;
       } else { 
         sellQuantity = maxSharesToSubtract;
         allShares = true;
         }

        let sellOrder = {
        sharesToSubtract: sellQuantity,
        stockSymbol: stockSymbol,
        accountId: this.account.accountId, 
        currentPrice: price, 
        allShares: allShares,
        userId: this.$store.state.user.id,
        gameId: this.game.gameId
      }
      return sellOrder;
    },
    sellStock(sellOrder){
      if(sellOrder.sharesToSubtract <= 0) {
        this.$router.push('/gameDetails');
      } else {
        GameService.sellStock(sellOrder).then((response) => {
          console.log(response.data);
          this.$store.commit('SET_ACCOUNT', response.data);
        });
        this.$router.push('/gameDetails');
      }
    },
    getAssetLineValue(stock) {
      let pricesArr = this.$store.state.stockPrices;
      for(let i = 0; i < pricesArr.length; i++) {
        if(stock.stockSymbol === pricesArr[i].stockSymbol) {
          return stock.numberOfShares * pricesArr[i].currentPrice;
        }
      }
    },
     goToLeaderBoard() {
            this.$router.push('/gameDetails');
        },
        
  },
  created() {
    this.getAssets(this.account.accountId);
  },
  
};
</script>
<style scoped>
thead, tfoot{
  cursor: default;
}

.btnBuy, .btnSell{
  cursor: pointer;
  margin-top: 5px;
  padding: 3px;
  width: 75px;
}

.midSpacing{
  margin: 5px;
  font-weight: bold;
  font-size: 13px;
  cursor: default;
}
.middleColumns{
  display: flex;
  justify-content: center;
  padding: 10px;
}
.seeLeader{
  display: flex;
  justify-content: center;
  padding: 30px;
}
.btnleaderboard{
  cursor: pointer;
  width: 180px;
  height: 40px;
}

#tables {
  display: flex;
  justify-content: space-evenly;
}
.clickable{
  height: 20px;
}
.leftSpace{
  display: flex;
  align-self: stretch;
}
/* spacing */
table {
  table-layout: flex;
  width: 33%;
  border-collapse: collapse;
  border: 3 px solid rgb(22, 29, 22);
  margin: 5px;
  
}
thead th:nth-child(1) {
  width: 25%;
}
thead th:nth-child(2) {
  width: 25%;
}
thead th:nth-child(3) {
  width: 25%;
}

th,
td {
  padding: 5px;
  font-size:75%;
  letter-spacing: 1px;
  text-align: center;
}

th
{
  font-size:100%;
  letter-spacing: 2px;
}
/* typography */
/* graphic and colors */
.money thead,
.money tfoot {
  background: url("../images/m.png");

  background-repeat: round; /* Makes money lie flat as if on a tabletop */
  color: rgb(232, 236, 228);
  text-shadow: 2px 3px 2px rgb(5, 42, 56);
   width: 75px;
  height: 50px;
}
.trade thead,
.trade tfoot {
  background: url("../images/m.png");
  
  /*background-repeat: round; /* Makes money lie flat as if on a tabletop */
  color:rgba(255, 251, 0, 0.993);
  text-shadow: 2px 3px 2px rgb(5, 88, 121);
  width: 75px;
  height: 50px;
}
.options thead,
.options tfoot {
  background: url("../images/m.png");

  background-repeat: round; /* Makes money lie flat as if on a tabletop */
  color:rgba(255, 251, 0, 0.993);
  text-shadow: 2px 3px 2px rgb(6, 42, 56);
  width: 75px;
  height: 50px;
}
thead th,
tfoot th,
money.tfoot td {
  
  background: linear-gradient(
    to bottom,
    rgba(99, 197, 200, 0.1),
    rgba(105, 134, 136, 0.5)
  );
  color:rgba(255, 251, 0, 0.993);
  text-shadow: 2px 3px 2px rgb(6, 42, 56);
  border: 2px solid gold;
}
/* zebra striping */

.money tbody tr:nth-child(odd) {
  background-color: rgb(177, 182, 184);
}
.money tbody tr:nth-child(odd):hover {
   background-color:lightcoral;
}


.money tbody tr:nth-child(even) {
  background-color: rgb(211, 206, 206);
}
.money tbody tr:nth-child(even):hover {
 background-color:lightcoral;
}

/* The rows CSS color */
.options tbody tr:nth-child(odd) {
  background-color: rgba(204, 169, 189, 0.2);
}
.options tbody tr:nth-child(odd):hover {
  background-color:lightgreen;
}
.options tbody tr:nth-child(even) {
  background-color: rgba(13, 70, 92, 0.1);
}
.options tbody tr:nth-child(even):hover {
  background-color: rgba(144, 238, 144, 0.9);
}

.clickable:hover{
    cursor: pointer;
    transform: scale(1.02);
    transition: all .2s ease-in-out;
}
@media only screen and (max-width: 600px) {
  #tables{
    display: grid;
  }
}
</style>