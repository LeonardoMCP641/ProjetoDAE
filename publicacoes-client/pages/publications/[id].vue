<template>
  <div v-if="publication" class="max-w-4xl mx-auto mb-10">

    <div class="bg-gradient-to-r from-blue-600 to-blue-800 rounded-t-2xl p-8 text-white shadow-lg relative overflow-hidden">

      <div class="absolute top-0 right-0 -mt-10 -mr-10 w-40 h-40 bg-white opacity-10 rounded-full blur-2xl pointer-events-none"></div>

      <div class="flex justify-between items-start relative z-10">
        <div class="flex flex-wrap gap-2">
          <span class="bg-white/20 backdrop-blur-md px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border border-white/30">
            {{ publication.area }}
          </span>
          <span class="bg-white/20 backdrop-blur-md px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border border-white/30">
            {{ publication.tipo }}
          </span>
          <span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border border-white/30"
                :class="publication.visivel ? 'bg-green-500/20 text-green-50' : 'bg-red-500/20 text-red-50'">
            <i class="bi" :class="publication.visivel ? 'bi-eye' : 'bi-eye-slash'"></i>
            {{ publication.visivel ? 'Público' : 'Privado' }}
          </span>
        </div>

        <div v-if="isOwner" class="flex gap-2">
          <button @click="goToEdit" class="bg-white/20 hover:bg-white/30 text-white p-2 rounded-lg transition backdrop-blur-md shadow-sm" title="Editar">
            <i class="bi bi-pencil-fill"></i>
          </button>
          <button @click="deletePublication" class="bg-red-500/80 hover:bg-red-600 text-white p-2 rounded-lg transition backdrop-blur-md shadow-sm" title="Apagar">
            <i class="bi bi-trash-fill"></i>
          </button>
        </div>
      </div>

      <h1 class="text-3xl md:text-4xl font-bold mt-6 mb-4 leading-tight shadow-black/5">
        {{ publication.titulo }}
      </h1>

      <div class="flex items-center text-blue-100 text-sm font-medium">
        <div class="flex items-center mr-6">
          <div class="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center mr-2 border border-white/30">
            <i class="bi bi-person-fill"></i>
          </div>
          <span>{{ publication.username }}</span>
        </div>

        <div class="flex items-center">
          <i class="bi bi-calendar-event mr-2 opacity-70"></i>
          <span>{{ new Date(publication.publicationDate || Date.now()).toLocaleDateString('pt-PT') }}</span>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-b-2xl shadow-sm border-x border-b border-gray-100 p-8">

      <div class="mb-8">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 flex items-center">
          <i class="bi bi-people-fill mr-2"></i> Autores
        </h3>
        <div class="flex flex-wrap gap-2">
          <span v-for="(autor, index) in publication.autores" :key="index"
                class="px-4 py-1.5 bg-gray-50 text-gray-700 rounded-lg text-sm font-medium border border-gray-100 hover:bg-blue-50 hover:text-blue-600 transition cursor-default">
            {{ autor }}
          </span>
        </div>
      </div>

      <div class="mb-10">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 flex items-center">
          <i class="bi bi-card-text mr-2"></i> Resumo
        </h3>
        <div class="relative">
          <div class="absolute top-0 left-0 w-1 h-full bg-blue-500 rounded-l"></div>
          <p class="text-gray-700 leading-relaxed text-lg bg-gray-50 p-6 rounded-r-xl text-justify">
            {{ publication.resumoCurto }}
          </p>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row gap-4 pt-6 border-t border-gray-100">
        <button
            v-if="publication.filename"
            @click="downloadPdf"
            class="flex-1 flex items-center justify-center px-6 py-4 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition shadow-lg shadow-blue-200 transform hover:-translate-y-0.5"
        >
          <i class="bi bi-file-earmark-pdf-fill mr-3 text-xl"></i>
          <span>Download PDF</span>
        </button>

        <button v-else class="flex-1 px-6 py-4 bg-gray-100 text-gray-400 rounded-xl font-bold cursor-not-allowed border border-gray-200 flex items-center justify-center">
          <i class="bi bi-file-earmark-x mr-2 text-lg"></i>
          Sem Ficheiro Disponível
        </button>
      </div>

    </div>

    <div class="mt-8 bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
      <div class="flex items-center mb-6">
        <div class="bg-blue-100 p-2 rounded-lg text-blue-600 mr-3">
          <i class="bi bi-chat-dots-fill"></i>
        </div>
        <h3 class="text-xl font-bold text-gray-800">Comentários</h3>
      </div>

      <div class="text-center py-8 bg-gray-50 rounded-xl border border-dashed border-gray-200">
        <p class="text-gray-500 italic">Ainda não há comentários nesta publicação.</p>
        <p class="text-sm text-gray-400 mt-1">Sê o primeiro a comentar!</p>
      </div>
    </div>

  </div>

  <div v-else class="flex flex-col items-center justify-center py-32 text-gray-400">
    <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
    <p class="font-medium animate-pulse">A carregar publicação...</p>
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
const message = ref(""); // Para mensagens de erro/sucesso

onMounted(async () => {
  // Verificar autenticação
  if (!token.value) {
    router.push("/auth/login");
    return;
  }

  try {
    // Garantir que temos os dados do user atual
    if (!authStore.user) await authStore.fetchUser();

    // Fetch da Publicação
    const id = route.params.id;
    const res = await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });

    publication.value = res;

    // Verificar se o user logado é o dono da publicação
    isOwner.value = authStore.user.username === publication.value.username;

  } catch (err) {
    console.error("Erro ao carregar:", err);
    router.push("/"); // Redireciona se houver erro grave (ex: 404)
  }
});

// Download PDF
async function downloadPdf() {
  try {
    const id = route.params.id;
    const pdfBlob = await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${id}/download`, {
      headers: { Authorization: `Bearer ${token.value}` },
      responseType: "blob",
    });

    // Criar link temporário para download
    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement("a");
    link.href = url;
    // Tenta usar o filename original, ou um genérico
    link.download = publication.value.filename || `${publication.value.titulo}.pdf`;
    document.body.appendChild(link);
    link.click();

    // Limpeza
    document.body.removeChild(link);
    URL.revokeObjectURL(url);

  } catch (err) {
    console.error(err);
    alert("Não foi possível fazer o download do ficheiro.");
  }
}

// Navegar para Editar
function goToEdit() {
  router.push(`/publications/edit/${publication.value.id}`);
}

// Apagar Publicação
async function deletePublication() {
  if (!confirm("Tens a certeza absoluta que queres eliminar esta publicação? Esta ação não pode ser desfeita.")) return;

  try {
    await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${route.params.id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token.value}` },
    });

    // Sucesso
    router.push("/");

  } catch (err) {
    console.error(err);
    alert("Ocorreu um erro ao tentar eliminar a publicação.");
  }
}
</script>