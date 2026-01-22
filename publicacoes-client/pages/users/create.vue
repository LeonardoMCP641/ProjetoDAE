
<template>
  <div class="max-w-2xl mx-auto py-10">

    <div class="mb-8 flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">Novo Utilizador</h2>
        <p class="text-gray-500 text-sm">Adicionar um novo membro à equipa.</p>
      </div>
      <button @click="$router.back()" class="text-gray-500 hover:text-blue-600 transition flex items-center text-sm font-medium">
        <i class="bi bi-arrow-left mr-2"></i> Voltar
      </button>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="p-8">

        <form @submit.prevent="createUser" class="space-y-6">

          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">Nome Completo</label>
            <input
                v-model="form.name"
                type="text"
                placeholder="Ex: Matilde Quitério"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                required
            >
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-bold text-gray-700 mb-2">Username</label>
              <input
                  v-model="form.username"
                  type="text"
                  placeholder="Ex: mati05"
                  class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                  required
              >
            </div>
            <div>
              <label class="block text-sm font-bold text-gray-700 mb-2">Email</label>
              <input
                  v-model="form.email"
                  type="email"
                  placeholder="mati@xyz.pt"
                  class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                  required
              >
            </div>
          </div>

          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">Permissão / Role</label>
            <div class="relative">
              <select v-model="form.role" class="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition bg-white appearance-none cursor-pointer" required>
                <option value="Colaborador">Colaborador</option>
                <option value="Responsavel">Responsável</option>
                <option value="Administrador">Administrador</option>
              </select>
              <div class="absolute inset-y-0 right-0 flex items-center px-3 pointer-events-none text-gray-500">
                <i class="bi bi-chevron-down"></i>
              </div>
            </div>
            <p class="text-xs text-gray-400 mt-2">
              <i class="bi bi-info-circle mr-1"></i>
              A password inicial será definida automaticamente pelo sistema.
            </p>
          </div>

          <div class="pt-4 flex gap-4">
            <button
                type="submit"
                class="flex-1 bg-blue-600 text-white font-bold py-3 px-4 rounded-lg hover:bg-blue-700 shadow-md hover:shadow-lg transition transform hover:-translate-y-0.5 flex justify-center items-center"
                :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm mr-2" role="status"></span>
              <span v-else><i class="bi bi-person-plus-fill mr-2"></i> Criar Utilizador</span>
            </button>
          </div>

        </form>

      </div>
    </div>

  </div>
</template>

<script setup>
definePageMeta({
  middleware: ['role'],
  requiredRoles: ['Administrador'],
});

import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from "~/stores/auth-store.js";

const authStore = useAuthStore();
const config = useRuntimeConfig();
const router = useRouter();

const form = reactive({
  username: '',
  name: '',
  email: '',
  role: 'Colaborador',
});

const loading = ref(false);

async function createUser() {
  if (!authStore.token) {
    router.push('/auth/login');
    return;
  }

  loading.value = true;
  try {
    await $fetch(`${config.public.apiBase}/users`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: form
    });

    alert("Utilizador criado com sucesso!");
    router.push('/users');

  } catch (e) {
    console.error(e);
    alert("Erro ao criar: " + (e.data ? e.data : e.message));
  } finally {
    loading.value = false;
  }
}
</script>