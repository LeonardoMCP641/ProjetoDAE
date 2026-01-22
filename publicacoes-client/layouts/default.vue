<template>
  <div class="min-h-screen bg-gray-50 flex font-sans">

    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col fixed h-full z-50">

      <div class="p-6 flex items-center border-b border-gray-50 h-16">
        <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center text-white mr-3 shadow-blue-200 shadow-md">
          <i class="bi bi-collection-fill text-sm"></i>
        </div>
        <div>
          <h1 class="font-bold text-gray-800 text-lg leading-tight">XYZ Center</h1>
          <p class="text-[9px] text-gray-400 font-bold tracking-widest uppercase">Scientific Platform</p>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto py-6 px-4 space-y-1">

        <NuxtLink to="/" class="flex items-center px-4 py-3 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group mb-1">
          <i class="bi bi-house-door-fill mr-3 text-lg group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">O Meu Feed</span>
        </NuxtLink>

        <NuxtLink to="/explorar" class="flex items-center px-4 py-3 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group mb-6">
          <i class="bi bi-compass-fill mr-3 text-lg group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">Explorar</span>
        </NuxtLink>

        <div class="px-4 mb-2 mt-6">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Pessoal</p>
        </div>

        <NuxtLink to="/publications/mine" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group">
          <i class="bi bi-folder mr-3 group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">Meus Uploads</span>
        </NuxtLink>

        <NuxtLink to="/tags" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group">
          <i class="bi bi-tags mr-3 group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">Tags Subscritas</span>
        </NuxtLink>

        <div v-if="user?.role === 'Administrador' || user?.role === 'Responsavel'" class="mt-8">
          <div class="px-4 mb-2">
            <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Administração</p>
          </div>

          <NuxtLink to="/tags/manage" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-orange-50 hover:text-orange-600 transition group">
            <i class="bi bi-folder-plus mr-3 group-hover:text-orange-600"></i>
            <span class="font-medium text-sm">Gestão de Tags</span>
          </NuxtLink>

          <template v-if="user?.role === 'Administrador'">
            <NuxtLink to="/users" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-orange-50 hover:text-orange-600 transition group">
              <i class="bi bi-people-fill mr-3 group-hover:text-orange-600"></i>
              <span class="font-medium text-sm">Gestão de Users</span>
            </NuxtLink>

            <NuxtLink to="/admin/history" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-orange-50 hover:text-orange-600 transition group">
              <i class="bi bi-clock-history mr-3 group-hover:text-orange-600"></i>
              <span class="font-medium text-sm">Histórico Global</span>
            </NuxtLink>
          </template>

        </div>
      </nav>

      <div v-if="user" class="p-4 border-t border-gray-100 bg-gray-50/50">
        <div class="flex items-center mb-3">
          <div class="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold mr-3 border-2 border-white shadow-sm">
            {{ user.username.charAt(0).toUpperCase() }}
          </div>
          <div class="overflow-hidden">
            <p class="text-sm font-bold text-gray-800 truncate">{{ user.username }}</p>
            <p class="text-xs text-gray-500 truncate">{{ user.role }}</p>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-2">
          <NuxtLink to="/users/profile" class="flex items-center justify-center px-3 py-1.5 text-xs font-bold text-gray-600 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition">Perfil</NuxtLink>
          <button @click="logout" class="flex items-center justify-center px-3 py-1.5 text-xs font-bold text-red-600 bg-white border border-red-100 rounded-lg hover:bg-red-50 transition">Sair</button>
        </div>
      </div>

      <div v-else class="p-4 border-t border-gray-100">
        <NuxtLink to="/auth/login" class="flex items-center justify-center w-full py-2.5 bg-blue-600 text-white font-bold text-sm rounded-xl hover:bg-blue-700 transition shadow-blue-200 shadow-sm">Entrar</NuxtLink>
      </div>

    </aside>

    <div class="flex-1 flex flex-col ml-64 min-w-0 transition-all duration-300">

      <header class="bg-white border-b border-gray-200 h-16 flex items-center justify-end px-8 sticky top-0 z-40 shadow-sm/50">
        <div v-if="token" class="flex items-center space-x-4">
          <div class="relative">
            <button @click="isNotifMenuOpen = !isNotifMenuOpen" class="p-2 rounded-full text-gray-400 hover:text-blue-600 hover:bg-blue-50 transition focus:outline-none relative">
              <i class="bi bi-bell-fill text-xl"></i>
              <span v-if="notifStore.unreadCount > 0" class="absolute top-1 right-1 block h-2.5 w-2.5 rounded-full ring-2 ring-white bg-red-500"></span>
            </button>

            <div v-if="isNotifMenuOpen" @click.away="isNotifMenuOpen = false" class="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-2xl border border-gray-100 py-2 z-50 origin-top-right animate-fadeIn">
              <div class="px-4 py-2 border-b border-gray-50 flex justify-between items-center">
                <span class="font-bold text-gray-700 text-sm">Notificações</span>
                <span v-if="notifStore.unreadCount > 0" class="bg-red-100 text-red-600 text-[10px] font-bold px-2 py-0.5 rounded-full">{{ notifStore.unreadCount }} novas</span>
              </div>
              <div class="max-h-64 overflow-y-auto custom-scrollbar">
                <NuxtLink v-for="n in notifStore.notifications" :key="n.id" :to="`/publications/${n.publicationId}`" class="block px-4 py-3 hover:bg-blue-50/50 transition border-b border-gray-50 last:border-0 group" @click="isNotifMenuOpen = false">
                  <p class="text-sm font-medium text-gray-700 group-hover:text-blue-600 leading-snug">{{ n.message }}</p>
                  <p class="text-[10px] text-gray-400 mt-1 flex items-center"><i class="bi bi-clock mr-1"></i> {{ n.timestamp }}</p>
                </NuxtLink>
                <div v-if="notifStore.notifications.length === 0" class="px-4 py-8 text-center text-gray-400">
                  <span class="text-xs">Sem notificações novas.</span>
                </div>
              </div>
            </div>
          </div>
          <span class="text-sm font-medium text-gray-500 hidden sm:block">Olá, <span class="text-gray-800 font-bold">{{ user?.name || user?.username }}</span></span>
        </div>
      </header>

      <main class="flex-1 p-8">
        <slot />
      </main>

      <footer class="bg-white border-t border-gray-200 py-6 mt-auto">
        <div class="max-w-7xl mx-auto px-8 text-center">
          <p class="text-sm text-gray-500">&copy; 2025 Centro de I&D XYZ</p>
          <p class="text-xs text-blue-600 font-semibold mt-1">Engenharia Informática - IPLeiria</p>
        </div>
      </footer>
    </div>

  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useAuthStore } from "~/stores/auth-store.js";
import { useNotificationStore } from "~/stores/notification-store";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

const authStore = useAuthStore();
const notifStore = useNotificationStore();
const { user, token } = storeToRefs(authStore);
const router = useRouter();

const isNotifMenuOpen = ref(false);

function logout() {
  authStore.logout();
  router.push('/auth/login');
}

onMounted(() => {
  if (token.value) {
    notifStore.fetchNotifications(token.value);
  }
});

watch(() => token.value, (newToken) => {
  if (newToken) notifStore.fetchNotifications(newToken);
});

watch(() => router.currentRoute.value.fullPath, () => {
  isNotifMenuOpen.value = false;
});
</script>

<style scoped>
.router-link-active { background-color: #eff6ff; color: #2563eb; }
.router-link-active i { color: #2563eb; }
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 4px; }
.animate-fadeIn { animation: fadeIn 0.2s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(-5px); } to { opacity: 1; transform: translateY(0); } }
</style>