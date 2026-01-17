<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-4">

        <div class="card shadow">
          <div class="card-header bg-primary text-white text-center">
            <h3 class="mb-0">Entrar</h3>
          </div>

          <div class="card-body p-4">
            <form @submit.prevent="handleLogin">

              <!-- Campo Username -->
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input
                    v-model="username"
                    type="text"
                    class="form-control"
                    id="username"
                    placeholder="Ex: admin"
                    required
                    autofocus
                />
              </div>

              <!-- Campo Password -->
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input
                    v-model="password"
                    type="password"
                    class="form-control"
                    id="password"
                    required
                />
              </div>

              <!-- Mensagem de Erro -->
              <div v-if="errorMessage" class="alert alert-danger text-center" role="alert">
                {{ errorMessage }}
              </div>

              <!-- Botão de Login -->
              <div class="d-grid gap-2">
                <button type="submit" class="btn btn-primary" :disabled="loading">
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ loading ? 'A entrar...' : 'Login' }}
                </button>
              </div>

            </form>
          </div>

          <div class="card-footer text-center py-3">
            <NuxtLink to="/auth/forgot-password" class="text-decoration-none">
              <small class="text-primary fw-bold">Esqueceu-se da password? Clique aqui.</small>
            </NuxtLink>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '~/stores/auth-store';

const authStore = useAuthStore();
const username = ref('');
const password = ref('');
const errorMessage = ref('');
const loading = ref(false);

async function handleLogin() {
  loading.value = true;
  errorMessage.value = '';

  try {
    // Chama a ação de login da Store
    await authStore.login(username.value, password.value);

    // Se correr bem, o redirecionamento é feito dentro da store
  } catch (error) {
    // Se falhar (ex: password errada), mostra mensagem
    errorMessage.value = "Username ou Password incorretos.";
  } finally {
    loading.value = false;
  }
}
</script>