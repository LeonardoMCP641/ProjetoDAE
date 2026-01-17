<template>
  <div class="min-h-screen bg-slate-50 flex items-center justify-center py-12 px-4">
    <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-sm border border-slate-200">

      <div class="text-center">
        <h2 class="text-3xl font-extrabold text-slate-900">Recuperar Acesso</h2>
        <p class="mt-2 text-sm text-slate-500">
          Introduza o seu e-mail para receber uma nova palavra-passe.
        </p>
      </div>

      <form @submit.prevent="handleRecover" class="mt-8 space-y-6">
        <div class="flex flex-col">
          <label class="text-sm font-semibold text-slate-700 mb-1 ml-1">Endereço de E-mail</label>
          <input
              v-model="email"
              type="email"
              required
              placeholder="exemplo@centro.pt"
              class="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-4 focus:ring-blue-100 focus:border-blue-500 outline-none transition-all"
          />
          <p v-if="emailError" class="text-xs text-red-500 mt-1 ml-1">{{ emailError }}</p>
        </div>

        <button
            type="submit"
            :disabled="loading || !!emailError"
            class="w-full py-3.5 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-md transition-all active:scale-[0.98] disabled:opacity-50 flex justify-center items-center"
        >
          <svg v-if="loading" class="animate-spin h-4 w-4 mr-2 border-2 border-white border-t-transparent rounded-full" viewBox="0 0 24 24"></svg>
          {{ loading ? 'A processar...' : 'Enviar Nova Password' }}
        </button>
      </form>

      <div class="text-center mt-4">
        <NuxtLink to="/auth/login" class="text-sm font-medium text-blue-600 hover:text-blue-500">
          Voltar ao Login
        </NuxtLink>
      </div>

      <p v-if="statusMessage"
         :class="isError ? 'text-red-600 bg-red-50' : 'text-emerald-700 bg-emerald-50'"
         class="mt-4 p-3 rounded-lg text-sm text-center font-medium">
        {{ statusMessage }}
      </p>
    </div>
  </div>
</template>

<script setup>
const config = useRuntimeConfig();
const email = ref('');
const loading = ref(false);
const statusMessage = ref('');
const isError = ref(false);

// Validação simples (Ficha 6, Parte I)
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
    statusMessage.value = data; // "Uma nova password foi enviada..."
    email.value = ''; // Limpar campo
  } catch (err) {
    isError.value = true;
    statusMessage.value = err.data || "Erro ao recuperar password. Verifique o e-mail.";
  } finally {
    loading.value = false;
  }
}
</script>