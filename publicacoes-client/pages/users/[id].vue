<template>
  <div class="max-w-2xl mx-auto py-10">

    <div class="mb-8 flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">Editar Utilizador</h2>
        <p class="text-gray-500 text-sm">Atualizar dados e permissões.</p>
      </div>
      <button @click="$router.back()" class="text-gray-500 hover:text-blue-600 transition flex items-center text-sm font-medium">
        <i class="bi bi-arrow-left mr-2"></i> Voltar
      </button>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">

      <div v-if="user" class="p-8">
        <form @submit.prevent="updateUser" class="space-y-6">

          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-200">
            <span class="text-xs font-bold text-gray-500 uppercase tracking-wider">ID do Utilizador</span>
            <span class="font-mono text-sm font-bold text-gray-700">#{{ userId }}</span>
          </div>

          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">Nome Completo</label>
            <input
                v-model="user.name"
                type="text"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                required
            >
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-bold text-gray-700 mb-2">Username</label>
              <input
                  v-model="user.username"
                  type="text"
                  class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition bg-gray-50 text-gray-500 cursor-not-allowed"
                  title="O username não deve ser alterado frequentemente"
                  readonly
              >
            </div>
            <div>
              <label class="block text-sm font-bold text-gray-700 mb-2">Email</label>
              <input
                  v-model="user.email"
                  type="email"
                  class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                  required
              >
            </div>
          </div>

          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">Cargo / Role</label>
            <div class="relative">
              <select v-model="user.role" class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition bg-white appearance-none">
                <option value="Colaborador">Colaborador</option>
                <option value="Responsavel">Responsável</option>
                <option value="Administrador">Administrador</option>
              </select>
              <div class="absolute inset-y-0 right-0 flex items-center px-3 pointer-events-none text-gray-500">
                <i class="bi bi-chevron-down"></i>
              </div>
            </div>
          </div>

          <div class="flex items-center justify-between p-4 bg-blue-50 rounded-lg border border-blue-100">
            <div>
              <span class="block text-sm font-bold text-gray-800">Estado da Conta</span>
              <span class="text-xs text-gray-500">Contas inativas não podem fazer login.</span>
            </div>

            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" v-model="user.active" class="sr-only peer">
              <div class="w-11 h-6 bg-gray-300 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
            </label>
          </div>

          <div class="pt-4 flex gap-4">
            <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-4 rounded-lg hover:bg-blue-700 shadow-md hover:shadow-lg transition transform hover:-translate-y-0.5">
              Guardar Alterações
            </button>
          </div>

        </form>
      </div>

      <div v-else class="text-center py-20">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mx-auto mb-4"></div>
        <p class="text-gray-400">A carregar dados...</p>
      </div>

    </div>
  </div>
</template>

<script setup>
definePageMeta({
  middleware: ['role'],
  requiredRoles: ['Administrador'],
});
import { ref, onMounted } from 'vue';
import { useAuthStore } from "~/stores/auth-store.js";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const config = useRuntimeConfig();

const userId = route.params.id;
const user = ref(null);

onMounted(async () => {
  if (!authStore.token) {
    router.push('/auth/login');
    return;
  }

  try {
    const data = await $fetch(`${config.public.apiBase}/users/${userId}`, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
    user.value = data;
  } catch (e) {
    console.error(e);
    alert("Erro ao carregar utilizador.");
    router.push('/users'); // Volta à lista se falhar
  }
});

async function updateUser() {
  try {
    await $fetch(`${config.public.apiBase}/users/${userId}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: user.value
    });

    alert("Utilizador atualizado com sucesso!");
    router.push('/users');

  } catch (e) {
    console.error(e);
    alert("Erro ao atualizar: " + (e.data ? e.data : e.message));
  }
}
</script>