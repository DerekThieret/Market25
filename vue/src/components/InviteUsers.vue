<template>
  <table>
    <thead>
      <tr id="textForCursor">
        <th colspan="1">Game Name</th>
        <th colspan="2">Start Date</th>
        <th colspan="3">End Date</th>
        <!-- <th colspan="2" class="actions"></th> -->
      </tr>
    </thead>
    <tbody>
      <tr v-for='invite in myInvites' v-bind:key="invite.gameId">
        <td class="textForCursor" colspan="1">{{ invite.gameName }}</td>
        <td class="textForCursor" colspan="2">{{ invite.startDate }}</td>
        <td class="textForCursor" colspan="3">{{ invite.endDate }}</td>
        <td class="buttons" colspan="4">
            <button v-on:click.prevent="acceptInvite(invite.gameId)">Accept</button>
        </td>
         <td >
            <button v-on:click.prevent="declineInvite(invite.gameId)" colspan="5">Decline</button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script>
import GameService from "../services/GameService.js"
export default {
  
    data(){
        return{
        myInvites: [],
        }
    },
    methods:{
        getInvitesList() {
            GameService.seeMyInvites(this.$store.state.user.id).then((response) => {
              console.log("getting invites")
                this.myInvites = response.data;
      })
    },
    declineInvite(gameId) {
      let inviteGameId = gameId;
      let inviteUserId = this.$store.state.user.id;
      console.log("whether tis nobler in the mind to delete such invites...")
      GameService.declineInvite(inviteUserId, inviteGameId).then((response) => {
        if (response.status === 204 || response.status === 202 || response.status === 200) {
          this.$router.go();
        }
      });
    },
    acceptInvite(gameId) {
      let invite = {
        gameId: gameId,
        userId: this.$store.state.user.id,
      }
      GameService.confirmInvite(invite);
      this.$router.go();
    },
    },   
    beforeMount(){
            this.getInvitesList();
    }
}
</script>

<style scoped>
table{
  width: 500px;
  border-collapse:collapse;
  text-align: center;
}
#textForCursor{
  cursor: default;
}
.actions{
  text-align: center;
}
.buttons{
  /* display: flex;
  justify-content: center; */
  padding: 5px;
}
.buttons:hover{
  cursor: pointer;
}

</style>