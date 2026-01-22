<template>
  <div class="max-w-3xl mx-auto py-10">

    <div class="mb-8 flex justify-between items-end">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">Submeter Nova Publicação</h2>
        <p class="text-gray-500 text-sm">Partilha o teu conhecimento com a comunidade.</p>
      </div>
      <button @click="$router.back()" class="text-gray-500 hover:text-blue-600 transition flex items-center text-sm font-medium">
        <i class="bi bi-arrow-left mr-2"></i> Cancelar
      </button>
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

        <div class="p-5 bg-gray-50 rounded-xl border border-gray-200">
          <div class="flex items-center justify-between mb-4">
            <label class="text-sm font-bold text-gray-700 flex items-center">
              <i class="bi bi-paperclip mr-2"></i> Ficheiro (PDF ou ZIP)
            </label>

            <div class="flex items-center">
              <span class="mr-3 text-sm font-medium text-gray-700">Tornar Público?</span>
              <label class="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" v-model="visivel" class="sr-only peer">
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
              </label>
            </div>
          </div>

          <input
              type="file"
              @change="onFileChange"
              class="block w-full text-sm text-gray-500
              file:mr-4 file:py-2.5 file:px-4
              file:rounded-lg file:border-0
              file:text-sm file:font-semibold
              file:bg-blue-100 file:text-blue-700
              hover:file:bg-blue-200 transition cursor-pointer"
          />
        </div>

        <button class="w-full bg-blue-600 text-white font-bold py-3.5 px-4 rounded-xl hover:bg-blue-700 shadow-md hover:shadow-lg transition transform hover:-translate-y-0.5 flex justify-center items-center">
          <i class="bi bi-send-check-fill mr-2"></i> Submeter Publicação
        </button>

      </form>

      <div v-if="message" class="mt-6 p-4 rounded-lg bg-green-50 text-green-700 border border-green-200 flex items-center justify-center font-medium animate-pulse">
        <i class="bi bi-check-circle-fill mr-2"></i> {{ message }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '~/stores/auth-store'
import { usePublicationStore } from '~/stores/publication-store'

const router = useRouter()
const authStore = useAuthStore()
const { token } = storeToRefs(authStore)
const publicationStore = usePublicationStore()

// Campos do formulário
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
  if (!token.value) {
    router.push('/auth/login')
    return
  }

  try {
    // 1. Criar a publicação
    const publication = await publicationStore.create({
      titulo: titulo.value,
      autores: autores.value.split(',').map(a => a.trim()),
      area: area.value,
      tipo: tipo.value,
      resumoCurto: resumoCurto.value,
      visivel: visivel.value
    }, token.value)

    // 2. Upload do ficheiro (se existir)
    if (file.value) {
      await publicationStore.uploadFile(publication.id, file.value, token.value)
    }

    // 3. Sucesso e Redirecionamento
    message.value = 'Publicação criada com sucesso! A redirecionar...'

    // Espera 1.5 segundos para o utilizador ler a mensagem e vai para a home
    setTimeout(() => {
      router.push('/')
    }, 1500)

  } catch (error) {
    console.error(error)
    message.value = 'Erro ao submeter publicação. Verifica os dados.'
  }
}
</script>