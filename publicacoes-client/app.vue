<script setup>
import { useAuthStore } from "~/stores/auth-store.js";

const authStore = useAuthStore();

// O onMounted corre apenas no Browser (Client-side)
onMounted(() => {
  authStore.init();
});
</script>
<template>
  <NuxtLayout>
    <NuxtPage /> <!-- Isto carrega as tuas páginas -->
  </NuxtLayout>
</template>
