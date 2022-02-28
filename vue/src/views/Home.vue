<template>
  <div class="home">
    <div class="column1">
      <div class="splitTables">
        <div class="createGame">
          <div>
            <h1>Create New Game</h1>
            <createGame />
          </div>
        </div>
      <div class="addingInfo">
        
        <div class="box">
          <h1>View Existing Games</h1>
          <userGames/>
        </div>
      </div>
    </div>
    
    <div class="invites">
      <h1>My Invites</h1>
      <invite-users />
    </div>
    </div>
  </div>
</template>

<script>
import createGame from "@/components/createGame.vue"
import userGames from "@/components/UserGames.vue"
import InviteUsers from '../components/InviteUsers.vue';

export default {
  name: "home",
  components:{
    createGame,
    userGames,
    InviteUsers
  }
};
</script>

<style scoped>
.titleSize{
  font-size: 30px;
  font-weight: 800;
  margin-bottom: 10px;
}
h1{
  cursor: default;
}
.splitTables{
  display: flex;
  justify-content: space-around;
  grid-area: createGame;
  display: grid;
  grid-template-areas: 
  "splitTables addingInfo"
  "invites addingInfo";
  
}
.column1{
  grid-area: col1;
}
.addingInfo{
  display: grid;
  grid-area: addingInfo;
  padding: 5px;
      background-color: rgb(233, 233, 233);
      border: black solid;
      text-align:center;

}
.invites{
  display: inline-grid;
  grid-area: invites;
  justify-self: left;
  margin-top: 20px;
  border: black solid;
  background-color: rgb(233, 233, 233);
  justify-content: center;
  padding: 10px;
  text-align: center;
  margin: 50px;

}
.createGame{
  background-color: rgb(233, 233, 233);
  border: black solid;
  padding: 10px;
  width: 300px;
  text-align: center;
}


@media only screen and (max-width: 790px) {
  .splitTables{
    display: grid; 
    grid-template: 
    "splitTables ."
    "invites invites"
    "addingInfo addingInfo";   
  }


}
</style>