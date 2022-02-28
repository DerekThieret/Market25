<template>
  <div id="app">
    <div id="nav">
      <router-link id="navBarLinkImage" v-bind:to="{ name: 'home' }"> <img id="homeImage" src="../images/homeImage.jpg" alt="Home" style="width:40px;height:40px; border-radius:10px;"></router-link>&nbsp;&nbsp;
     
      <router-link id="navBarLinkLogout" v-bind:to="{ name: 'logout' }" v-if="$store.state.token != ''">Logout</router-link>
    </div>
    <router-view />
  </div>
</template>

<style>
div#nav{
display: flex;
justify-content: space-between;
overflow: hidden;
}
#navBarLinkImage, #navBarLinkLogout{
  align-self: center;
  margin-top: 5px;
}
#navBarLinkLogout{
margin-right: 10px;
}
#navBarLinkImage{
margin-left: 10px;
justify-self: right;
cursor: pointer;
}
#navBarLinkImage:hover{
  transform: scale(1.1) ;
}
#navBarLinkLogout:hover{
  transform: scale(1.02);
  
}

html{
  font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
  
  background-blend-mode: soft-light;
  /* background-blend-mode: overlay; */
  /* background-blend-mode: lighten; */
  /* background-blend-mode: color-dodge; */
  /* background-blend-mode: color-burn; */
  background-image: url('../images/stockBackground.jpg');
  background-color: lightblue;
  background-size: cover;
  
}

</style>