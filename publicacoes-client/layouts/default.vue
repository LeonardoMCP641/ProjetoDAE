<template>
  <div class="min-h-screen bg-gray-50 flex font-sans">

    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col fixed h-full z-50">

      <div class="p-6 flex items-center border-b border-gray-50">
        <div class="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center text-white mr-3 shadow-blue-200 shadow-md">
          <i class="bi bi-collection-fill text-lg"></i>
        </div>
        <div>
          <h1 class="font-bold text-gray-800 text-lg leading-tight">XYZ Center</h1>
          <p class="text-[10px] text-gray-400 font-bold tracking-widest uppercase">Scientific Platform</p>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto py-6 px-4 space-y-1">

        <div class="px-4 mb-2 mt-6">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Publicações</p>
        </div>

        <NuxtLink to="/" class="flex items-center px-4 py-3 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group mb-1">
          <i class="bi bi-house-door-fill mr-3 text-lg group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">O Meu Feed</span>
        </NuxtLink>

        <NuxtLink to="/explorar" class="flex items-center px-4 py-3 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group mb-6">
          <i class="bi bi-compass-fill mr-3 text-lg group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">Explorar</span>
        </NuxtLink>

        <NuxtLink to="/publications/mine" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group">
          <i class="bi bi-folder mr-3 group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">Meus Uploads</span>
        </NuxtLink>

        <NuxtLink to="/tags" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-blue-50 hover:text-blue-600 transition group">
          <i class="bi bi-tags mr-3 group-hover:text-blue-600"></i>
          <span class="font-medium text-sm">Tags Subscritas</span>
        </NuxtLink>


        <div v-if="user?.role === 'Administrador'" class="mt-8">
          <div class="px-4 mb-2">
            <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Administração</p>
          </div>

          <NuxtLink to="/tags/manage" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-orange-50 hover:text-orange-600 transition group">
            <i class="bi bi-folder-plus mr-3 group-hover:text-orange-600"></i>
            <span class="font-medium text-sm">Gestão de Tags</span>
          </NuxtLink>

          <NuxtLink to="/users" class="flex items-center px-4 py-2.5 text-gray-600 rounded-xl hover:bg-orange-50 hover:text-orange-600 transition group">
            <i class="bi bi-people-fill mr-3 group-hover:text-orange-600"></i>
            <span class="font-medium text-sm">Gestão de Users</span>
          </NuxtLink>
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
          <button class="flex items-center justify-center px-3 py-1.5 text-xs font-bold text-gray-600 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition">
            Perfil
          </button>
          <button @click="logout" class="flex items-center justify-center px-3 py-1.5 text-xs font-bold text-red-600 bg-white border border-red-100 rounded-lg hover:bg-red-50 transition">
            Sair
          </button>
        </div>
      </div>

      <div v-else class="p-4 border-t border-gray-100">
        <NuxtLink to="/auth/login" class="flex items-center justify-center w-full py-2.5 bg-blue-600 text-white font-bold text-sm rounded-xl hover:bg-blue-700 transition shadow-blue-200 shadow-sm">
          Entrar
        </NuxtLink>
      </div>

    </aside>

    <main class="flex-1 ml-64 p-8">
      <slot />
    </main>

  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

const authStore = useAuthStore();
const { user } = storeToRefs(authStore);
const router = useRouter();

function logout() {
  authStore.logout();
  router.push('/auth/login');
}
</script>

<style>
.router-link-active {
  background-color: #eff6ff;
  color: #2563eb;
}
.router-link-active i {
  color: #2563eb;
}
</style>