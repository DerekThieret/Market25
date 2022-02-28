<template>
  <div id="register" class="text-center">
    <form class="form-register" @submit.prevent="register">
      <h1 class="h3 mb-3 font-weight-normal">Register</h1>
      <div class="alert alert-danger" role="alert" v-if="registrationErrors">
        {{ registrationErrorMsg }}
      </div>


      <!-- DO NOT NEED THE LABEL: EMAIL USERNAME, ETC.  Sam took them out for now  -->
      <!-- "Home" is in the app.vue -->

      
      <label for="email" class="sr-only" id="email"></label>
      <h4>Please enter a valid email: </h4>
      <div class="email">
      <input
        type="email" 
        pattern="[A-Za-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$"
        id="email"
        class="form-control"
        placeholder="Email"
        v-model="user.email"
        required
        autofocus
      />
      </div>
      <div id="username">
        <label for="username" class="sr-only" ></label>
        <input
          type="text"
          id="username"
          class="form-control"
          placeholder="Username"
          v-model="user.username"
          required
          autofocus
        />
      </div>

      
        <label for="password" class="sr-only"></label>
          <div class="passFirst">
        <input
          type="password"
          id="password"
          class="form-control"
          placeholder="Password"
          v-model="user.password"
          required
          />
        </div>
        <div class="confirmPasswordClass">
          <input
            type="password"
            id="confirmPassword"
            class="form-control"
            placeholder="Confirm Password"
            v-model="user.confirmPassword"
            required
            />
      </div>
      <button class="btn btn-lg btn-primary btn-block" type="submit">
        Create Account
      </button>
       <div class="account">
        <router-link :to="{ name: 'login' }">Already have an account? </router-link>
      </div>
    </form>
  </div>
</template>

<script>
import authService from '../services/AuthService';

export default {
  name: 'register',
  
  data() {
    return {
      user: {
        username: '',
        password: '',
        confirmPassword: '',
        role: 'user',
      },
      registrationErrors: false,
      registrationErrorMsg: 'There were problems registering this user.',
    };
  },
  methods: {
    register() {
      if (this.user.password != this.user.confirmPassword) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password & Confirm Password do not match.';
      } else {
        authService
          .register(this.user)
          .then((response) => {
            if (response.status == 201) {
              this.$router.push({
                path: '/login',
                query: { registration: 'success' },
              });
            }
          })
          .catch((error) => {
            const response = error.response;
            this.registrationErrors = true;
            if (response.status === 400) {
              this.registrationErrorMsg = 'Bad Request: Validation Errors';
            }
          });
      }
    },
    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg = 'There were problems registering this user.';
    },
  },
};
</script>

<style scoped>
/* "Create Account" */
.h3{
  grid-area: create;
  color: #2c3e50;
  margin-top: 15px;
  color:black;
}

.alert{
  grid-area: alert;
}

/* Email field */
.email{
  grid-area: email;
  margin-top: 10px;
}

/* adjusts the password field */
.passFirst{
  display: flex;
  grid-area: password;
  margin-top: 10px;
  justify-content: center;
}
.form-register{
 border: black;
  border-style: solid;
  padding: 5px;
  width: 500px;
  height: 400px;
  background-color: rgb(233, 233, 233);}
/* adjusts the confirm password field*/
.confirmPasswordClass{ 
  grid-area: confirmPassword;
  margin-bottom: 10px;
}
#username{ 
  grid-area: username;
  margin-top: 5px
}

.btn{
  grid-area: button;
}
#register{
  grid-area: register;
}
.account{
  display: flex;
  margin-top: 10px;
  justify-content: center;
}
#register{
  /* display: grid; */
  display: flex;
    grid-template-columns:  1fr;
    text-align: center;
    /* grid-template-areas: 
        "register",
        "create",
        "alert",
        "email",
        "username",
        "initialPassword",
        "confirmPassword",
        "account",
        "button"; */
        justify-content: center;
      
}



</style>
