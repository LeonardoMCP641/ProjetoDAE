<template>
  <div class="max-w-3xl mx-auto">

    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">Submeter Nova Publicação</h2>
      <p class="text-gray-500 text-sm">Preenche os dados abaixo para partilhar conhecimento com o centro.</p>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-8">
      <form @submit.prevent="submit" class="space-y-6">

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Título da Publicação</label>
          <input
              v-model="titulo"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Ex: Deep Learning em Medicina"
              required
          />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">Autores</label>
            <input
                v-model="autores"
                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                placeholder="Ex: Ana Silva, João Santos"
            />
            <p class="text-xs text-gray-400 mt-1">Separados por vírgula</p>
          </div>
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">Área Científica</label>
            <input
                v-model="area"
                class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                placeholder="Ex: Ciência de Dados"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Tipo de Publicação</label>
          <select v-model="tipo" class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition bg-white">
            <option value="" disabled>Seleciona um tipo</option>
            <option value="Artigo">Artigo Científico</option>
            <option value="Tese">Tese</option>
            <option value="Relatório">Relatório Técnico</option>
            <option value="Livro">Livro / Capítulo</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Resumo Curto</label>
          <textarea
              v-model="resumoCurto"
              rows="4"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="Escreve um breve resumo sobre o conteúdo..."
          ></textarea>
        </div>

        <div class="p-4 bg-gray-50 rounded-lg border border-gray-200">
          <div class="flex items-center justify-between mb-4">
            <label class="text-sm font-bold text-gray-700">Ficheiro (PDF ou ZIP)</label>

            <div class="flex items-center">
              <input type="checkbox" id="visivel" v-model="visivel" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500 border-gray-300">
              <label for="visivel" class="ml-2 text-sm font-medium text-gray-700">Tornar Público?</label>
            </div>
          </div>

          <input
              type="file"
              @change="onFileChange"
              class="block w-full text-sm text-gray-500
              file:mr-4 file:py-2 file:px-4
              file:rounded-full file:border-0
              file:text-sm file:font-semibold
              file:bg-blue-50 file:text-blue-700
              hover:file:bg-blue-100 transition"
          />
        </div>

        <button class="w-full bg-blue-600 text-white font-bold py-3 px-4 rounded-lg hover:bg-blue-700 shadow-md hover:shadow-lg transition transform hover:-translate-y-0.5">
          <i class="bi bi-send-check mr-2"></i> Submeter Publicação
        </button>

      </form>

      <div v-if="message" class="mt-4 p-4 rounded-lg bg-blue-50 text-blue-700 flex items-center">
        <i class="bi bi-info-circle-fill mr-2"></i> {{ message }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '~/stores/auth-store'
import { usePublicationStore } from '~/stores/publication-store'

const authStore = useAuthStore()
const { token } = storeToRefs(authStore)
const publicationStore = usePublicationStore()

const titulo = ref('')
const autores = ref('')
const area = ref('')
const tipo = ref('')
const resumoCurto = ref('')
const visivel = ref(true)
const file = ref(null)
const message = ref('')

function onFileChange(e) {
  file.value = e.target.files[0]
}

async function submit() {
  try {
    const publication = await publicationStore.create({
      titulo: titulo.value,
      autores: autores.value.split(',').map(a => a.trim()),
      area: area.value,
      tipo: tipo.value,
      resumoCurto: resumoCurto.value,
      visivel: visivel.value
    }, token.value)

    if (file.value) {
      await publicationStore.uploadFile(publication.id, file.value, token.value)
    }
    message.value = 'Publicação submetida com sucesso!'
  } catch (error) {
    console.error(error)
    message.value = 'Erro ao submeter publicação.'
  }
}
</script>