<template>
  <div class="max-w-6xl mx-auto py-10 px-4">

    <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
      <div>
        <h2 class="text-3xl font-bold text-gray-800">As Minhas Tags</h2>
        <p class="text-gray-500 mt-2">Gere os tópicos que segues e acede rapidamente às publicações.</p>
      </div>

      <button
          @click="showExploreModal = true"
          class="bg-blue-600 text-white px-6 py-3 rounded-xl hover:bg-blue-700 transition flex items-center shadow-lg shadow-blue-200 font-bold transform hover:-translate-y-0.5"
      >
        <i class="bi bi-compass-fill mr-2"></i> Explorar Mais
      </button>
    </div>

    <div v-if="message" class="mb-6 p-4 rounded-xl bg-blue-50 text-blue-700 border border-blue-100 flex items-center animate-pulse font-medium">
      <i class="bi bi-info-circle-fill mr-3 text-xl"></i> {{ message }}
    </div>

    <div v-if="mySubscribedTags.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

      <div
          v-for="tag in mySubscribedTags"
          :key="tag.id"
          @click="goToTagPage(tag.name)"
          class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex justify-between items-center group hover:shadow-md hover:border-blue-300 transition cursor-pointer relative overflow-hidden"
      >
        <div class="absolute inset-y-0 left-0 w-1 bg-blue-500 transition-all group-hover:w-2"></div>

        <div class="flex items-center">
          <div class="w-12 h-12 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center mr-4 group-hover:bg-blue-600 group-hover:text-white transition">
            <i class="bi bi-hash text-xl"></i>
          </div>
          <div>
            <h3 class="font-bold text-gray-800 text-lg group-hover:text-blue-600 transition">{{ tag.name }}</h3>
            <p class="text-xs text-gray-400 font-medium">Ver publicações &rarr;</p>
          </div>
        </div>

        <button
            @click.stop="unsubscribe(tag.id, tag.name)"
            class="w-10 h-10 rounded-full text-gray-300 hover:bg-red-50 hover:text-red-500 transition flex items-center justify-center z-10"
            title="Deixar de seguir"
        >
          <i class="bi bi-trash-fill text-lg"></i>
        </button>
      </div>

    </div>

    <div v-else class="text-center py-20 bg-white rounded-3xl border border-dashed border-gray-300">
      <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gray-50 mb-6 text-gray-300">
        <i class="bi bi-tags-fill text-4xl"></i>
      </div>
      <h3 class="text-xl font-bold text-gray-800 mb-2">Ainda não segues nenhum tópico.</h3>
      <p class="text-gray-500 mb-8">Descobre novas tags para personalizares o teu feed.</p>
      <button @click="showExploreModal = true" class="text-blue-600 font-bold hover:text-blue-800 hover:underline">
        Começar a explorar agora
      </button>
    </div>

    <div v-if="showExploreModal" class="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4 backdrop-blur-sm transition-all">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[80vh] flex flex-col transform transition-all scale-100">

        <div class="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50/50 rounded-t-2xl">
          <div>
            <h3 class="text-xl font-bold text-gray-800">Explorar Tags</h3>
            <p class="text-xs text-gray-500">Encontra tópicos do teu interesse.</p>
          </div>
          <button @click="showExploreModal = false" class="text-gray-400 hover:text-gray-600 w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-200 transition">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div class="relative mb-6">
            <span class="absolute inset-y-0 left-0 flex items-center pl-4 text-gray-400"><i class="bi bi-search"></i></span>
            <input v-model="searchQuery" type="text" placeholder="Filtrar tags..." class="w-full pl-11 pr-4 py-3 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-500 outline-none transition bg-gray-50 focus:bg-white">
          </div>

          <div class="space-y-2">
            <div v-for="tag in paginatedTags" :key="tag.id" class="flex items-center justify-between p-4 hover:bg-gray-50 rounded-xl border border-transparent hover:border-gray-100 transition group">
              <span class="font-bold text-gray-700 flex items-center">
                <span class="w-8 h-8 rounded-lg bg-blue-100 text-blue-600 flex items-center justify-center mr-3 text-sm"><i class="bi bi-hash"></i></span>
                {{ tag.name }}
              </span>

              <button
                  v-if="amISubscribed(tag)"
                  @click="unsubscribe(tag.id, tag.name)"
                  class="text-xs font-bold text-gray-500 bg-white px-4 py-2 rounded-lg hover:bg-red-50 hover:text-red-600 transition border border-gray-200 shadow-sm"
              >
                Seguindo <i class="bi bi-check-circle-fill ml-1 text-green-500"></i>
              </button>

              <button
                  v-else
                  @click="subscribe(tag.id, tag.name)"
                  class="text-xs font-bold text-white bg-blue-600 px-4 py-2 rounded-lg hover:bg-blue-700 transition shadow-blue-200 shadow-sm"
              >
                Seguir
              </button>
            </div>
          </div>
        </div>

        <div class="p-4 border-t border-gray-100 flex justify-between items-center bg-gray-50 rounded-b-2xl">
          <button @click="currentPage--" :disabled="currentPage === 1" class="px-4 py-2 text-sm rounded-lg bg-white border border-gray-200 disabled:opacity-50 hover:bg-gray-100 font-bold text-gray-600">Anterior</button>
          <span class="text-xs font-bold text-gray-400 uppercase tracking-widest">Pág {{ currentPage }} / {{ totalPages }}</span>
          <button @click="currentPage++" :disabled="currentPage === totalPages" class="px-4 py-2 text-sm rounded-lg bg-white border border-gray-200 disabled:opacity-50 hover:bg-gray-100 font-bold text-gray-600">Seguinte</button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>

