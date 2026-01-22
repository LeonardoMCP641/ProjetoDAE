<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

    <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
      <div>
        <h2 class="text-3xl font-bold text-gray-800">Os Meus Uploads 📂</h2>
        <p class="text-gray-500 mt-2">Gere as tuas publicações, consulta estatísticas e edita conteúdos.</p>
      </div>
      <NuxtLink
          to="/publications/create"
          class="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-blue-700 transition shadow-lg shadow-blue-200 transform hover:-translate-y-0.5 flex items-center"
      >
        <i class="bi bi-cloud-upload-fill mr-2"></i> Submeter Novo
      </NuxtLink>
    </div>

    <div v-if="loading" class="text-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      <p class="text-gray-400 mt-4 text-sm font-bold animate-pulse">A carregar o teu arquivo...</p>
    </div>

    <div v-else-if="myPublications.length === 0" class="bg-white rounded-3xl border border-dashed border-gray-300 p-12 text-center">
      <div class="w-20 h-20 bg-blue-50 text-blue-400 rounded-full flex items-center justify-center mx-auto mb-6 text-3xl">
        <i class="bi bi-folder-x"></i>
      </div>
      <h3 class="text-xl font-bold text-gray-800 mb-2">Ainda não publicaste nada.</h3>
      <p class="text-gray-500 mb-6">Partilha o teu primeiro projeto com a comunidade!</p>
      <NuxtLink to="/publications/create" class="text-blue-600 font-bold hover:underline">
        Começar agora &rarr;
      </NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

      <div
          v-for="p in myPublications"
          :key="p.id"
          class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition group flex flex-col h-full"
      >

        <div class="p-5 pb-0 flex justify-between items-start">
          <div class="flex gap-2">
            <span class="px-2 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider bg-gray-100 text-gray-600 border border-gray-200">
              {{ p.area }}
            </span>
            <span
                class="px-2 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider border flex items-center gap-1"
                :class="p.visivel ? 'bg-green-50 text-green-700 border-green-100' : 'bg-red-50 text-red-700 border-red-100'"
            >
              <i class="bi" :class="p.visivel ? 'bi-unlock-fill' : 'bi-lock-fill'"></i>
              {{ p.visivel ? 'Público' : 'Privado' }}
            </span>
          </div>

          <div class="flex gap-1">
            <NuxtLink :to="`/publications/edit/${p.id}`" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-blue-50 text-gray-400 hover:text-blue-600 transition" title="Editar">
              <i class="bi bi-pencil-fill text-xs"></i>
            </NuxtLink>
            <button @click="deletePublication(p.id)" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-600 transition" title="Apagar">
              <i class="bi bi-trash-fill text-xs"></i>
            </button>
          </div>
        </div>

        <div class="p-5 flex-1">
          <NuxtLink :to="`/publications/${p.id}`" class="block">
            <h3 class="text-lg font-bold text-gray-800 mb-2 leading-tight group-hover:text-blue-600 transition line-clamp-2">
              {{ p.titulo }}
            </h3>
          </NuxtLink>

          <div class="flex items-center mb-3">
            <div class="flex text-yellow-400 text-xs mr-2">
              <i v-for="star in 5" :key="star" class="bi" :class="star <= Math.round(p.ratingAverage || 0) ? 'bi-star-fill' : 'bi-star text-gray-200'"></i>
            </div>
            <span class="text-xs text-gray-400 font-medium">({{ p.ratingCount || 0 }} votos)</span>
          </div>

          <p class="text-sm text-gray-500 line-clamp-3 mb-4">
            {{ p.resumoCurto }}
          </p>
        </div>

        <div class="px-5 py-3 bg-gray-50 border-t border-gray-100 flex justify-between items-center text-xs font-medium text-gray-500">
          <div class="flex gap-3">
            <span class="flex items-center" title="Comentários">
              <i class="bi bi-chat-dots-fill mr-1 text-blue-400"></i> {{ p.comments ? p.comments.length : 0 }}
            </span>
            <span class="flex items-center" title="Tipo">
              <i class="bi bi-file-earmark-text-fill mr-1 text-purple-400"></i> {{ p.tipo }}
            </span>
          </div>

          <NuxtLink :to="`/publications/${p.id}`" class="text-blue-600 hover:text-blue-800 font-bold flex items-center">
            Ver detalhes <i class="bi bi-arrow-right-short text-lg"></i>
          </NuxtLink>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useAuthStore } from "~/stores/auth-store.js";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";

const config = useRuntimeConfig();
const api = config.public.apiBase;
const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);
const publicationStore = usePublicationStore();
const loading = ref(true);

onMounted(async () => {
  if (token.value) {
    await publicationStore.fetchAll(token.value);
    loading.value = false;
  }
});

const myPublications = computed(() => {
  if (!user.value) return [];
  return publicationStore.publications.filter(p => p.username === user.value.username);
});

async function deletePublication(id) {
  if (!confirm("Tens a certeza que queres apagar esta publicação? Não podes voltar atrás!")) return;

  try {
    await $fetch(`${api}/publicacoes/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token.value}` }
    });
    await publicationStore.fetchAll(token.value);
  } catch (e) {
    alert("Erro ao apagar publicação.");
  }
}
</script>