<template>
  <div class="container mt-5">
    <div v-if="publication" class="card shadow-lg border-0">
      <div class="card-header bg-primary text-white">
        <h2 class="mb-0">{{ publication.titulo }}</h2>
        <small class="text-light">Área: {{ publication.area }} | Tipo: {{ publication.tipo }}</small>
      </div>

      <div class="card-body">
        <!-- Resumo -->
        <p class="lead">{{ publication.resumoCurto }}</p>

        <!-- Autores -->
        <p>
          <strong>Autores:</strong>
          <span v-for="(autor, idx) in publication.autores" :key="idx">
            {{ autor }}<span v-if="idx < publication.autores.length - 1">, </span>
          </span>
        </p>

        <!-- Visibilidade -->
        <p>
          <strong>Visível:</strong>
          <span 
            class="badge" 
            :class="publication.visivel ? 'bg-success' : 'bg-danger'"
          >
            {{ publication.visivel ? 'Sim' : 'Não' }}
          </span>
        </p>

        <!-- Botões de ação -->
        <div class="d-flex flex-wrap gap-2 mt-4">
          <!-- Download -->
          <button
            v-if="publication.filename"
            @click="downloadPdf"
            class="btn btn-primary"
          >
            <i class="bi bi-file-earmark-arrow-down"></i> Download PDF
          </button>

          <!-- Editar -->
          <button
            class="btn btn-warning"
            @click="goToEdit"
          >
            <i class="bi bi-pencil-square"></i> Editar
          </button>

          <!-- Apagar (apenas owner) -->
          <button
            v-if="isOwner"
            class="btn btn-danger"
            @click.prevent="deletePublication"
          >
            <i class="bi bi-trash"></i> Apagar
          </button>
        </div>

        <!-- Mensagem de status -->
        <div v-if="message" class="alert alert-info mt-3">{{ message }}</div>
      </div>
    </div>

    <div v-else class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Carregando...</span>
      </div>
      <p class="mt-3">Carregando publicação...</p>
    </div>
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

const publication = ref(null);
const isOwner = ref(false);
const user = ref(null);
const message = ref("");

onMounted(async () => {
  if (!token.value) {
    router.push("/auth/login");
    return;
  }

  try {
    if (!authStore.user) await authStore.fetchUser();
    user.value = authStore.user;

    const id = route.params.id;
    const res = await $fetch(
      `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`,
      { headers: { Authorization: `Bearer ${token.value}` } }
    );
    publication.value = res;

    isOwner.value = user.value.username === publication.value.username;
  } catch (err) {
    console.error(err);
    router.push("/");
  }
});

async function downloadPdf() {
  try {
    const id = route.params.id;
    const pdfBlob = await $fetch(
      `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}/download`,
      { headers: { Authorization: `Bearer ${token.value}` }, responseType: "blob" }
    );
    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${publication.value.titulo}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  } catch (err) {
    console.error(err);
    message.value = "Não foi possível fazer download do PDF.";
  }
}

function goToEdit() {
  router.push(`/publications/edit/${publication.value.id}`);
}

async function deletePublication() {
  if (!confirm("Tem certeza que deseja eliminar esta publicação?")) return;
  try {
    const id = route.params.id;
    await $fetch(
      `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`,
      { method: "DELETE", headers: { Authorization: `Bearer ${token.value}` } }
    );
    message.value = "Publicação eliminada com sucesso!";
    setTimeout(() => router.push("/"), 1500);
  } catch (err) {
    console.error(err);
    message.value = "Não foi possível eliminar a publicação.";
  }
}
</script>

<style scoped>
.card {
  border-radius: 1rem;
}

.card-header {
  border-top-left-radius: 1rem;
  border-top-right-radius: 1rem;
}

.badge {
  font-size: 1em;
}
</style>
