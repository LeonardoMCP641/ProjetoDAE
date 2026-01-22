<template>
  <div v-for="p in myPublications" :key="p.id" class="list-group-item mb-2">
    <h5>{{ p.titulo }}</h5>
    <p>{{ p.resumoCurto }}</p>

    <span class="badge" :class="p.visivel ? 'bg-success' : 'bg-secondary'">
      {{ p.visivel ? "Visível" : "Oculta" }}
    </span>

    <div class="mt-2 d-flex gap-2">
      <NuxtLink :to="`/publications/${p.id}`" class="btn btn-sm btn-primary">
        Ver publicação
      </NuxtLink>

      <NuxtLink
        :to="`/publications/history?id=${p.id}`"
        class="btn btn-sm btn-outline-secondary"
      >
        Ver histórico
      </NuxtLink>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "~/stores/auth-store.js";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);

const publicationStore = usePublicationStore();
const myPublications = ref([]);
const loading = ref(true);

onMounted(async () => {
  if (!token.value) return;

  // Certifica-te que o user está carregado
  if (!user.value) await authStore.fetchUser();

  // Busca todas as publicações e guarda no store
  await publicationStore.fetchAll(token.value);

  // Filtra as publicações do user logado
  myPublications.value = publicationStore.publications.filter(
    (pub) => pub.username === user.value.username,
  );

  loading.value = false;
});
</script>
