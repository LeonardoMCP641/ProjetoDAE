<template>
  <div class="flex items-center justify-center min-h-[80vh] bg-slate-50 py-12 px-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden transform transition-all hover:shadow-2xl">

      <div class="bg-blue-600 p-8 text-center relative overflow-hidden">
        <div class="relative z-10">
          <h2 class="text-3xl font-bold text-white mb-2">Recuperar Acesso</h2>
          <p class="text-blue-100 text-sm">Vamos enviar-te uma nova password.</p>
        </div>
        <div class="absolute bottom-0 left-0 -mb-4 -ml-4 w-24 h-24 bg-white opacity-10 rounded-full blur-xl"></div>
      </div>

      <div class="p-8">
        <form @submit.prevent="handleRecover" class="space-y-6">

          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2" for="email">Endereço de E-mail</label>
            <div class="relative group">
              <span class="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400 group-focus-within:text-blue-500 transition">
                <i class="bi bi-envelope text-lg"></i>
              </span>
              <input
                  v-model="email"
                  id="email"
                  type="email"
                  required
                  placeholder="exemplo@centro.pt"
                  class="w-full pl-10 pr-3 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-gray-50 focus:bg-white"
                  :class="{'border-red-300 ring-red-200': emailError}"
              />
            </div>
            <p v-if="emailError" class="text-xs text-red-500 mt-1 ml-1 flex items-center">
              <i class="bi bi-x-circle mr-1"></i> {{ emailError }}
            </p>
          </div>

          <button
              type="submit"
              :disabled="loading || !!emailError"
              class="w-full bg-blue-600 text-white font-bold py-3.5 px-4 rounded-xl hover:bg-blue-700 transform hover:-translate-y-0.5 transition duration-200 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex justify-center items-center"
          >
            <svg v-if="loading" class="animate-spin h-5 w-5 mr-2 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loading ? 'A processar...' : 'Enviar Nova Password' }}
          </button>
        </form>

        <div class="mt-6 text-center">
          <NuxtLink to="/auth/login" class="text-sm text-gray-500 hover:text-blue-600 font-medium transition flex items-center justify-center">
            <i class="bi bi-arrow-left mr-1"></i> Voltar ao Login
          </NuxtLink>
        </div>

        <div v-if="statusMessage"
             :class="isError ? 'bg-red-50 text-red-600 border-red-100' : 'bg-emerald-50 text-emerald-700 border-emerald-100'"
             class="mt-6 p-4 rounded-xl text-sm text-center font-medium border flex items-center justify-center animate-pulse">
          <i :class="isError ? 'bi-exclamation-triangle-fill' : 'bi-check-circle-fill'" class="mr-2 text-lg"></i>
          {{ statusMessage }}
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();
const email = ref('');
const loading = ref(false);
const statusMessage = ref('');
const isError = ref(false);

const emailError = computed(() => {
  if (!email.value) return null;
  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return pattern.test(email.value) ? null : 'Formato de e-mail inválido';
});

async function handleRecover() {
  loading.value = true;
  statusMessage.value = '';

  try {
    const data = await $fetch(`${config.public.apiBase}/users/forgot-password`, {
      method: 'POST',
      body: { email: email.value }
    });

    isError.value = false;
    statusMessage.value = data;
    email.value = '';
  } catch (err) {
    isError.value = true;
    statusMessage.value = err.response?._data || "Erro ao recuperar password. Verifique o e-mail.";
  } finally {
    loading.value = false;
  }
}
</script>