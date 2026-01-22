<template>
  <div class="max-w-6xl mx-auto px-4 py-8">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">Histórico de Alterações do Utilizador</h2>
        <p class="text-sm text-gray-500">User ID: {{ userId }}</p>
      </div>

      <button
          class="px-4 py-2 rounded-lg bg-gray-100 hover:bg-gray-200"
          @click="goBack"
      >
        Voltar
      </button>
    </div>

    <div v-if="loading" class="bg-white border rounded-xl p-6">
      A carregar histórico...
    </div>

    <div v-else class="bg-white border rounded-xl overflow-hidden">
      <table v-if="history.length" class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Publicação</th>
          <th class="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Campo</th>
          <th class="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Valor antigo</th>
          <th class="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Valor novo</th>
          <th class="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Data</th>
        </tr>
        </thead>

        <tbody class="divide-y divide-gray-100">
        <tr v-for="(h, idx) in history" :key="idx" class="hover:bg-gray-50">
          <td class="px-6 py-4 text-sm text-gray-700">
            {{ h.publicationTitle || h.publicationId }}
          </td>
          <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ h.fieldName }}</td>
          <td class="px-6 py-4 text-sm text-gray-700">{{ h.oldValue }}</td>
          <td class="px-6 py-4 text-sm text-gray-700">{{ h.newValue }}</td>
          <td class="px-6 py-4 text-sm text-gray-700">{{ formatDate(h.editedAt) }}</td>
        </tr>
        </tbody>
      </table>

      <div v-else class="p-8 text-center text-gray-500">
        Este utilizador ainda não tem alterações registadas.
      </div>
    </div>
  </div>
</template>

<script setup>
definePageMeta({
  middleware: ['role'],
  requiredRoles: ['Administrador'],
})

import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth-store.js'
import { storeToRefs } from 'pinia'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { token } = storeToRefs(authStore)

const config = useRuntimeConfig()
const api = config.public.apiBase

const userId = computed(() => route.params.id)

const history = ref([])
const loading = ref(true)

onMounted(async () => {
  if (!token.value) {
    router.push('/auth/login')
    return
  }

  try {
    // Endpoint novo (por editor)
    const res = await $fetch(`${api}/publicacoes/history/editor/${userId.value}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })

    history.value = res
  } catch (err) {
    console.error('Erro ao carregar histórico do utilizador', err)
    router.push('/users')
  } finally {
    loading.value = false
  }
})

function formatDate(date) {
  return new Date(date).toLocaleString('pt-PT')
}

function goBack() {
  router.back()
}
</script>
