<template>
  <div v-if="publication" class="max-w-4xl mx-auto">

    <div class="bg-gradient-to-r from-blue-600 to-blue-800 rounded-t-2xl p-8 text-white shadow-lg">
      <div class="flex justify-between items-start">
        <span class="bg-white/20 backdrop-blur-sm px-3 py-1 rounded-full text-sm font-semibold border border-white/30">
          {{ publication.area }}
        </span>

        <div class="flex gap-2">
          <button v-if="isOwner" @click="goToEdit" class="bg-white/20 hover:bg-white/30 p-2 rounded-lg transition backdrop-blur-md">
            <i class="bi bi-pencil-fill"></i>
          </button>
          <button v-if="isOwner" @click="deletePublication" class="bg-red-500/80 hover:bg-red-500 p-2 rounded-lg transition backdrop-blur-md">
            <i class="bi bi-trash-fill"></i>
          </button>
        </div>
      </div>

      <h1 class="text-3xl font-bold mt-4 mb-2 leading-tight">{{ publication.titulo }}</h1>

      <div class="flex items-center text-blue-100 text-sm mt-4">
        <i class="bi bi-person-fill mr-2"></i>
        <span class="font-semibold mr-4">{{ publication.username }}</span>

        <i class="bi bi-calendar-event mr-2"></i>
        <span>{{ new Date(publication.publicationDate || Date.now()).toLocaleDateString() }}</span>
      </div>
    </div>

    <div class="bg-white rounded-b-2xl shadow-sm border-x border-b border-gray-100 p-8">

      <div class="mb-8">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">Autores</h3>
        <div class="flex flex-wrap gap-2">
          <span v-for="autor in publication.autores" :key="autor" class="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm font-medium">
            {{ autor }}
          </span>
        </div>
      </div>

      <div class="mb-8">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">Resumo</h3>
        <p class="text-gray-700 leading-relaxed text-lg bg-gray-50 p-6 rounded-xl border-l-4 border-blue-500">
          {{ publication.resumoCurto }}
        </p>
      </div>

      <div class="flex gap-4 border-t border-gray-100 pt-6">
        <button
            v-if="publication.filename"
            @click="downloadPdf"
            class="flex items-center px-6 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition shadow-md"
        >
          <i class="bi bi-file-earmark-pdf-fill mr-2 text-xl"></i>
          Download PDF
        </button>
        <button v-else class="px-6 py-3 bg-gray-100 text-gray-400 rounded-xl font-bold cursor-not-allowed">
          Sem PDF Disponível
        </button>
      </div>

    </div>

    <div class="mt-8 bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
      <h3 class="text-xl font-bold text-gray-800 mb-4">Comentários</h3>
      <p class="text-gray-500 italic">Ainda não há comentários.</p>
    </div>

  </div>

  <div v-else class="text-center py-20">
    <div class="spinner-border text-blue-600" role="status"></div>
    <p class="mt-2 text-gray-500">A carregar...</p>
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

onMounted(async () => {
  if (!token.value) { router.push("/auth/login"); return; }
  try {
    if (!authStore.user) await authStore.fetchUser();

    const id = route.params.id;
    const res = await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    publication.value = res;
    isOwner.value = authStore.user.username === publication.value.username;
  } catch (err) { router.push("/"); }
});

async function downloadPdf() {
  try {
    const id = route.params.id;
    const pdfBlob = await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${id}/download`, {
      headers: { Authorization: `Bearer ${token.value}` },
      responseType: "blob",
    });
    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${publication.value.titulo}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  } catch (err) { alert("Erro no download"); }
}

function goToEdit() { router.push(`/publications/edit/${publication.value.id}`); }

async function deletePublication() {
  if (!confirm("Eliminar publicação?")) return;
  try {
    await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${route.params.id}`, {
      method: "DELETE", headers: { Authorization: `Bearer ${token.value}` },
    });
    router.push("/");
  } catch (err) { alert("Erro ao eliminar"); }
}
</script>