<template>
  <div class="container mt-4">
    <h2>Histórico de Edições</h2>

    <div v-if="loading">
      <p>A carregar histórico...</p>
    </div>

    <table v-else-if="history.length" class="table table-striped mt-3">
      <thead>
        <tr>
          <th>Publicação</th>
          <th>Campo</th>
          <th>Valor Antigo</th>
          <th>Valor Novo</th>
          <th>Data</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(h, index) in history" :key="index">
          <td>{{ h.publicationTitle || h.publicationId }}</td>
          <td>{{ h.fieldName }}</td>
          <td>{{ h.oldValue }}</td>
          <td>{{ h.newValue }}</td>
          <td>{{ formatDate(h.editedAt) }}</td>
        </tr>
      </tbody>
    </table>

    <div v-else class="alert alert-info mt-3">
      Esta publicação ainda não tem histórico.
    </div>

    <button class="btn btn-secondary mt-3" @click="goBack">
      Voltar
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const route = useRoute();
const router = useRouter();

const authStore = useAuthStore();
const { token } = storeToRefs(authStore);

const history = ref([]);
const loading = ref(true);

onMounted(async () => {
  if (!token.value) {
    router.push("/auth/login");
    return;
  }

  try {
    const publicationId = route.query.id;

    if (!publicationId) {
      router.push("/publications/mine");
      return;
    }

    const res = await $fetch(
      `http://localhost:8080/publicacoescientificas/api/publicacoes/history/${publicationId}`,
      {
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      }
    );

  history.value = res;
  } catch (err) {
    console.error("Erro ao carregar histórico", err);
    router.push("/publications/mine");
  } finally {
    loading.value = false;
  }
});

function formatDate(date) {
  return new Date(date).toLocaleString("pt-PT");
}

function goBack() {
  router.back();
}
</script>
