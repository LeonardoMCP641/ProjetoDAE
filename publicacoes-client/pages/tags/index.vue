<template>
  <div class="max-w-5xl mx-auto">

    <div class="flex justify-between items-end mb-8">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">
          {{ isAdminOrResponsible ? 'Gestão de Tags' : 'As Minhas Tags' }}
        </h2>
        <p class="text-gray-500 text-sm">
          {{ isAdminOrResponsible ? 'Criar, editar e remover tags do sistema.' : 'Gere as tuas subscrições.' }}
        </p>
      </div>

      <button
          v-if="!isAdminOrResponsible"
          @click="showExploreModal = true"
          class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition flex items-center shadow-sm"
      >
        <i class="bi bi-compass mr-2"></i> Explorar Mais Tags
      </button>
    </div>

    <div v-if="message" class="mb-4 p-4 rounded-lg bg-blue-50 text-blue-700 border border-blue-100 flex items-center justify-between">
      <span><i class="bi bi-info-circle-fill mr-2"></i> {{ message }}</span>
      <button @click="message = ''" class="text-blue-400 hover:text-blue-600"><i class="bi bi-x"></i></button>
    </div>

    <div v-if="isAdminOrResponsible" class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">

      <div class="p-6 bg-gray-50 border-b border-gray-100 flex gap-4">
        <input
            v-model="newTagName"
            @keyup.enter="createTag"
            type="text"
            placeholder="Nome da nova tag..."
            class="flex-1 px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none"
        >
        <button @click="createTag" class="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 font-medium">
          <i class="bi bi-plus-lg mr-1"></i> Criar
        </button>
      </div>

      <table class="w-full text-left">
        <thead class="bg-gray-50 text-gray-600 font-semibold text-sm uppercase tracking-wider">
        <tr>
          <th class="px-6 py-4">Nome da Tag</th>
          <th class="px-6 py-4">Subscritores</th>
          <th class="px-6 py-4 text-right">Ações</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
        <tr v-for="tag in tags" :key="tag.id" class="hover:bg-gray-50 transition">
          <td class="px-6 py-4 font-medium text-gray-800">
            #{{ tag.name }}
          </td>
          <td class="px-6 py-4 text-gray-500 text-sm">
            {{ tag.subscriberUsernames ? tag.subscriberUsernames.length : 0 }} users
          </td>
          <td class="px-6 py-4 text-right space-x-2">
            <button @click="deleteTag(tag.id)" class="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 p-2 rounded transition" title="Apagar">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-else>
      <div v-if="mySubscribedTags.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="tag in mySubscribedTags" :key="tag.id" class="bg-white p-5 rounded-xl shadow-sm border border-gray-100 flex justify-between items-center group hover:border-blue-300 transition">
          <div>
            <h3 class="font-bold text-gray-800 text-lg">#{{ tag.name }}</h3>
            <p class="text-xs text-gray-400">Subscrita</p>
          </div>
          <button @click="unsubscribe(tag.id, tag.name)" class="text-gray-400 hover:text-red-500 transition" title="Remover subscrição">
            <i class="bi bi-x-circle-fill text-xl"></i>
          </button>
        </div>
      </div>

      <div v-else class="text-center py-12 bg-white rounded-xl border border-dashed border-gray-300">
        <i class="bi bi-tags text-4xl text-gray-300 mb-3 block"></i>
        <p class="text-gray-500">Ainda não subscreveste nenhuma tag.</p>
        <button @click="showExploreModal = true" class="mt-4 text-blue-600 font-bold hover:underline">
          Explorar Tags
        </button>
      </div>
    </div>

    <div v-if="showExploreModal" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 backdrop-blur-sm">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[80vh] flex flex-col">

        <div class="p-6 border-b border-gray-100 flex justify-between items-center">
          <h3 class="text-xl font-bold text-gray-800">Explorar Tags</h3>
          <button @click="showExploreModal = false" class="text-gray-400 hover:text-gray-600">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <input v-model="searchQuery" type="text" placeholder="Pesquisar tag..." class="w-full mb-4 px-4 py-2 border rounded-lg text-sm bg-gray-50 focus:bg-white transition outline-none focus:ring-2 focus:ring-blue-100">

          <table class="w-full">
            <tbody>
            <tr v-for="tag in paginatedTags" :key="tag.id" class="border-b border-gray-50 last:border-0">
              <td class="py-3 font-medium text-gray-700">#{{ tag.name }}</td>
              <td class="py-3 text-right">
                <button
                    v-if="amISubscribed(tag)"
                    @click="unsubscribe(tag.id, tag.name)"
                    class="text-xs font-bold text-gray-500 bg-gray-100 px-3 py-1.5 rounded-full hover:bg-red-50 hover:text-red-600 transition"
                >
                  Subscrito <i class="bi bi-check"></i>
                </button>
                <button
                    v-else
                    @click="subscribe(tag.id, tag.name)"
                    class="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1.5 rounded-full hover:bg-blue-600 hover:text-white transition"
                >
                  Subscrever
                </button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div class="p-4 border-t border-gray-100 flex justify-between items-center bg-gray-50 rounded-b-2xl">
          <button
              @click="currentPage--"
              :disabled="currentPage === 1"
              class="px-3 py-1 text-sm rounded bg-white border border-gray-200 disabled:opacity-50 hover:bg-gray-100"
          >
            Anterior
          </button>
          <span class="text-sm text-gray-500">Página {{ currentPage }} de {{ totalPages }}</span>
          <button
              @click="currentPage++"
              :disabled="currentPage === totalPages"
              class="px-3 py-1 text-sm rounded bg-white border border-gray-200 disabled:opacity-50 hover:bg-gray-100"
          >
            Seguinte
          </button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const config = useRuntimeConfig()
