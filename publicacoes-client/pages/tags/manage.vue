<template>
  <div class="max-w-5xl mx-auto py-10 px-4">

    <div class="mb-8">
      <h2 class="text-3xl font-bold text-gray-800">Gestão de Tags</h2>
      <p class="text-gray-500 mt-2">Criar e remover tags globais do sistema.</p>
    </div>

    <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 mb-8">
      <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-4">Adicionar Nova Tag</h3>
      <form @submit.prevent="createTag" class="flex gap-4">
        <input
            v-model="newTagName"
            type="text"
            placeholder="Ex: Inteligência Artificial"
            class="flex-1 px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50 focus:bg-white transition"
            required
        >
        <button
            type="submit"
            class="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-blue-700 transition flex items-center"
        >
          <i class="bi bi-plus-lg mr-2"></i> Criar
        </button>
      </form>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 border-b border-gray-100">
          <tr>
            <th class="px-6 py-4 text-left text-xs font-bold text-gray-400 uppercase tracking-wider">Nome da Tag</th>
            <th class="px-6 py-4 text-left text-xs font-bold text-gray-400 uppercase tracking-wider">Utilização</th>
            <th class="px-6 py-4 text-right text-xs font-bold text-gray-400 uppercase tracking-wider">Ações</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
          <tr v-for="tag in tags" :key="tag.id" class="hover:bg-blue-50/30 transition">

            <td class="px-6 py-4 whitespace-nowrap">
              <span class="font-bold text-gray-800">#{{ tag.name }}</span>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
                <span class="text-sm text-gray-600 bg-gray-100 px-3 py-1 rounded-full border border-gray-200">
                  {{ tag.usageCount }} seguidores
                </span>
            </td>

            <td class="px-6 py-4 whitespace-nowrap text-right">
              <button
                  @click="deleteTag(tag.id)"
                  class="text-red-500 hover:text-red-700 text-sm font-bold border border-red-100 hover:bg-red-50 px-3 py-1.5 rounded-lg transition"
              >
                <i class="bi bi-trash mr-1"></i> Apagar
              </button>
            </td>

          </tr>
          <tr v-if="tags.length === 0">
            <td colspan="3" class="px-6 py-8 text-center text-gray-400 text-sm">
              Ainda não existem tags no sistema.
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script setup>

definePageMeta({
  middleware: ['role'],
  requiredRoles: ['Administrador', 'Responsavel'],
})

import { ref, onMounted } from 'vue'
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const config = useRuntimeConfig()
const api = config.public.apiBase
const authStore = useAuthStore()
const { token } = storeToRefs(authStore)

const tags = ref([])
const newTagName = ref('')

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
  } catch (e) { console.error("Erro ao carregar tags", e) }
}

async function createTag() {
  if (!newTagName.value.trim()) return
  try {
    await $fetch(`${api}/tags`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { name: newTagName.value }
    })
    newTagName.value = ''
    await fetchTags()
  } catch (e) { alert("Erro ao criar tag.") }
}

async function deleteTag(tagId) {
  if (!confirm("Tem a certeza? Isto irá remover a tag de todas as publicações.")) return
  try {
    await $fetch(`${api}/tags/${tagId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    await fetchTags()
  } catch (e) { alert("Erro ao apagar tag.") }
}
</script>