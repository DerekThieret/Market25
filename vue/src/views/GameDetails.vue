<template>
  <div class="main">
    <div id="layout">
        <div id="leaderboard">
            <th>
              <div class="title">{{ game.gameName }}</div>
            </th>
            <th v-if="yetToStart" class="gametime"> 
              <div class="end">{{ " Starts on " + game.startDate }}</div>
            </th>
            <th v-if="gameOver" class="gametime"> 
              <div class="end">{{ " Ended on " + game.endDate }}</div>
            </th>
            <th v-else class="gametime"> 
              <div class="end">{{ " Ends on " + game.endDate }}</div>
            </th>
            <table>
               <thead v-if="gameOver">
                <tr>
                    <th class="thSize">Final Standing</th>
                    <th class="thSize">Username</th>
                    <th class="thSize">Final Portfolio Value</th>
                </tr>
                </thead>
                <thead v-else>
                <tr>
                    <th class="thSize">Place</th>
                    <th class="thSize">Username</th>
                    <th class="thSize">Portfolio Value</th>
                </tr>
                </thead>
                <tr v-for='(place, index) in leaderboard' v-bind:key="place.accountId">

                <td class="data">{{ index + 1 }}</td>
                <td class="data">{{ place.username }}</td>
                <td class="data">${{ Number(place.userBalance).toLocaleString() }}</td>
                </tr>
            </table>
            <button v-if="isActive" v-on:click="goToPortfolio()">View My Portfolio</button>
        </div>
            <div id="invite">
            <div>
              <tr>
                  <th class="inviteUser">Invite User with Email</th>
              </tr>
          </div>
             <form class="form-example">
                <label for="email">Enter User Email: </label>
                <input type="email" name="email" id="email" required v-model="email">
                <input type="submit" value="Invite!" v-on:click.prevent="sendInvite()">
                </form>
                </div>
   
    </div>
  </div>
</template>

<script>
import GameService from "../services/GameService.js";
export default {
    name: 'gameDetails',
    data(){
        return{
            
            game:{
                gameName: this.$store.state.game.gameName,
                endDate: this.$store.state.game.endDate,
                startDate: this.$store.state.game.startDate,
                organizerId: this.$store.state.game.organizerId,
                gameId: this.$store.state.game.gameId
            },
            account:{
                accountId: this.$store.state.account.accountId,
                dollarAmount: this.$store.state.account.dollarAmount,
                stockValue: this.$store.state.account.stockValue,
                userBalance: this.$store.state.account.userBalance
            },
            leaderboard: [],
            email: '',
            isActive: true,
            gameOver: false,
            yetToStart: false
        }
      
    },
    methods:{
        viewDetails(){
            GameService.viewDetailsByGameId(this.$store.state.user.id, this.game.gameId).then((response) =>{
                this.game.endDate= response.data.endDate;
                this.game.startDate= response.data.startDate;
                this.game.gameName= this.$store.state.game.gameName;
            })
        },
        goToPortfolio() {
            this.$router.push('/portfolio');
        },
        getAccount() {
          GameService.refreshAccount(this.$store.state.user.id, this.game.gameId).then((response) => {
            if (response.status === 200) {
              this.$store.commit('SET_ACCOUNT', response.data)
            }
          })
        },
        getGame() {
          GameService.refreshGame(this.game.gameId).then((response) => {
            if (response.status === 200) {
              this.$store.commit('SET_GAME', response.data)
              this.game = this.$store.state.game;
              this.checkActiveDate();
              this.checkGameOver();
              this.setLeaderBoard();
            }
          })
        },
        refresh() {
            GameService.updateStockPrices().then((response) => {
                if (response.status === 200) {
                    this.$store.commit('SET_STOCK_PRICES', response.data[1]);
                    this.$store.commit('SET_UPDATE_TIME', response.data[0]);
                    if(this.$store.state.isLoading === true){
                     this.$store.commit('TOGGLE_IS_LOADING');
                    }
                }
            })
        },
        setLeaderBoard(){
          console.log("method called")
            GameService.viewLeaderBoard(this.$store.state.game.gameId).then((response)=>{
                console.log(response);
                this.$store.commit('SET_LEADERBOARD', response.data);
                this.leaderboard = this.$store.state.leaderboard
            })
        },
        sendInvite() {
          let invite = {
            userId: '',
            gameId: this.game.gameId,
            userEmail: this.email
          }
          GameService.invitePlayer(invite);
          this.email = '';
        },
        checkActiveDate(){
          let currentTimeStamp = new Date();
          let endDate = new Date(this.game.endDate);
          let startDate = new Date(this.game.startDate);
          if(currentTimeStamp > endDate || currentTimeStamp < startDate){
           this.isActive = false;
          }else{
            this.isActive = true;
          }
        },
        checkGameOver(){
          console.log('check game over')
         let currentTimeStamp = new Date();
         let endDate = new Date(this.game.endDate);
         if(currentTimeStamp > endDate){
           this.gameOver = true;
           console.log('gameOver = true')
          GameService.endGame(this.game.gameId);
          this.setLeaderBoard();
         }else{
           this.gameOver = false;
         }
        },
        checkGameYetToStart(){
         let currentTimeStamp = new Date();
         let startDate = new Date(this.game.startDate);
         if(currentTimeStamp < startDate){
           this.yetToStart = true;
         }else{
           this.yetToStart = false;
         }
        }
    },
    beforeMount() {
     this.setLeaderBoard();
     this.refresh();
     this.viewDetails();
     },
     created(){
       this.getAccount();
       this.getGame();
        this.checkActiveDate();
        this.checkGameOver();
        this.checkGameYetToStart();
     }
}
</script>


<style scoped>
.main{
  margin-top: 70px;
}
tr, td, th {
  height: 30px;
}
.thSize{
    font-weight: bolder;
    font-size: 20px;
}
.data {
  text-align: center;
  font-size: 17px;
}
.title{
font-size: 30px;
text-transform:uppercase;
letter-spacing: 10px;
height: 7px;

}
.end {
height: 22px;
font-size:17px;
/* border: rgb(17, 16, 16) solid; */
border-bottom:black solid 2px;
width:210px;


}
.gametime {
  display:flex ;
  justify-content:center ;
}

#layout {
display:flex;
justify-content: space-around;
}

form.form-example {
  
    display: table-row;
    
}

label, input {
    display: table-cell;
    margin-bottom: 10px;
}

label {
    padding-right: 10px;
}
/* spacing */
#leaderboard {
  display:grid;
  border-collapse: collapse;
  border: 3 px solid rgb(22, 29, 22);
  align-items: stretch;
  border: black solid;
  padding: 10px;
  width:550px;
      background-color: rgb(233, 233, 233);

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
}
td {
  font-size: 75%;
}
th {
  font-size: 100%;
}
/* typography */
th {
  letter-spacing: 2px;
}
td {
  letter-spacing: 1px;
}
tbody td {
  text-align: center;
  border: burlywood solid;
}
tbody tr {
  text-align: center;
  border: burlywood solid;
}
tfoot th {
  text-align: center;
}
#invite{
    background-color: rgb(233, 233, 233);
    border: black solid;
    padding: 10px;
}
.inviteUser{
  padding-bottom: 50px;
  
  font-size: 28px;
  /* padding: 20px; */
}
h1{
  text-align: center;
}
@media only screen and (max-width: 915px) {
    #leaderBoard{
        display: grid;
        width: 100%;
    }
    #invite{
        display: grid;
    }
    #layout{
        display: grid;
    }
}
</style>