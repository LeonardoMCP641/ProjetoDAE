<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

    <div class="mb-10 text-center max-w-2xl mx-auto">
      <h2 class="text-3xl font-bold text-gray-800 mb-4">Explorar <span class="text-blue-600">Conhecimento</span> 🔭</h2>
      <p class="text-gray-500">
        Navega por todo o arquivo científico do centro. Usa a pesquisa para encontrar tópicos específicos.
      </p>
    </div>

    <div class="max-w-3xl mx-auto mb-12 relative group">
      <div class="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none text-gray-400 group-focus-within:text-blue-500 transition">
        <i class="bi bi-search text-xl"></i>
      </div>
      <input
          v-model="searchQuery"
          type="text"
          class="block w-full pl-14 pr-6 py-5 bg-white border border-gray-200 rounded-full shadow-lg shadow-blue-50 text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-4 focus:ring-blue-100 focus:border-blue-500 transition text-lg"
          placeholder="Pesquisar por título, autor, tag ou área..."
      />
    </div>

    <div v-if="loading" class="text-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
    </div>

    <div v-else-if="filteredPublications.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">

      <div v-for="p in filteredPublications" :key="p.id" class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group flex flex-col h-full">

        <div class="p-6 pb-0 flex justify-between items-start">
          <div class="flex gap-2">
            <span class="px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider bg-blue-50 text-blue-700 border border-blue-100">
              {{ p.area }}
            </span>
          </div>
          <span class="text-xs font-bold text-gray-400 uppercase tracking-wider flex items-center">
             {{ p.tipo }}
          </span>
        </div>

        <div class="p-6 flex-1">
          <NuxtLink :to="`/publications/${p.id}`" class="block">
            <h3 class="text-xl font-bold text-gray-800 mb-2 leading-tight group-hover:text-blue-600 transition line-clamp-2">
              {{ p.titulo }}
            </h3>
          </NuxtLink>

          <div class="flex items-center mb-4">
            <div class="flex text-yellow-400 text-xs mr-2">
              <i v-for="star in 5" :key="star" class="bi" :class="star <= Math.round(p.ratingAverage || 0) ? 'bi-star-fill' : 'bi-star text-gray-200'"></i>
            </div>
            <span class="text-xs text-gray-400 font-medium">({{ p.ratingCount || 0 }})</span>
          </div>

          <p class="text-sm text-gray-500 line-clamp-3 mb-4 leading-relaxed">
            {{ p.resumoCurto }}
          </p>

          <div v-if="p.tags && p.tags.length" class="flex flex-wrap gap-1 mb-2">
            <span v-for="tag in p.tags.slice(0, 3)" :key="tag" class="text-[10px] text-gray-500 bg-gray-50 px-2 py-1 rounded border border-gray-100">
              #{{ tag }}
            </span>
            <span v-if="p.tags.length > 3" class="text-[10px] text-gray-400 px-1">...</span>
          </div>
        </div>

        <div class="px-6 py-4 bg-gray-50/50 border-t border-gray-100 flex justify-between items-center">
          <div class="flex items-center">
            <div class="w-8 h-8 rounded-full bg-white border border-gray-200 flex items-center justify-center text-xs font-bold text-gray-600 shadow-sm mr-3">
              {{ p.username.charAt(0).toUpperCase() }}
            </div>
            <div class="text-xs">
              <p class="font-bold text-gray-700">{{ p.username }}</p>
            </div>
          </div>

          <div class="flex items-center gap-3 text-xs font-bold text-gray-400">
            <span class="flex items-center" title="Comentários">
              <i class="bi bi-chat-fill mr-1 text-blue-300"></i> {{ p.comments ? p.comments.length : 0 }}
            </span>
            <NuxtLink :to="`/publications/${p.id}`" class="w-8 h-8 flex items-center justify-center rounded-full bg-white hover:bg-blue-600 hover:text-white transition shadow-sm text-blue-600">
              <i class="bi bi-arrow-right"></i>
            </NuxtLink>
          </div>
        </div>

      </div>
    </div>

    <div v-else class="text-center py-20 bg-white rounded-3xl border border-dashed border-gray-200">
      <i class="bi bi-search text-4xl text-gray-300 mb-4 block"></i>
      <p class="text-gray-500 font-medium">Nenhuma publicação encontrada para a tua pesquisa.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useAuthStore } from "~/stores/auth-store.js";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const { token } = storeToRefs(authStore);
const publicationStore = usePublicationStore();
const loading = ref(true);
const searchQuery = ref('');

onMounted(async () => {
  if (token.value) {
    await publicationStore.fetchAll(token.value);
    loading.value = false;
  }
});

const filteredPublications = computed(() => {
  let pubs = publicationStore.publications.filter(p => p.visivel);
  if (!searchQuery.value) return pubs;
  const search = searchQuery.value.toLowerCase();
  return pubs.filter(p =>
      p.titulo.toLowerCase().includes(search) ||
      p.area.toLowerCase().includes(search) ||
      (p.tags && p.tags.some(t => t.toLowerCase().includes(search))) ||
      (p.autores && p.autores.join(' ').toLowerCase().includes(search))
  );
});
</script>