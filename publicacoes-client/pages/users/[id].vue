<template>
  <div class="container mt-4">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow-sm">
          <div class="card-header bg-white">
            <h4>Editar Utilizador #{{ userId }}</h4>
          </div>
          <div class="card-body">

            <form v-if="user" @submit.prevent="updateUser">

              <div class="mb-3">
                <label class="form-label">Nome</label>
                <input v-model="user.name" type="text" class="form-control" required>
              </div>

              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Username</label>
                  <input v-model="user.username" type="text" class="form-control" required>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Email</label>
                  <input v-model="user.email" type="email" class="form-control" required>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Role</label>
                <select v-model="user.role" class="form-select">
                  <option value="Colaborador">Colaborador</option>
                  <option value="Responsavel">Responsável</option>
                  <option value="Administrador">Administrador</option>
                </select>
              </div>

              <div class="form-check form-switch mb-4">
                <input v-model="user.active" class="form-check-input" type="checkbox" id="activeSwitch">
                <label class="form-check-label" for="activeSwitch">Conta Ativa</label>
              </div>

              <div class="d-flex justify-content-between">
                <NuxtLink to="/users" class="btn btn-secondary">Cancelar</NuxtLink>
                <button type="submit" class="btn btn-primary">Guardar Alterações</button>
              </div>
            </form>

            <div v-else class="text-center py-5">
              <div class="spinner-border text-primary" role="status"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const config = useRuntimeConfig();

// 1. Pegar o ID da rota (ex: /users/edit/5 -> userId = 5)
const userId = route.params.id;
const user = ref(null);

// 2. Carregar dados usando o ID
onMounted(async () => {
  try {
    // Backend deve ter endpoint: GET /api/users/{id}
    const data = await $fetch(`${config.public.apiBase}/users/${userId}`, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    });
    user.value = data;
  } catch (e) {
    alert("Erro ao carregar utilizador.");
    router.push('/users');
  }
});

// 3. Atualizar dados usando o ID
async function updateUser() {
  try {
    // Backend deve ter endpoint: PUT /api/users/{id}
    await $fetch(`${config.public.apiBase}/users/${userId}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: user.value
    });

    alert("Utilizador atualizado com sucesso!");
    router.push('/users');

  } catch (e) {
    alert("Erro ao atualizar: " + (e.data || e.message));
  }
}
</script>