<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

    <div class="flex flex-col sm:flex-row justify-between items-center mb-8 gap-4">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">Gestão de Utilizadores</h2>
        <p class="text-sm text-gray-500">Administra os acessos e permissões da plataforma.</p>
      </div>

      <NuxtLink
          to="/users/create"
          class="inline-flex items-center px-5 py-2.5 border border-transparent rounded-xl shadow-sm text-sm font-bold text-white bg-blue-600 hover:bg-blue-700 transition transform hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        <i class="bi bi-person-plus-fill mr-2 text-lg"></i> Novo Utilizador
      </NuxtLink>
    </div>

    <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 mb-6">
      <div class="relative">
        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
          <i class="bi bi-search"></i>
        </div>
        <input
            v-model="searchQuery"
            type="text"
            class="block w-full pl-10 pr-3 py-2.5 border border-gray-200 rounded-lg leading-5 bg-gray-50 placeholder-gray-400 focus:outline-none focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition sm:text-sm"
            placeholder="Pesquisar por nome, username ou email..."
        >
      </div>
    </div>

    <div class="bg-white shadow-sm rounded-xl border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50/50">
          <tr>
            <th scope="col" class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Utilizador</th>
            <th scope="col" class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Role (Cargo)</th>
            <th scope="col" class="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Estado</th>
            <th scope="col" class="relative px-6 py-4 text-right">
              <span class="sr-only">Ações</span>
            </th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-100">
          <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-blue-50/30 transition-colors group">

            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10">
                  <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-sm border-2 border-white shadow-sm">
                    {{ getInitials(user.name) }}
                  </div>
                </div>
                <div class="ml-4">
                  <div class="text-sm font-bold text-gray-900">{{ user.name }}</div>
                  <div class="text-xs text-gray-500">@{{ user.username }} | {{ user.email }}</div>
                </div>
              </div>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getRoleBadge(user.role)" class="px-3 py-1 inline-flex text-xs leading-5 font-bold rounded-full border">
                  {{ user.role }}
                </span>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
                <span
                    v-if="user.active"
                    class="px-2.5 py-0.5 inline-flex text-xs font-medium rounded-full bg-green-50 text-green-700 border border-green-200"
                >
                  <i class="bi bi-check-circle-fill mr-1.5 mt-0.5"></i> Ativo
                </span>
              <span
                  v-else
                  class="px-2.5 py-0.5 inline-flex text-xs font-medium rounded-full bg-gray-100 text-gray-600 border border-gray-200"
              >
                  <i class="bi bi-dash-circle-fill mr-1.5 mt-0.5"></i> Inativo
                </span>
            </td>

            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <div class="flex justify-end gap-2 opacity-80 group-hover:opacity-100 transition-opacity">

                <NuxtLink
                    :to="`/users/edit/${user.id}`"
                    class="p-2 text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition"
                    title="Editar Dados"
                >
                  <i class="bi bi-pencil-square"></i>
                </NuxtLink>

                <button
                    @click="toggleActive(user)"
                    class="p-2 rounded-lg transition"
                    :class="user.active ? 'text-yellow-600 bg-yellow-50 hover:bg-yellow-100' : 'text-green-600 bg-green-50 hover:bg-green-100'"
                    :title="user.active ? 'Bloquear Conta' : 'Reativar Conta'"
                >
                  <i class="bi" :class="user.active ? 'bi-lock-fill' : 'bi-unlock-fill'"></i>
                </button>

                <button
                    @click="deleteUser(user)"
                    class="p-2 text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition"
                    title="Eliminar Permanentemente"
                >
                  <i class="bi bi-trash-fill"></i>
                </button>

              </div>
            </td>

          </tr>
          </tbody>
        </table>
      </div>

      <div v-if="filteredUsers.length === 0" class="text-center py-16">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
          <i class="bi bi-search text-2xl text-gray-400"></i>
        </div>
        <h3 class="text-lg font-medium text-gray-900">Nenhum utilizador encontrado</h3>
        <p class="mt-1 text-gray-500">Tenta ajustar a tua pesquisa ou adiciona um novo utilizador.</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from "~/stores/auth-store.js";

const authStore = useAuthStore();
const config = useRuntimeConfig();
const api = config.public.apiBase;

const users = ref([]);
const searchQuery = ref('');

async function fetchUsers() {
  try {
    const data = await $fetch(`${api}/users`, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
    users.value = data;
  } catch (e) {
    console.error("Erro ao carregar users", e);
  }
}

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value;
  const lowerSearch = searchQuery.value.toLowerCase();

  return users.value.filter(u =>
      (u.name && u.name.toLowerCase().includes(lowerSearch)) ||
      (u.username && u.username.toLowerCase().includes(lowerSearch)) ||
      (u.email && u.email.toLowerCase().includes(lowerSearch))
  );
});

function getInitials(name) {
  if (!name) return '?';
  const parts = name.split(' ');
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

function getRoleBadge(role) {
  switch(role) {
    case 'Administrador':
      return 'bg-purple-50 text-purple-700 border-purple-200';
    case 'Responsavel':
      return 'bg-orange-50 text-orange-700 border-orange-200';
    default:
      return 'bg-blue-50 text-blue-700 border-blue-200';
  }
}

// 5. Ações Lógicas
async function toggleActive(user) {
  if (!confirm(`Tem a certeza que deseja ${user.active ? 'bloquear' : 'reativar'} o acesso de ${user.name}?`)) return;

  try {
    const updatedUser = { ...user, active: !user.active };

    await $fetch(`${api}/users/${user.id}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: updatedUser
    });

    user.active = !user.active; // Atualiza a UI
  } catch (e) {
    alert("Erro ao alterar estado do utilizador.");
  }
}

async function deleteUser(user) {
  const confirmMessage = `ATENÇÃO PERIGO ⚠️\n\nIsto irá apagar permanentemente o utilizador "${user.name}" e todos os dados associados.\n\nTem a certeza absoluta?`;

  if (!confirm(confirmMessage)) return;

  try {
    await $fetch(`${api}/users/${user.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authStore.token}` }
    });

    users.value = users.value.filter(u => u.id !== user.id);

  } catch (e) {
    const errorMsg = e.data ? e.data : "Erro ao apagar utilizador (pode ter dados associados).";
    alert(errorMsg);
  }
}

onMounted(() => {
  if (authStore.user?.role !== 'Administrador') {
    navigateTo('/');
  } else {
    fetchUsers();
  }
});
</script>