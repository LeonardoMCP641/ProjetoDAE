<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>Gestão de Utilizadores</h2>
      <NuxtLink to="/users/create" class="btn btn-primary">
        <i class="bi bi-person-plus"></i> Novo Utilizador
      </NuxtLink>
    </div>

    <!-- Barra de Pesquisa -->
    <div class="card mb-4 shadow-sm">
      <div class="card-body">
        <div class="input-group">
          <span class="input-group-text">🔍</span>
          <input
              v-model="searchQuery"
              type="text"
              class="form-control"
              placeholder="Pesquisar por nome, username ou email..."
          >
        </div>
      </div>
    </div>

    <!-- Tabela de Utilizadores -->
    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
        <tr>
          <th>Nome</th>
          <th>Username / Email</th>
          <th>Role</th>
          <th>Estado</th>
          <th class="text-end">Ações</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in filteredUsers" :key="user.username">
          <td>
            <span class="fw-bold">{{ user.name }}</span>
          </td>
          <td>
            <div>{{ user.username }}</div>
            <small class="text-muted">{{ user.email }}</small>
          </td>
          <td>
            <span :class="getRoleBadge(user.role)">{{ user.role }}</span>
          </td>
          <td>
            <span v-if="user.active" class="badge bg-success">Ativo</span>
            <span v-else class="badge bg-danger">Inativo</span>
          </td>
          <td class="text-end">
            <!-- Botão Editar -->
            <NuxtLink :to="`/users/edit/${user.id}`" class="btn btn-sm btn-outline-primary me-2">
              Editar
            </NuxtLink>

            <!-- Botão Ativar/Desativar (Alterna o estado) -->
            <button
                @click="toggleActive(user)"
                class="btn btn-sm me-2"
                :class="user.active ? 'btn-outline-warning' : 'btn-outline-success'"
            >
              {{ user.active ? 'Bloquear' : 'Ativar' }}
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-if="filteredUsers.length === 0" class="text-center py-4 text-muted">
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

// Segurança: Só Admin entra aqui
//definePageMeta({
  //middleware: 'auth'
//});

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

// 2. Computed para a Pesquisa (Filtra a lista localmente)
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
    // Copiamos o user e invertemos o estado
    const updatedUser = { ...user, active: !user.active };

    await $fetch(`${api}/users/${user.id}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: updatedUser
    });

    // Atualiza a lista localmente para ser rápido
    user.active = !user.active;
  } catch (e) {
    alert("Erro ao alterar estado do utilizador.");
  }
}

// Helper para cores das roles
function getRoleBadge(role) {
  switch(role) {
    case 'Administrador': return 'badge bg-danger';
    case 'Responsavel': return 'badge bg-warning text-dark';
    default: return 'badge bg-info text-dark';
  }
}

// Carregar ao iniciar
onMounted(() => {
  if (authStore.user?.role !== 'Administrador') {
    navigateTo('/'); // Expulsa se não for admin
  } else {
    fetchUsers();
  }
});
</script>