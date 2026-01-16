<template>
  <div class="container mt-4">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow-sm">
          <div class="card-header bg-white">
            <h4 class="mb-0">Novo Utilizador</h4>
          </div>
          <div class="card-body">
            <form @submit.prevent="createUser">

              <div class="mb-3">
                <label class="form-label">Nome Completo</label>
                <input v-model="form.name" type="text" class="form-control" required>
              </div>

              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Username</label>
                  <input v-model="form.username" type="text" class="form-control" required>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Email</label>
                  <input v-model="form.email" type="email" class="form-control" required>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Role (Permissão)</label>
                <select v-model="form.role" class="form-select" required>
                  <option value="Colaborador">Colaborador</option>
                  <option value="Responsavel">Responsável</option>
                  <option value="Administrador">Administrador</option>
                </select>
              </div>

              <div class="d-flex justify-content-between mt-4">
                <NuxtLink to="/users" class="btn btn-secondary">Cancelar</NuxtLink>
                <button type="submit" class="btn btn-primary" :disabled="loading">
                  Criar Utilizador
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
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
  loading.value = true;
  try {
    await $fetch(`${config.public.apiBase}/users`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: form
    });

    alert("Utilizador criado com sucesso!");
    router.push('/users'); // Volta para a lista

  } catch (e) {
    alert("Erro ao criar: " + (e.data || e.message));
  } finally {
    loading.value = false;
  }
}
</script>