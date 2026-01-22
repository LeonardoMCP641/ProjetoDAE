<template>
  <div class="flex min-h-screen bg-gray-50 font-sans">

    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col fixed h-full shadow-sm z-20">
      <div class="p-6 border-b border-gray-100 flex items-center">
        <div class="bg-blue-600 text-white p-1.5 rounded mr-3">
          <i class="bi bi-journal-text text-xl"></i>
        </div>
        <div>
          <h1 class="text-lg font-bold text-gray-800 leading-tight">XYZ Center</h1>
          <p class="text-[10px] text-gray-500 uppercase tracking-wider font-semibold">Scientific Platform</p>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto py-6 px-3 space-y-1">

        <NuxtLink to="/" class="nav-item" active-class="active">
          <i class="bi bi-grid-1x2 mr-3 text-lg"></i> Dashboard
        </NuxtLink>

        <template v-if="token">
          <div class="mt-6 mb-2 px-4 text-xs font-semibold text-gray-400 uppercase tracking-wider">
            Publicações
          </div>

          <NuxtLink to="/publications" class="nav-item" active-class="active">
            <i class="bi bi-globe mr-3 text-lg"></i> Explorar
          </NuxtLink>

          <NuxtLink to="/publications/mine" class="nav-item" active-class="active">
            <i class="bi bi-folder2-open mr-3 text-lg"></i> Meus Uploads
          </NuxtLink>

          <NuxtLink to="/tags" class="nav-item" active-class="active">
            <i class="bi bi-tags mr-3 text-lg"></i> Tags Subscritas
          </NuxtLink>
        </template>

        <template v-else>
          <NuxtLink to="/auth/login" class="nav-item text-blue-600 bg-blue-50 mt-4">
            <i class="bi bi-box-arrow-in-right mr-3 text-lg"></i> Fazer Login
          </NuxtLink>
        </template>
      </nav>

      <div v-if="token" class="p-4 border-t border-gray-100 bg-gray-50">
        <div class="flex items-center mb-3">
          <div class="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold mr-3">
            {{ user?.username?.charAt(0).toUpperCase() }}
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-bold text-gray-900 truncate">{{ user?.username }}</p>
            <p class="text-xs text-gray-500 truncate">{{ user?.role || 'Colaborador' }}</p>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-2">
          <NuxtLink to="/users/profile" class="text-xs text-center py-1.5 border border-gray-200 rounded hover:bg-white transition">
            Perfil
          </NuxtLink>
          <button @click="handleLogout" class="text-xs text-center py-1.5 border border-red-200 text-red-600 rounded hover:bg-red-50 transition">
            Sair
          </button>
        </div>
      </div>
    </aside>

    <main class="flex-1 ml-64 p-8 min-h-screen">
      <slot />
    </main>

  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const router = useRouter();
const { token, user } = storeToRefs(authStore);

function handleLogout() {
  authStore.logout();
  router.push('/auth/login');
}
</script>

<style scoped>
.nav-item {
  @apply flex items-center px-4 py-2.5 text-gray-600 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors font-medium text-sm;
}

.nav-item.active {
  @apply bg-blue-50 text-blue-700 font-bold border border-blue-100 shadow-sm;
}

.nav-item i {
  @apply transition-colors;
}

.nav-item:hover i {
  @apply text-blue-600;
}
</style>