<template>
  <div class="min-h-screen bg-gray-50 font-sans">
    <nav class="bg-blue-700 shadow-md sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">

          <div class="flex items-center">
            <NuxtLink to="/" class="flex-shrink-0 flex items-center text-white font-bold text-xl tracking-tight">
              <span class="bg-white text-blue-700 p-1 rounded mr-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </span>
              Publicações
            </NuxtLink>

            <div class="hidden md:ml-8 md:flex md:space-x-4">
              <NuxtLink to="/" class="nav-link">Início</NuxtLink>
              <template v-if="token">
                <NuxtLink to="/tags" class="nav-link">Gestão Tags</NuxtLink>
                <NuxtLink v-if="user?.role === 'Administrador'" to="/users" class="nav-link">Gestão Users</NuxtLink>
              </template>
            </div>
          </div>

          <div class="flex-1"></div>

          <div class="hidden md:flex items-center space-x-4">
            <template v-if="token">

              <div class="relative">
                <button
                    @click="isNotifMenuOpen = !isNotifMenuOpen"
                    class="bg-blue-700 p-1 rounded-full text-blue-200 hover:text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-blue-800 focus:ring-white relative"
                >
                  <span class="sr-only">Ver notificações</span>
                  <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                  </svg>
                  <span v-if="notifStore.unreadCount > 0" class="absolute top-0 right-0 block h-2.5 w-2.5 rounded-full ring-2 ring-blue-800 bg-red-500 transform translate-x-1/4 -translate-y-1/4"></span>
                </button>

                <div v-if="isNotifMenuOpen" @click.away="isNotifMenuOpen = false" class="origin-top-right absolute right-0 mt-2 w-80 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-50">
                  <div class="px-4 py-2 border-b border-gray-100 font-semibold text-gray-700">Notificações</div>
                  <div class="max-h-64 overflow-y-auto">
                    <NuxtLink
                        v-for="n in notifStore.notifications"
                        :key="n.id"
                        :to="`/publications/${n.publicationId}`"
                        class="block px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 border-b border-gray-100 last:border-0"
                        @click="isNotifMenuOpen = false"
                    >
                      <p class="font-medium text-blue-600">{{ n.message }}</p>
                      <p class="text-xs text-gray-400 mt-1">{{ n.timestamp }}</p>
                    </NuxtLink>
                    <div v-if="notifStore.notifications.length === 0" class="px-4 py-4 text-sm text-gray-500 text-center">
                      Sem notificações novas.
                    </div>
                  </div>
                </div>
              </div>

              <div class="relative ml-3">
                <button
                    @click="isUserMenuOpen = !isUserMenuOpen"
                    class="flex items-center text-white bg-blue-800 hover:bg-blue-900 px-3 py-2 rounded-lg text-sm font-medium transition"
                >
                  <span class="mr-2">👤 {{ user?.username }}</span>
                  <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                </button>

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

      <div v-if="isMobileMenuOpen" class="md:hidden bg-blue-800 border-t border-blue-600">
        <div class="px-2 pt-2 pb-3 space-y-1">
          <NuxtLink to="/" class="mobile-nav-link">Início</NuxtLink>
          <template v-if="token">

            <NuxtLink to="/tags" class="mobile-nav-link">Gestão Tags</NuxtLink>
            <NuxtLink v-if="user?.role === 'Administrador'" to="/users" class="mobile-nav-link">Gestão Users</NuxtLink>
            <NuxtLink to="/users/profile" class="mobile-nav-link text-blue-200">O Meu Perfil</NuxtLink>

            <div class="px-3 py-2 text-blue-200" v-if="notifStore.unreadCount > 0">
              Você tem {{ notifStore.unreadCount }} novas notificações.
            </div>

            <button @click="handleLogout" class="block w-full text-left px-3 py-2 text-red-300 font-medium italic">Sair</button>
          </template>
          <NuxtLink v-else to="/auth/login" class="mobile-nav-link font-bold">Login</NuxtLink>
        </div>
      </div>
    </nav>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <slot />
    </main>

    <footer class="bg-white border-t border-gray-200 py-8 mt-auto">
      <div class="text-center text-gray-500 text-sm">
        <p>&copy; 2025 Centro de I&D XYZ - Plataforma DAE</p>
        <p class="mt-1 font-semibold text-blue-600">Engenharia Informática - IPLeiria</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useAuthStore } from "~/stores/auth-store.js";
import { useNotificationStore } from "~/stores/notification-store";
import { storeToRefs } from "pinia";
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const notifStore = useNotificationStore(); // Store de Notificações
const router = useRouter();
const { token, user } = storeToRefs(authStore);

const isMobileMenuOpen = ref(false);
const isUserMenuOpen = ref(false);
const isNotifMenuOpen = ref(false);

function handleLogout() {
  isUserMenuOpen.value = false;
  authStore.logout();
  router.push('/auth/login');
}

// Carregar notificações
onMounted(() => {
  if (token.value) {
    notifStore.fetchNotifications(token.value);
  }
});

// Watch token para carregar notificações ao fazer login
watch(() => token.value, (newToken) => {
  if (newToken) {
    notifStore.fetchNotifications(newToken);
  }
});

// Fechar menus ao mudar de rota
watch(() => router.currentRoute.value.fullPath, () => {
  isMobileMenuOpen.value = false;
  isUserMenuOpen.value = false;
  isNotifMenuOpen.value = false;
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