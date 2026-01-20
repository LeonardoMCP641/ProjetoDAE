<template>
  <div class="min-h-screen bg-gray-50 font-sans">
    <!-- NAVBAR FIXA -->
    <nav class="bg-blue-700 shadow-md sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">

          <!-- Lado Esquerdo: Logo -->
          <div class="flex items-center">
            <NuxtLink to="/" class="flex-shrink-0 flex items-center text-white font-bold text-xl tracking-tight">
              <span class="bg-white text-blue-700 p-1 rounded mr-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </span>
              Publicações
            </NuxtLink>

            <!-- Links Desktop (Esquerda) -->
            <div class="hidden md:ml-8 md:flex md:space-x-4">
              <NuxtLink to="/" class="nav-link">Início</NuxtLink>
              <template v-if="token">
                <NuxtLink to="/tags" class="nav-link">Gestão Tags</NuxtLink>
                <NuxtLink v-if="user?.role === 'Administrador'" to="/users" class="nav-link">Gestão Users</NuxtLink>
              </template>
            </div>
          </div>

          <!-- Lado Direito: Auth / User -->
          <div class="hidden md:flex items-center space-x-4">
            <template v-if="token">
              <!-- Perfil Dropdown -->
              <div class="relative ml-3">
                <button
                    @click="isUserMenuOpen = !isUserMenuOpen"
                    class="flex items-center text-white bg-blue-800 hover:bg-blue-900 px-3 py-2 rounded-lg text-sm font-medium transition"
                >
                  <span class="mr-2">👤 {{ user?.username }}</span>
                  <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                </button>

                <!-- Menu Dropdown Desktop -->
                <div v-if="isUserMenuOpen" @click.away="isUserMenuOpen = false" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 ring-1 ring-black ring-opacity-5 z-50">
                  <NuxtLink to="/users/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">O Meu Perfil</NuxtLink>
                  <div class="border-t border-gray-100"></div>
                  <button @click="handleLogout" class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50">Sair / Logout</button>
                </div>
              </div>
            </template>

            <template v-else>
              <NuxtLink to="/auth/login" class="bg-white text-blue-700 hover:bg-blue-50 px-4 py-2 rounded-lg font-bold transition">
                Login
              </NuxtLink>
            </template>
          </div>

          <!-- Botão Mobile -->
          <div class="flex md:hidden items-center">
            <button @click="isMobileMenuOpen = !isMobileMenuOpen" class="text-white hover:text-blue-200">
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path v-if="!isMobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7" />
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Menu Mobile Aberto -->
      <div v-if="isMobileMenuOpen" class="md:hidden bg-blue-800 border-t border-blue-600">
        <div class="px-2 pt-2 pb-3 space-y-1">
          <NuxtLink to="/" class="mobile-nav-link">Início</NuxtLink>
          <template v-if="token">
            <NuxtLink to="/tags" class="mobile-nav-link">Gestão Tags</NuxtLink>
            <NuxtLink v-if="user?.role === 'Administrador'" to="/users" class="mobile-nav-link">Gestão Users</NuxtLink>
            <NuxtLink to="/users/profile" class="mobile-nav-link text-blue-200">O Meu Perfil</NuxtLink>
            <button @click="handleLogout" class="block w-full text-left px-3 py-2 text-red-300 font-medium italic">Sair</button>
          </template>
          <NuxtLink v-else to="/auth/login" class="mobile-nav-link font-bold">Login</NuxtLink>
        </div>
      </div>
    </nav>

    <!-- CONTEÚDO PRINCIPAL -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <slot />
    </main>

    <!-- FOOTER -->
    <footer class="bg-white border-t border-gray-200 py-8 mt-auto">
      <div class="text-center text-gray-500 text-sm">
        <p>&copy; 2025 Centro de I&D XYZ - Plataforma DAE</p>
        <p class="mt-1 font-semibold text-blue-600">Engenharia Informática - IPLeiria</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const router = useRouter();
const { token, user } = storeToRefs(authStore);

const isMobileMenuOpen = ref(false);
const isUserMenuOpen = ref(false);

function handleLogout() {
  isUserMenuOpen.value = false;
  authStore.logout();
  router.push('/auth/login');
}

// Fechar menus ao mudar de rota
watch(() => router.currentRoute.value.fullPath, () => {
  isMobileMenuOpen.value = false;
  isUserMenuOpen.value = false;
});
</script>

<style scoped>
.nav-link {
  @apply text-blue-100 hover:text-white hover:bg-blue-600 px-3 py-2 rounded-md text-sm font-medium transition;
}

/* Estilo para NuxtLink ativo */
.router-link-active {
  @apply bg-blue-800 text-white font-bold;
}

.mobile-nav-link {
  @apply block px-3 py-2 rounded-md text-base font-medium text-white hover:bg-blue-700 transition;
}
</style>