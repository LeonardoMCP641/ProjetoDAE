<template>
  <div class="max-w-6xl mx-auto py-8 px-4">

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 mb-8 flex flex-col md:flex-row justify-between items-center text-center md:text-left">
      <div class="flex items-center mb-4 md:mb-0">
        <div class="w-16 h-16 bg-purple-100 text-purple-600 rounded-2xl flex items-center justify-center mr-5 shadow-sm transform -rotate-3">
          <i class="bi bi-hash text-3xl"></i>
        </div>
        <div>
          <h1 class="text-3xl font-extrabold text-gray-800 capitalize">{{ tagName }}</h1>
          <p class="text-gray-500 mt-1">Conhecimento arquivado sob este tópico.</p>
        </div>
      </div>

      <button
          v-if="!loadingTag"
          @click="toggleSubscription"
          class="px-6 py-3 rounded-xl font-bold transition shadow-md flex items-center"
          :class="isSubscribed ? 'bg-gray-100 text-gray-600 hover:bg-red-50 hover:text-red-600 border border-gray-200' : 'bg-purple-600 text-white hover:bg-purple-700 shadow-purple-200'"
      >
        <i class="bi mr-2 text-lg" :class="isSubscribed ? 'bi-check-circle-fill' : 'bi-plus-circle-fill'"></i>
        {{ isSubscribed ? 'Subscrito' : 'Subscrever' }}
      </button>
    </div>

    <div class="flex justify-end mb-6">
      <div class="flex items-center gap-3 bg-white p-2 rounded-xl border border-gray-100 shadow-sm">
        <span class="text-xs font-bold text-gray-400 uppercase ml-2">Ordenar por:</span>
        <select v-model="sortBy" @change="fetchPublicationsByTag" class="text-sm font-bold text-gray-700 outline-none bg-transparent cursor-pointer">
          <option value="date">📅 Recentes</option>
          <option value="popular">🔥 Populares</option>
          <option value="rating">⭐ Melhores</option>
        </select>
      </div>
    </div>

    <div v-if="loadingPubs" class="text-center py-20 text-gray-400">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600 mx-auto mb-4"></div>
      <p>A organizar publicações...</p>
    </div>

    <div v-else-if="publications.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      <div v-for="p in publications" :key="p.id" class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group flex flex-col h-full">

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
          </div>
        </div>

        <div class="px-6 py-4 bg-gray-50/50 border-t border-gray-100 flex justify-between items-center">
          <div class="flex items-center">
            <div class="w-8 h-8 rounded-full bg-white border border-gray-200 flex items-center justify-center text-xs font-bold text-gray-600 shadow-sm mr-3">
              {{ p.username ? p.username.charAt(0).toUpperCase() : 'U' }}
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

    <div v-else class="text-center py-20 bg-gray-50 rounded-2xl border border-dashed border-gray-200">
      <i class="bi bi-journal-x text-4xl text-gray-300 mb-3 block"></i>
      <p class="text-gray-500">Ainda não há publicações com a tag <strong>#{{ tagName }}</strong>.</p>
    </div>

  </div>
</template>

<script setup>

definePageMeta({
  middleware: ['role'],
  requiredRoles: ['Administrador', 'Responsavel'],
})
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const route = useRoute()
const tagName = route.params.name
const config = useRuntimeConfig()
const api = config.public.apiBase
const authStore = useAuthStore()
const { token, user } = storeToRefs(authStore)

const publications = ref([])
const currentTagId = ref(null)
const isSubscribed = ref(false)
const loadingPubs = ref(true)
const loadingTag = ref(true)
const sortBy = ref('date')

onMounted(async () => {
  if (!token.value) return
  if (!user.value) await authStore.fetchUser()
  await fetchTagDetails()
  await fetchPublicationsByTag()
})

async function fetchTagDetails() {
  try {
    const allTags = await $fetch(`${api}/tags`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    const tag = allTags.find(t => t.name.toLowerCase() === tagName.toLowerCase())
    if (tag) {
      currentTagId.value = tag.id
      if (tag.subscriberUsernames) {
        isSubscribed.value = tag.subscriberUsernames.includes(user.value.username)
      }
    }
  } catch (e) { console.error("Erro detalhes tag:", e) }
  finally { loadingTag.value = false }
}

  async function fetchPublicationsByTag() {
    loadingPubs.value = true;
    try {
      // Chamamos o motor de busca do Java com a tag e a ordenação escolhida
      const data = await $fetch(`${api}/publicacoes/pesquisa`, {
        headers: { Authorization: `Bearer ${token.value}` },
        params: {
          q: tagName,
          sortBy: sortBy.value
        }
      });

      // Filtramos apenas para garantir que estamos na tag certa, mas sem mudar a ordem!
      publications.value = data.filter(p =>
          p.visivel && p.tags && p.tags.some(t => t.toLowerCase() === tagName.toLowerCase())
      );
    } catch (e) {
      console.error("Erro na tag", e);
    } finally {
      loadingPubs.value = false;
    }
  }

async function toggleSubscription() {
  if (!currentTagId.value) return
  try {
    const method = isSubscribed.value ? 'DELETE' : 'POST'
    await $fetch(`${api}/tags/${currentTagId.value}/subscricao`, {
      method: method,
      headers: { Authorization: `Bearer ${token.value}` }
    })
    isSubscribed.value = !isSubscribed.value
  } catch (e) { alert("Erro ao alterar subscrição.") }
}
</script>