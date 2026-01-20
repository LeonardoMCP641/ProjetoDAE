<template>
  <div class="container mt-4">
    <div v-if="publication">
      <h2>{{ publication.titulo }}</h2>
      <p>{{ publication.resumoCurto }}</p>
      <p>Autores: {{ publication.autores.join(", ") }}</p>

      <!-- Botão para download -->
      <button
        v-if="publication.filename"
        @click="downloadPdf"
        class="btn btn-primary mt-3 me-2"
      >
        Download PDF
      </button>

      <!-- Botões Editar e Apagar -->
      <div class="mt-3">
        <button
          class="btn btn-warning me-2"
          @click="goToEdit"
        >
          Editar
        </button>

        <!-- Só aparece se o user for dono -->
        <button
          v-if="isOwner"
          class="btn btn-danger"
          @click.prevent="deletePublication"
        >
          Apagar Publicação
        </button>
      </div>

      <!-- Mensagem de sucesso/erro -->
      <div v-if="message" class="alert alert-info mt-3">{{ message }}</div>
    </div>

    <div v-else>
      <p>Carregando publicação...</p>
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
const user = ref(null); // user logado
const message = ref("");

onMounted(async () => {
  if (!token.value) {
    router.push("/auth/login");
    return;
  }

  try {
    // Garantir que o user está carregado
    if (!authStore.user) await authStore.fetchUser();
    user.value = authStore.user;

    // Buscar publicação
    const id = route.params.id;
    const res = await $fetch(
      `http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`,
      {
        headers: { Authorization: `Bearer ${token.value}` },
      }
    );
    publication.value = res;

    // Verifica se o user é dono (quem criou a publicação)
    isOwner.value = user.value.username === publication.value.username;
  } catch (err) {
    console.error(err);
    router.push("/");
  }
});

// Função para download do PDF
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
    link.download = `${publication.value.titulo}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  } catch (err) {
    console.error("Erro ao fazer download do PDF", err);
    message.value = "Não foi possível fazer download do PDF.";
  }
}

// Vai para a página de edição
function goToEdit() {
  router.push(`/publications/edit/${publication.value.id}`);
}

// Elimina a publicação com confirmação
async function deletePublication() {
  if (!confirm("Tem certeza que deseja eliminar esta publicação?")) return;

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
      router.push("/"); // Redireciona após eliminar
    }, 1500);
  } catch (err) {
    console.error("Erro ao eliminar publicação", err);
    message.value = "Não foi possível eliminar a publicação.";
  }
}
</script>
