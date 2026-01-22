<template>
  <div class="flex items-center justify-center min-h-[80vh] bg-slate-50 py-12 px-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden transform transition-all hover:shadow-2xl">

      <div class="bg-blue-600 p-8 text-center relative overflow-hidden">
        <div class="relative z-10">
          <h2 class="text-3xl font-bold text-white mb-2">Bem-vindo!</h2>
          <p class="text-blue-100 text-sm">Insere as tuas credenciais para aceder.</p>
        </div>
        <div class="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-white opacity-10 rounded-full blur-xl"></div>
      </div>

      <div class="p-8">
        <form @submit.prevent="login">

          <div class="mb-5">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="username">Username</label>
            <div class="relative group">
              <span class="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400 group-focus-within:text-blue-500 transition">
                <i class="bi bi-person text-lg"></i>
              </span>
              <input
                  v-model="username"
                  class="w-full pl-10 pr-3 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-gray-50 focus:bg-white"
                  id="username"
                  type="text"
                  placeholder="Ex: 2021001"
                  required
              >
            </div>
          </div>

          <div class="mb-8">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="password">Password</label>
            <div class="relative group">
              <span class="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400 group-focus-within:text-blue-500 transition">
                <i class="bi bi-lock text-lg"></i>
              </span>
              <input
                  v-model="password"
                  class="w-full pl-10 pr-3 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-gray-50 focus:bg-white"
                  id="password"
                  type="password"
                  placeholder="********"
                  required
              >
            </div>
          </div>

          <div v-if="errorMsg" class="mb-4 p-3 bg-red-50 text-red-600 text-sm rounded-lg flex items-center border border-red-100">
            <i class="bi bi-exclamation-circle-fill mr-2"></i> {{ errorMsg }}
          </div>

          <button
              type="submit"
              class="w-full bg-blue-600 text-white font-bold py-3.5 px-4 rounded-xl hover:bg-blue-700 transform hover:-translate-y-0.5 transition duration-200 shadow-md hover:shadow-lg flex justify-center items-center"
          >
            Entrar
          </button>

        </form>

        <div class="mt-6 text-center">
          <NuxtLink to="/auth/forgot-password" class="text-sm text-blue-500 hover:text-blue-700 font-semibold transition">
            Esqueceste-te da password?
          </NuxtLink>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const password = ref('');
const errorMsg = ref('');

async function login() {
  try {
    const success = await authStore.login(username.value, password.value);
    if (success) {
      router.push('/');
    } else {
      errorMsg.value = "Login falhou. Verifica os dados.";
    }
  } catch (e) {
    errorMsg.value = "Erro de conexão ao servidor.";
  }
}
</script>