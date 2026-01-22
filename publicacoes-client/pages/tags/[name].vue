<template>
  <div class="max-w-6xl mx-auto py-8 px-4">

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 mb-8 flex flex-col md:flex-row justify-between items-center text-center md:text-left">
      <div class="flex items-center mb-4 md:mb-0">
        <div class="w-16 h-16 bg-purple-100 text-purple-600 rounded-2xl flex items-center justify-center mr-5 shadow-sm transform -rotate-3">
          <i class="bi bi-hash text-3xl"></i>
        </div>
        <div>
          <h1 class="text-3xl font-extrabold text-gray-800 capitalize">{{ tagName }}</h1>
          <p class="text-gray-500 mt-1">Todas as publicações sobre este tópico.</p>
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

    <div v-if="loadingPubs" class="text-center py-20 text-gray-400">
      <div class="spinner-border text-purple-600 mb-2"></div>
      <p>A procurar publicações...</p>
    </div>

    <div v-else-if="publications.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <NuxtLink v-for="p in publications" :key="p.id" :to="`/publications/${p.id}`" class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition hover:-translate-y-1 block group">
        <div class="flex justify-between mb-3">
          <span class="bg-blue-50 text-blue-700 px-2 py-1 rounded text-xs font-bold uppercase">{{ p.area }}</span>
          <span class="text-xs text-gray-400">{{ p.tipo }}</span>
        </div>
        <h3 class="text-lg font-bold text-gray-800 mb-2 group-hover:text-blue-600 transition">{{ p.titulo }}</h3>
        <p class="text-sm text-gray-500 line-clamp-3 mb-4">{{ p.resumoCurto }}</p>
        <div class="border-t border-gray-50 pt-4 flex items-center text-xs text-gray-400">
          <i class="bi bi-person-fill mr-1"></i> {{ p.username }}
        </div>
      </NuxtLink>
    </div>

    <div v-else class="text-center py-20 bg-gray-50 rounded-2xl border border-dashed border-gray-200">
      <i class="bi bi-journal-x text-4xl text-gray-300 mb-3 block"></i>
      <p class="text-gray-500">Ainda não há publicações com a tag <strong>#{{ tagName }}</strong>.</p>
    </div>

  </div>
</template>

<script setup>
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
      if (tag.subscribers) {
        isSubscribed.value = tag.subscribers.some(sub => sub.username === user.value.username)
      }
    }
  } catch (e) { console.error("Erro tag", e) }
  finally { loadingTag.value = false }
}

async function fetchPublicationsByTag() {
  try {
    const allPubs = await $fetch(`${api}/publicacoes`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })

    publications.value = allPubs.filter(p =>
        p.tags && p.tags.some(t => t.toLowerCase() === tagName.toLowerCase())
    )
  } catch (e) { console.error("Erro pubs", e) }
  finally { loadingPubs.value = false }
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