<template>
  <div>
    <!-- NAVBAR FIXA NO TOPO -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
      <div class="container">

        <!-- 1. TÍTULO / LOGO (Lado Esquerdo) -->
        <NuxtLink class="navbar-brand fw-bold" to="/">
          <i class="bi bi-journal-text"></i> Publicações Científicas
        </NuxtLink>

        <!-- Botão para Telemóvel (Hambúrguer) -->
        <button
            class="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarNav"
            aria-controls="navbarNav"
            aria-expanded="false"
            aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <!-- 2. ATALHOS (Lado Direito) -->
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto align-items-center">

            <!-- Link Home (Sempre visível) -->
            <li class="nav-item">
              <NuxtLink class="nav-link" active-class="active" to="/">
                Início
              </NuxtLink>
            </li>

            <!-- === SE ESTIVER LOGADO === -->
            <template v-if="token">
              <li class="nav-item">
                <NuxtLink class="nav-link" active-class="active" to="/tags">
                  Gestão Tags
                </NuxtLink>
              </li>

              <!-- Link Admin (Só aparece se for Administrador) -->
              <li class="nav-item" v-if="user?.role === 'Administrador'">
                <NuxtLink class="nav-link" active-class="active" to="/users">
                  Gestão Users
                </NuxtLink>
              </li>

              <!-- Dropdown do Utilizador -->
              <li class="nav-item dropdown ms-lg-3">
                <a
                    class="nav-link dropdown-toggle btn btn-outline-light border-0 text-white"
                    href="#"
                    role="button"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                >
                  👤 {{ user?.username }}
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li>
                    <NuxtLink class="dropdown-item" to="/users/profile">
                      O Meu Perfil
                    </NuxtLink>
                  </li>
                  <li><hr class="dropdown-divider"></li>
                  <li>
                    <a class="dropdown-item text-danger" href="#" @click.prevent="handleLogout">
                      Sair / Logout
                    </a>
                  </li>
                </ul>
              </li>
            </template>

            <!-- === SE NÃO ESTIVER LOGADO === -->
            <template v-else>
              <li class="nav-item ms-lg-3">
                <NuxtLink class="btn btn-light text-primary fw-bold" to="/auth/login">
                  Login
                </NuxtLink>
              </li>
            </template>

          </ul>
        </div>
      </div>
    </nav>

    <!-- 3. CONTEÚDO DAS PÁGINAS (Injetado aqui) -->
    <div class="container py-4">
      <slot />
    </div>

    <!-- Rodapé Simples -->
    <footer class="text-center py-4 text-muted border-top mt-5">
      <small>&copy; 2025 Centro de I&D XYZ - Plataforma DAE</small>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
// import { storeToRefs } from "pinia"; // O Nuxt auto-importa

const authStore = useAuthStore();
const router = useRouter();

// Usamos storeToRefs para que a Navbar mude automaticamente
// quando o user faz login ou logout
const { token, user } = storeToRefs(authStore);

function handleLogout() {
  authStore.logout();
  router.push('/auth/login');
}
</script>

<style scoped>
/* Pequeno ajuste para destacar o link ativo */
.router-link-active {
  font-weight: bold;
}
</style>