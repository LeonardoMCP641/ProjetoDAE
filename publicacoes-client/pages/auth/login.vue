<template>
  <div class="flex items-center justify-center min-h-[80vh]">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">

      <div class="bg-blue-600 p-8 text-center">
        <h2 class="text-3xl font-bold text-white mb-2">Bem-vindo!</h2>
        <p class="text-blue-100 text-sm">Insere as tuas credenciais para aceder à plataforma.</p>
      </div>

      <div class="p-8">
        <form @submit.prevent="login">

          <div class="mb-5">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="username">Username</label>
            <div class="relative">
              <span class="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
                <i class="bi bi-person"></i>
              </span>
              <input
                  v-model="username"
                  class="w-full pl-10 pr-3 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                  id="username"
                  type="text"
                  placeholder="Ex: 2021001"
                  required
              >
            </div>
          </div>

          <div class="mb-8">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="password">Password</label>
            <div class="relative">
              <span class="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
                <i class="bi bi-lock"></i>
              </span>
              <input
                  v-model="password"
                  class="w-full pl-10 pr-3 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                  id="password"
                  type="password"
                  placeholder="********"
                  required
              >
            </div>
          </div>

          <div v-if="errorMsg" class="mb-4 p-3 bg-red-50 text-red-600 text-sm rounded-lg flex items-center">
            <i class="bi bi-exclamation-circle-fill mr-2"></i> {{ errorMsg }}
          </div>

          <button
              type="submit"
              class="w-full bg-blue-600 text-white font-bold py-3 px-4 rounded-lg hover:bg-blue-700 transform hover:-translate-y-0.5 transition duration-200 shadow-md hover:shadow-lg"
          >
            Entrar
          </button>

        </form>

        <div class="mt-6 text-center">
          <a href="#" class="text-sm text-blue-500 hover:text-blue-700 font-semibold">Esqueceste-te da password?</a>
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
      router.push('/'); // Vai para a Dashboard
    } else {
      errorMsg.value = "Login falhou. Verifica os dados.";
    }
  } catch (e) {
    errorMsg.value = "Erro de conexão ao servidor.";
  }
}
</script>