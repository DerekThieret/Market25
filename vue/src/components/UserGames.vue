<template>
      <table class="table">
        <thead>
          <tr id="textForCursor">
            <th colspan="1">Game Name</th>
            <th colspan="2">Portfolio Value</th>
            <th colspan="3">Game End Date</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(game, index) in gameList"
            v-bind:key="game.gameId"
            v-on:click="goToGameDetails(game, accountList[index])">
            <td colspan="1" class="hover" id="gameName" >
              {{ game.gameName }}
            </td>
            <td colspan="2" class="hover">

              ${{ Number(accountList[index].userBalance).toLocaleString() }}
            </td>
            <td colspan="3" class="hover" >
              {{ game.endDate }}
            </td>
          </tr>
        </tbody>
      </table>
      <!-- took out the table -->
</template>

<script>
import GameService from "../services/GameService.js";
export default {
  data() {
    return {
      accountList: [],
      gameList: [],
    };
  },
  methods: {
    getGameList(id) {
      GameService.viewGamesByUserId(id).then((response) => {
        this.accountList = response.data.accountsList;
        this.gameList = response.data.gamesList;
      });
    },
    goToGameDetails(game, account) {
      this.$store.commit("SET_GAME", game);
      this.$store.commit("SET_ACCOUNT", account);
      this.$router.push("/gameDetails");
    },
  },
  beforeMount() {
    this.getGameList(this.$store.state.user.id);
  },
};
</script>

<style scoped>

#textForCursor {
  cursor: default;
  justify-content: center;
}

.hover:hover {
  cursor: pointer;
}
#gameName {
  color: darkblue;
  text-decoration: underline;
  font: bold;
}
.table {
  width: 500px;
  text-align: center;
  padding: 5px;
} 

</style>