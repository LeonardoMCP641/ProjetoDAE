<template>
  <div class="container mt-5">
    <div v-if="publication">

      <div class="card shadow-lg border-0">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center py-3">
          <h2 class="h4 mb-0 fw-bold">{{ publication.titulo }}</h2>
          <span class="badge bg-light text-primary fs-6">{{ publication.tipo }}</span>
        </div>

        <div class="card-body p-4">
          <div class="row">
            <div class="col-md-8">

              <h5 class="text-primary mb-3 text-uppercase small fw-bold tracking-wide">
                {{ publication.area }}
              </h5>

              <div class="mb-4 bg-light p-3 rounded border-start border-4 border-primary">
                <h6 class="fw-bold text-dark mb-2">Resumo</h6>
                <p class="mb-0 text-secondary" style="white-space: pre-wrap;">{{ publication.resumoCurto }}</p>
              </div>

              <div class="mb-4">
                <h6 class="fw-bold text-dark">Autores</h6>
                <ul class="list-inline">
                  <li v-for="(autor, index) in publication.autores" :key="index" class="list-inline-item">
                    <span class="badge bg-white text-dark border shadow-sm p-2">
                      <i class="bi bi-person-circle text-secondary me-1"></i> {{ autor }}
                    </span>
                  </li>
                </ul>
              </div>

              <div class="mb-3">
                <h6 class="fw-bold text-dark">Tags</h6>
                <div class="d-flex flex-wrap gap-2 mt-2">
                  <span v-for="tag in publication.tags" :key="tag.id" class="badge bg-light text-dark border p-2">
                    #{{ tag.name }}
                  </span>
                  <span v-if="!publication.tags || publication.tags.length === 0" class="text-muted small">
                    Sem tags associadas.
                  </span>
                </div>
              </div>

            </div>

            <div class="col-md-4 border-start ps-md-4">

              <div class="d-grid gap-2 mb-4">
                <h6 class="text-muted mb-2 text-uppercase small fw-bold">Ficheiros</h6>
                <button v-if="publication.filename" @click="downloadPdf" class="btn btn-success py-2 shadow-sm">
                  <i class="bi bi-file-earmark-arrow-down me-2"></i> Download PDF
                </button>
                <button v-else class="btn btn-light text-muted border" disabled>
                  Sem Ficheiro Anexado
                </button>
              </div>

              <hr class="my-4">

              <div class="d-grid gap-2">
                <h6 class="text-muted mb-2 text-uppercase small fw-bold">Gestão</h6>

                <button class="btn btn-outline-warning text-dark text-start" @click="goToEdit">
                  <i class="bi bi-pencil me-2"></i> Editar Publicação
                </button>

                <button
                    v-if="isOwner"
                    class="btn btn-outline-danger text-start"
                    @click.prevent="deletePublication"
                >
                  <i class="bi bi-trash me-2"></i> Eliminar Publicação
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="card-footer bg-light text-muted small py-3">
          Publicado em: <strong>{{ publication.publicationDate ? new Date(publication.publicationDate).toLocaleDateString() : 'N/A' }}</strong>
          | Submetido por: <strong>{{ publication.username || 'Sistema' }}</strong>
        </div>
      </div>

      <div v-if="message" class="alert alert-info mt-3 shadow-sm">{{ message }}</div>

      <div class="mt-3">
        <NuxtLink to="/" class="btn btn-link text-decoration-none ps-0">&larr; Voltar à lista</NuxtLink>
      </div>
    </div>

    <div v-else class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Carregando publicação...</p>
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
const isOwner = ref(false); // Controla se pode apagar
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
    // URL hardcoded (ajustar se necessário para usar config.public.apiBase)
    const res = await $fetch(
        `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`,
        {
          headers: { Authorization: `Bearer ${token.value}` },
        }
    );
    publication.value = res;

    // Lógica para determinar se é dono (para mostrar botão de apagar)
    // Ajustar conforme a estrutura real do teu DTO (se user vem como string ou objeto)
    isOwner.value = (user.value.username === publication.value.username) || (user.value.role === 'Administrador');

  } catch (err) {
    console.error(err);
    router.push("/");
  }
});

// Download PDF
async function downloadPdf() {
  try {
    const id = route.params.id;
    const pdfBlob = await $fetch(
        `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}/download`,
        {
          headers: { Authorization: `Bearer ${token.value}` },
          responseType: "blob",
        }
    );

    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement("a");
    link.href = url;
    link.download = publication.value.filename || `${publication.value.titulo}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  } catch (err) {
    console.error("Erro ao fazer download do PDF", err);
    message.value = "Não foi possível fazer download do PDF.";
  }
}

// Navegar para Edição
function goToEdit() {
  router.push(`/publications/edit/${publication.value.id}`);
}

// Apagar Publicação
async function deletePublication() {
  if (!confirm("Tem certeza que deseja eliminar esta publicação permanentemente?")) return;

  try {
    const id = route.params.id;
    await $fetch(
        `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token.value}` },
        }
    );

    message.value = "Publicação eliminada com sucesso!";
    setTimeout(() => {
      router.push("/");
    }, 1500);
  } catch (err) {
    console.error("Erro ao eliminar publicação", err);
    message.value = "Não foi possível eliminar a publicação.";
  }
}
</script>