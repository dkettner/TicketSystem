import { ref } from "vue";
import { defineStore } from "pinia";
import axios from 'axios';

export const useUserStore = defineStore("user", () => {
  const usersPath = "/users";
  const user = ref({
    id: "",
    email: "",
    name: ""
  });

  const updateUserById = async (id = this.id) => {
    try {
      const getUserResponse = await axios.get(usersPath + '/' + id);
      if (getUserResponse.status != 200) {
        console.log("getUserById failed with:");
        console.log(getUserResponse.status);
        console.log(getUserResponse.data);
      } else {
        user.value.id = getUserResponse.data.id;
        user.value.email = getUserResponse.data.email;
        user.value.name = getUserResponse.data.name;
      }
    } catch(error) {
      console.log(error);
    }
  }
  const updateUserByEmail = async (email = this.email) => {
    try {
      const getUserResponse = await axios.get(usersPath + '?email=' + email);
      if (getUserResponse.status != 200) {
        console.log("getUserByEmail failed with:");
        console.log(getUserResponse.status);
        console.log(getUserResponse.data);
      } else {
        user.value.id = getUserResponse.data[0].id;
        user.value.email = getUserResponse.data[0].email;
        user.value.name = getUserResponse.data[0].name;
      }
    } catch(error) {
      console.log(error);
    }
  }

  return {
    user,
    updateUserById,
    updateUserByEmail
  };
});