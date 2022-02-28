<template>
  <div id="main">
      <form class="form" v-on:submit='addGame()'>
        <div class="aligning">
            <div class="space">Name: <input type="text" v-model='game.gameName'/></div>
            <div class="space">Start date: <input type="date" min="2022-02-07"  v-model='game.startDate' class="dateCursor"></div>
            <div class="space"> End date: <input type="date" min="2022-02-08" v-model='game.endDate' class="dateCursor"></div>
            <div class="space" id="createGame"><button type="submit" value="save" class="dateCursor">Create Game</button></div>
        </div>
        
        </form>
         
   </div>
</template>

<script>
import GameService from "../services/GameService.js";
export default {
    data(){
        return{
            
            game:{
                gameName: '',
                endDate: '',
                startDate: '',
                organizerId: this.$store.state.user.id
            }
        }
    },
    methods:{
        addGame(){
            if (this.game.gameName != ""){
                // if(!(this.game.gameName.equals(this.game.gameName))){
                    if (Date.parse(this.game.startDate) < Date.parse(this.game.endDate)){
                    GameService.createGame(this.game, this.$store.state.user.id).then(() =>{
                        
                            
                            this.game.endDate="";
                            this.game.startDate="";
                            this.game.gameName="";
                            this.$router.push({name:'home'});
                
                
            })
            }else{
                    alert("Invalid Date Range")
                }
            //     }else{
            //     alert("This name is already taken, please create a new name")
            // }
            }else if(this.game.gameName == ""){
                alert("Enter in a game name");
            }
        },
        

    }
}
</script>

<style>
.aligning{
    display: flex;
    flex-direction: column;
    align-content: space-between;
    align-items: stretch;

    }
.space{
     margin: 5px;
     cursor: default;
}
#createGame{
    align-self: center;
}
.dateCursor{
cursor: pointer;
}

</style>