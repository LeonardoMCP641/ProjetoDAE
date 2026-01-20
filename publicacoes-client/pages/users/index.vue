<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

    <!-- Cabeçalho -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-900">Gestão de Utilizadores</h2>
      <NuxtLink
          to="/users/create"
          class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        <i class="bi bi-person-plus mr-2"></i> Novo Utilizador
      </NuxtLink>
    </div>

    <!-- Barra de Pesquisa -->
    <div class="bg-white shadow rounded-lg mb-6">
      <div class="p-4">
        <div class="relative rounded-md shadow-sm">
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <span class="text-gray-500 sm:text-sm">🔍</span>
          </div>
          <input
              v-model="searchQuery"
              type="text"
              class="focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 sm:text-sm border-gray-300 rounded-md py-2 border"
              placeholder="Pesquisar por nome, username ou email..."
          >
        </div>
      </div>
    </div>

    <!-- Tabela de Utilizadores -->
    <div class="bg-white shadow overflow-hidden sm:rounded-lg border border-gray-200">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Nome
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Username / Email
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Role
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Estado
            </th>
            <th scope="col" class="relative px-6 py-3 text-right">
              <span class="sr-only">Ações</span>
            </th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in filteredUsers" :key="user.username" class="hover:bg-gray-50 transition-colors">

            <!-- Nome -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900">{{ user.name }}</div>
            </td>

            <!-- Username e Email -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-900">{{ user.username }}</div>
              <div class="text-sm text-gray-500">{{ user.email }}</div>
            </td>

            <!-- Role (Badge) -->
            <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getRoleBadge(user.role)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                  {{ user.role }}
                </span>
            </td>

            <!-- Estado -->
            <td class="px-6 py-4 whitespace-nowrap">
                <span
                    v-if="user.active"
                    class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800"
                >
                  Ativo
                </span>
              <span
                  v-else
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800"
              >
                  Inativo
                </span>
            </td>

            <!-- Ações -->
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">

              <!-- Botão Editar -->
              <NuxtLink
                  :to="`/users/edit/${user.id}`"
                  class="text-blue-600 hover:text-blue-900 inline-block"
                  title="Editar"
              >
                Editar
              </NuxtLink>

              <!-- Botão Ativar/Desativar -->
              <button
                  @click="toggleActive(user)"
                  class="font-medium focus:outline-none"
                  :class="user.active ? 'text-yellow-600 hover:text-yellow-900' : 'text-green-600 hover:text-green-900'"
              >
                {{ user.active ? 'Desativar' : 'Ativar' }}
              </button>

              <!-- Botão Apagar -->
              <button
                  @click="deleteUser(user)"
                  class="text-red-600 hover:text-red-900 focus:outline-none"
                  title="Apagar"
              >
                <i class="bi bi-trash text-lg"></i>
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Empty State -->
      <div v-if="filteredUsers.length === 0" class="text-center py-10 text-gray-500">
        Nenhum utilizador encontrado.
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
const authStore = useAuthStore();
const config = useRuntimeConfig();
const api = config.public.apiBase;

const users = ref([]);
const searchQuery = ref('');

// 1. Fetch de todos os utilizadores
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

// 2. Computed para a Pesquisa
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value;
  const lowerSearch = searchQuery.value.toLowerCase();

  return users.value.filter(u =>
      u.name.toLowerCase().includes(lowerSearch) ||
      u.username.toLowerCase().includes(lowerSearch) ||
      u.email.toLowerCase().includes(lowerSearch)
  );
});

// 3. Ativar/Desativar Utilizador
async function toggleActive(user) {
  if (!confirm(`Tem a certeza que deseja ${user.active ? 'bloquear' : 'ativar'} o utilizador ${user.name}?`)) return;

  try {
    const updatedUser = { ...user, active: !user.active };

    await $fetch(`${api}/users/${user.id}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: updatedUser
    });

    user.active = !user.active;
  } catch (e) {
    alert("Erro ao alterar estado do utilizador.");
  }
}

// 4. Apagar Utilizador
async function deleteUser(user) {
  const confirmMessage = `ATENÇÃO: Isto irá apagar permanentemente o utilizador "${user.name}".\n\nTem a certeza?`;

  if (!confirm(confirmMessage)) return;

  try {
    await $fetch(`${api}/users/${user.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authStore.token}` }
    });

    users.value = users.value.filter(u => u.id !== user.id);

  } catch (e) {
    const errorMsg = e.data || "Erro ao apagar utilizador.";
    alert(errorMsg);
  }
}

// --- MUDANÇA AQUI: Cores adaptadas para Tailwind ---
function getRoleBadge(role) {
  switch(role) {
    case 'Administrador':
      return 'bg-red-100 text-red-800';
    case 'Responsavel':
      return 'bg-yellow-100 text-yellow-800';
    default:
      return 'bg-blue-100 text-blue-800';
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