const api = config.public.apiBase
const authStore = useAuthStore()
const { user, token } = storeToRefs(authStore)

// Estado
const message = ref('')
const newTagName = ref('')
const showExploreModal = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const itemsPerPage = 6 // Tags por página no modal

// 1. Fetch das Tags
const { data: tags, refresh } = await useFetch(`${api}/tags`, {
  headers: { Authorization: `Bearer ${token.value}` }
})

// 2. Computed: Permissões
const isAdminOrResponsible = computed(() => {
  return user.value?.role === 'Administrador' || user.value?.role === 'Responsavel'
})

// 3. Computed: Tags do User (Para a vista de Colaborador)
const mySubscribedTags = computed(() => {
  if (!tags.value || !user.value) return []
  return tags.value.filter(tag =>
      tag.subscriberUsernames && tag.subscriberUsernames.includes(user.value.username)
  )
})

// 4. Lógica de Paginação e Filtro (Cliente)
const filteredTags = computed(() => {
  if (!tags.value) return []
  return tags.value.filter(t => t.name.toLowerCase().includes(searchQuery.value.toLowerCase()))
})

const totalPages = computed(() => Math.ceil(filteredTags.value.length / itemsPerPage))

const paginatedTags = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredTags.value.slice(start, end)
})

// 5. Funções Auxiliares
function amISubscribed(tag) {
  if (!user.value || !tag.subscriberUsernames) return false
  return tag.subscriberUsernames.includes(user.value.username)
}

// 6. Ações (CRUD)
async function createTag() {
  if (!newTagName.value.trim()) return
  try {
    await $fetch(`${api}/tags`, {
      method: 'POST',
      body: { name: newTagName.value },
      headers: { Authorization: `Bearer ${token.value}` }
    })
    message.value = 'Tag criada com sucesso!'
    newTagName.value = ''
    refresh()
  } catch (e) { message.value = 'Erro ao criar tag.' }
}

async function deleteTag(id) {
  if (!confirm("Tens a certeza que queres apagar esta tag? Isto afeta todas as publicações.")) return
  try {
    await $fetch(`${api}/tags/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    message.value = 'Tag eliminada.'
    refresh()
  } catch (e) { message.value = 'Erro ao eliminar tag.' }
}

async function subscribe(tagId, tagName) {
  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    // Pequeno feedback visual sem spam
    refresh()
  } catch (e) { message.value = "Erro ao subscrever." }
}

async function unsubscribe(tagId, tagName) {
  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    refresh()
  } catch (e) { message.value = "Erro ao anular subscrição." }
}
</script>