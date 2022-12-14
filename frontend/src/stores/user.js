import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";

export const useUserStore = defineStore("user", () => {
  const user = ref({
    id: "",
    email: "", 
    name: ""
  });

  const fetchAgent = useFetchAgent();

  const setEmail = (email) => {
    user.value.email = email;
  }

  const updateUserById = async (id = user.value.id) => {
    const getUserResponse = await fetchAgent.getUserById(id);
    if (getUserResponse.isSuccessful) {
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
      return { isSuccessful: true, data: getUserResponse.data };
    } else {
      return { isSuccessful: false, data: getUserResponse.data.response.data };
    }
  }
  const updateUserByEmail = async (email = user.value.email) => {
    const getUserResponse = await fetchAgent.getUserByEmail(email);
    if (getUserResponse.isSuccessful) {
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
      return { isSuccessful: true, data: getUserResponse.data };
    } else {
      return { isSuccessful: false, data: getUserResponse.data.response.data };
    }
  }

  return {
    user,
    setEmail,
    updateUserById,
    updateUserByEmail
  };
});