definePageMeta({
  middleware: ['role'],
  requiredRoles: ['Administrador', 'Responsavel'],
})
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

const router = useRouter()
const config = useRuntimeConfig()
const api = config.public.apiBase
const authStore = useAuthStore()
const { user, token } = storeToRefs(authStore)

const message = ref('')
const showExploreModal = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const itemsPerPage = 5
const tags = ref([])

onMounted(async () => {
  if (token.value) {
    await fetchTags()
  }
})

async function fetchTags() {
  try {
    const data = await $fetch(`${api}/tags`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    tags.value = data
  } catch (e) { console.error("Erro tags", e) }
}

function goToTagPage(tagName) {
  router.push(`/tags/${tagName}`)
}

const mySubscribedTags = computed(() => {
  if (!tags.value || !user.value) return []
  return tags.value.filter(tag => amISubscribed(tag))
})

function amISubscribed(tag) {
  if (!user.value) return false
  if (tag.subscribers && Array.isArray(tag.subscribers)) {
    return tag.subscribers.some(sub => sub.username === user.value.username);
  }
  if (tag.subscriberUsernames && Array.isArray(tag.subscriberUsernames)) {
    return tag.subscriberUsernames.includes(user.value.username);
  }
  return false;
}

const filteredTags = computed(() => {
  if (!tags.value) return []
  return tags.value.filter(t => t.name.toLowerCase().includes(searchQuery.value.toLowerCase()))
})

const totalPages = computed(() => Math.ceil(filteredTags.value.length / itemsPerPage) || 1)

const paginatedTags = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return filteredTags.value.slice(start, start + itemsPerPage)
})

async function subscribe(tagId, tagName) {
  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    await fetchTags()
    message.value = `Agora segues #${tagName}`
    setTimeout(() => message.value = '', 3000)
  } catch (e) { message.value = "Erro ao subscrever." }
}

async function unsubscribe(tagId, tagName) {
  if(!confirm(`Queres mesmo deixar de seguir #${tagName}?`)) return;

  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    await fetchTags()
    message.value = `Deixaste de seguir #${tagName}`
    setTimeout(() => message.value = '', 3000)
  } catch (e) { message.value = "Erro ao deixar de seguir." }
}
</script>