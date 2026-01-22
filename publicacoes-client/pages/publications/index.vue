<template>
  <div>
    <div class="flex justify-between items-center mb-8">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">Explorar Publicações</h2>
        <p class="text-gray-500">Descobre o conhecimento partilhado pelo centro.</p>
      </div>

      <div class="relative w-1/3 hidden md:block">
        <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
          <i class="bi bi-search"></i>
        </span>
        <input
            v-model="searchQuery"
            type="text"
            class="w-full py-2.5 pl-10 pr-4 bg-white border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition shadow-sm"
            placeholder="Filtrar por título..."
        >
      </div>
    </div>

    <div v-if="loading" class="flex flex-col items-center justify-center py-20 text-gray-400">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-3"></div>
      <p>A carregar biblioteca...</p>
    </div>

    <div v-else class="space-y-6">

      <div v-for="pub in filteredPublications" :key="pub.id" class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition duration-300 group">

        <div class="flex flex-col md:flex-row justify-between items-start gap-4">
          <div class="flex-1">

            <div class="flex items-center gap-3 mb-2">
              <h3 class="text-xl font-bold text-gray-800 group-hover:text-blue-600 transition">
                <NuxtLink :to="`/publications/${pub.id}`">{{ pub.titulo }}</NuxtLink>
              </h3>
              <span v-if="pub.resumoCurto" class="px-2 py-0.5 bg-blue-50 text-blue-600 text-[10px] font-bold uppercase tracking-wider rounded border border-blue-100 flex items-center">
                <i class="bi bi-stars mr-1"></i> AI Summary
              </span>
            </div>

            <div class="flex flex-wrap gap-2 mb-3">
              <span class="px-2 py-1 bg-gray-100 text-gray-600 text-xs font-medium rounded">
                {{ pub.area }}
              </span>
              <span v-for="tag in pub.tags" :key="tag.id" class="px-2 py-1 bg-blue-50 text-blue-600 text-xs font-medium rounded">
                #{{ tag.name }}
              </span>
            </div>

            <div class="flex items-center mb-4 space-x-1">
              <div class="flex text-yellow-400 text-sm">
                <i v-for="n in 5" :key="n" class="bi"
                   :class="n <= Math.round(pub.ratingAverage || 0) ? 'bi-star-fill' : 'bi-star text-gray-300'"></i>
              </div>
              <span class="text-xs text-gray-400 ml-2">({{ pub.ratingCount || 0 }} votos)</span>
            </div>

            <p class="text-gray-600 text-sm mb-4 line-clamp-2 bg-gray-50 p-3 rounded-lg border-l-4 border-gray-200 italic">
              "{{ pub.resumoCurto || 'Sem resumo disponível.' }}"
            </p>

            <div class="flex items-center text-xs text-gray-500">
              <div class="w-6 h-6 rounded-full bg-gray-200 flex items-center justify-center font-bold text-gray-600 mr-2">
                {{ pub.username ? pub.username.charAt(0).toUpperCase() : '?' }}
              </div>
              <span>Publicado por <span class="font-bold text-gray-700">{{ pub.username }}</span></span>
            </div>
          </div>

          <div class="flex flex-row md:flex-col gap-2 min-w-[140px]">
            <button class="flex-1 px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition flex items-center justify-center shadow-sm">
              <i class="bi bi-file-earmark-pdf mr-2"></i> PDF
            </button>

            <NuxtLink :to="`/publications/${pub.id}`" class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition flex items-center justify-center shadow-sm shadow-blue-100">
              <i class="bi bi-chat-left-text mr-2"></i> Ver
            </NuxtLink>
          </div>
        </div>

      </div>

      <div v-if="filteredPublications.length === 0" class="text-center py-20 bg-gray-50 rounded-xl border border-dashed border-gray-300">
        <i class="bi bi-search text-3xl text-gray-300 mb-2 block"></i>
        <p class="text-gray-500">Não encontrámos publicações.</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '~/stores/auth-store.js'
import { usePublicationStore } from '~/stores/publication-store'
import { storeToRefs } from 'pinia'

const authStore = useAuthStore()
const { token } = storeToRefs(authStore)
const publicationStore = usePublicationStore()

const loading = ref(true)
const searchQuery = ref('')

const filteredPublications = computed(() => {
  if (!searchQuery.value) return publicationStore.publications

  const query = searchQuery.value.toLowerCase()
  return publicationStore.publications.filter(pub =>
      pub.titulo.toLowerCase().includes(query)
  )
})

onMounted(async () => {
  try {
    await publicationStore.fetchAll(token.value)
  } catch (e) {
    console.error("Erro ao carregar publicações", e)
  } finally {
    loading.value = false
  }
})
</script>