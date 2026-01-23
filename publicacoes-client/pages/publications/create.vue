<template>
  <div class="max-w-3xl mx-auto py-10 px-4">

    <div class="flex justify-between items-center mb-8">
      <div>
        <h1 class="text-3xl font-bold text-gray-900">Submeter Nova Publicação</h1>
        <p class="text-gray-500 mt-1">Partilha o teu conhecimento com a comunidade.</p>
      </div>
      <NuxtLink to="/" class="text-gray-500 hover:text-gray-700 flex items-center text-sm font-bold transition">
        <i class="bi bi-arrow-left mr-1"></i> Cancelar
      </NuxtLink>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
      <form @submit.prevent="submitPublication">

        <div class="mb-6">
          <label class="block text-gray-700 text-sm font-bold mb-2">
            Título da Publicação <span class="text-red-500">*</span>
          </label>
          <input
              v-model="form.titulo"
              type="text"
              class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              placeholder="Ex: Análise de Algoritmos em Java"
              required
          >
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Autores <span class="text-red-500">*</span>
            </label>
            <input
                v-model="form.autores"
                type="text"
                class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                placeholder="Ex: Ana Silva, João Santos"
                required
            >
            <p class="text-xs text-gray-400 mt-1">Separados por vírgula</p>
          </div>

          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Área Científica <span class="text-red-500">*</span>
            </label>
            <input
                v-model="form.area"
                type="text"
                class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
                placeholder="Ex: Ciência de Dados"
                required
            >
          </div>
        </div>

        <div class="mb-6 relative">
          <label class="block text-gray-700 text-sm font-bold mb-2">
            Tags <span class="text-red-500">*</span>
          </label>

          <div class="flex flex-wrap gap-2 mb-2" v-if="tags.length > 0">
            <span v-for="(tag, index) in tags" :key="index" class="bg-blue-100 text-blue-700 px-2 py-1 rounded-lg text-sm font-bold flex items-center animate-fadeIn">
              #{{ tag }}
              <button type="button" @click="removeTag(index)" class="ml-2 text-blue-400 hover:text-blue-900"><i class="bi bi-x"></i></button>
            </span>
          </div>

          <input
              v-model="tagInput"
              @keydown.enter.prevent="addTagFromInput"
              type="text"
              class="w-full px-4 py-3 rounded-xl border outline-none transition"
              :class="showTagError ? 'border-red-500 bg-red-50' : 'border-gray-300 focus:ring-2 focus:ring-blue-500'"
              placeholder="Escreve para pesquisar ou criar tag..."
              autocomplete="off"
          >

          <p v-if="showTagError" class="text-xs text-red-500 font-bold mt-1">
            <i class="bi bi-exclamation-circle-fill"></i> Tens de adicionar pelo menos uma tag à lista!
          </p>
          <p v-else class="text-xs text-gray-400 mt-1">Seleciona da lista ou pressiona Enter para criar nova.</p>

          <div v-if="filteredTags.length > 0 && tagInput" class="absolute z-10 w-full bg-white border border-gray-200 rounded-xl shadow-lg mt-1 max-h-48 overflow-y-auto">
            <ul>
              <li
                  v-for="tag in filteredTags"
                  :key="tag.id"
                  @click="selectTag(tag.name)"
                  class="px-4 py-2 hover:bg-blue-50 cursor-pointer text-gray-700 text-sm font-medium flex justify-between items-center transition"
              >
                <span>#{{ tag.name }}</span>
              </li>
            </ul>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-gray-700 text-sm font-bold mb-2">
            Tipo de Publicação <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <select
                v-model="form.tipo"
                class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition appearance-none bg-white"
                required
            >
              <option value="" disabled selected>Seleciona um tipo</option>
              <option value="Artigo">Artigo Científico</option>
              <option value="Tese">Tese / Dissertação</option>
              <option value="Relatório">Relatório Técnico</option>
              <option value="Livro">Livro / Capítulo</option>
              <option value="Outro">Outro</option>
            </select>
            <div class="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-gray-500">
              <i class="bi bi-chevron-down"></i>
            </div>
          </div>
        </div>

        <!-- SECÇÃO DO RESUMO COM IA -->
        <div class="mb-8">
          <div class="flex items-center justify-between mb-2">
            <label class="block text-gray-700 text-sm font-bold">
              Resumo Curto <span class="text-gray-400 text-xs font-normal">(opcional)</span>
            </label>
            
            <button
                v-if="canGenerateAI"
                type="button"
                @click="generateAISummary"
                :disabled="isGenerating"
                class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all"
                :class="isGenerating 
                  ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
                  : 'bg-gradient-to-r from-purple-500 to-blue-500 text-white hover:from-purple-600 hover:to-blue-600 shadow-md hover:shadow-lg transform hover:-translate-y-0.5'"
            >
              <i class="bi" :class="isGenerating ? 'bi-hourglass-split animate-spin' : 'bi-stars'"></i>
              {{ isGenerating ? 'A gerar com IA...' : 'Gerar com IA' }}
            </button>
          </div>

          <!-- Mensagem informativa -->
          <div v-if="!canGenerateAI && !form.resumo" class="mb-3 p-3 bg-blue-50 border border-blue-200 rounded-lg">
            <p class="text-xs text-blue-700 flex items-center gap-2">
              <i class="bi bi-info-circle-fill"></i>
              <span>Preenche o <strong>título</strong>, <strong>autores</strong>, <strong>área</strong> e <strong>tipo</strong> para gerar o resumo automaticamente com IA, ou deixa em branco para gerar ao submeter</span>
            </p>
          </div>

          <!-- Alert de sucesso -->
          <div v-if="aiGeneratedSuccess" class="mb-3 p-3 bg-green-50 border border-green-200 rounded-lg animate-fadeIn">
            <p class="text-xs text-green-700 flex items-center gap-2">
              <i class="bi bi-check-circle-fill"></i>
              <span>Resumo gerado com sucesso usando <strong>llama2</strong>! Podes editá-lo se quiseres.</span>
            </p>
          </div>

          <!-- Alert de erro -->
          <div v-if="aiGeneratedError" class="mb-3 p-3 bg-red-50 border border-red-200 rounded-lg animate-fadeIn">
            <p class="text-xs text-red-700 flex items-center gap-2">
              <i class="bi bi-exclamation-triangle-fill"></i>
              <span>{{ aiGeneratedError }}</span>
            </p>
          </div>

          <div class="relative">
            <textarea
                v-model="form.resumo"
                rows="6"
                class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition resize-none"
                :class="{ 'bg-purple-50 border-purple-300': isGenerating }"
                placeholder="Escreve um breve resumo do trabalho ou deixa em branco para gerar automaticamente com IA..."
                :disabled="isGenerating"
            ></textarea>
            
            <!-- Indicador de loading -->
            <div v-if="isGenerating" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-50 rounded-xl">
              <div class="flex flex-col items-center gap-2">
                <div class="w-8 h-8 border-4 border-purple-200 border-t-purple-600 rounded-full animate-spin"></div>
                <p class="text-xs text-gray-600 font-bold">A criar resumo...</p>
              </div>
            </div>
          </div>

          <p class="text-xs text-gray-400 mt-2">
            <i class="bi bi-lightbulb"></i> Dica: Se não escreveres nada, o resumo será gerado automaticamente ao submeter.
          </p>
        </div>

        <div class="mb-8 p-6 bg-gray-50 border border-dashed border-gray-300 rounded-xl hover:bg-gray-100 transition relative">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="w-12 h-12 bg-white rounded-lg flex items-center justify-center border border-gray-200 mr-4 shadow-sm">
                <i class="bi bi-paperclip text-xl text-gray-400"></i>
              </div>
              <div>
                <h4 class="font-bold text-gray-700 text-sm">Ficheiro (PDF ou ZIP)</h4>
                <p class="text-xs text-gray-500" v-if="!selectedFile">Nenhum ficheiro selecionado</p>
                <p class="text-xs text-blue-600 font-bold" v-else>{{ selectedFile.name }}</p>
              </div>
            </div>

            <div class="flex items-center gap-4">
              <label class="cursor-pointer bg-blue-100 text-blue-700 px-4 py-2 rounded-lg text-sm font-bold hover:bg-blue-200 transition">
                Escolher
                <input type="file" class="hidden" @change="handleFileChange" accept=".pdf,.zip,.doc,.docx">
              </label>

              <div class="flex items-center gap-2">
                <span class="text-xs font-bold text-gray-500">Público?</span>
                <button
                    type="button"
                    @click="form.visivel = !form.visivel"
                    class="w-12 h-6 rounded-full p-1 transition-colors duration-200 ease-in-out focus:outline-none"
                    :class="form.visivel ? 'bg-blue-600' : 'bg-gray-300'"
                >
                  <div
                      class="w-4 h-4 bg-white rounded-full shadow-md transform transition-transform duration-200 ease-in-out"
                      :class="form.visivel ? 'translate-x-6' : 'translate-x-0'"
                  ></div>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-3 pt-4 border-t border-gray-100">
          <NuxtLink to="/" class="px-6 py-3 rounded-xl border border-gray-200 text-gray-600 font-bold hover:bg-gray-50 transition">
            Cancelar
          </NuxtLink>
          <button
              type="submit"
              :disabled="isGenerating"
              class="px-8 py-3 rounded-xl bg-blue-600 text-white font-bold shadow-lg shadow-blue-200 transition"
              :class="isGenerating 
                ? 'opacity-50 cursor-not-allowed' 
                : 'hover:bg-blue-700 transform hover:-translate-y-0.5'"
          >
            {{ isGenerating ? 'Aguarda...' : 'Submeter Publicação' }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from "~/stores/auth-store.js";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";

const router = useRouter();
const authStore = useAuthStore();
const { token } = storeToRefs(authStore);
const config = useRuntimeConfig();
const api = config.public.apiBase;

const form = ref({
  titulo: '',
  autores: '',
  area: '',
  tipo: '',
  resumo: '',
  visivel: true
});

const tags = ref([]);
const tagInput = ref('');
const availableTags = ref([]);
const selectedFile = ref(null);
const showTagError = ref(false);

// Estados para IA
const isGenerating = ref(false);
const aiGeneratedSuccess = ref(false);
const aiGeneratedError = ref(null);

onMounted(async () => {
  if (token.value) {
    try {
      const data = await $fetch(`${api}/tags`, {
        headers: { Authorization: `Bearer ${token.value}` }
      });
      availableTags.value = data;
    } catch (e) { console.error("Erro a carregar tags", e); }
  }
});

// Verifica se pode gerar resumo com IA (todos os campos necessários preenchidos)
const canGenerateAI = computed(() => {
  return form.value.titulo.trim() !== '' &&
         form.value.autores.trim() !== '' &&
         form.value.area.trim() !== '' &&
         form.value.tipo !== '';
});

const filteredTags = computed(() => {
  const search = tagInput.value.toLowerCase().trim();
  if (!search) return [];

  return availableTags.value.filter(t =>
      t.name.toLowerCase().includes(search) &&
      !tags.value.includes(t.name)
  );
});

function selectTag(tagName) {
  if (!tags.value.includes(tagName)) {
    tags.value.push(tagName);
    showTagError.value = false;
  }
  tagInput.value = '';
}

function addTagFromInput() {
  const val = tagInput.value.trim();
  if (val && !tags.value.includes(val)) {
    tags.value.push(val);
    showTagError.value = false;
  }
  tagInput.value = '';
}

function removeTag(index) {
  tags.value.splice(index, 1);
}

function handleFileChange(event) {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
  }
}

// FUNÇÃO PARA GERAR RESUMO COM IA
async function generateAISummary() {
  if (!canGenerateAI.value) return;

  isGenerating.value = true;
  aiGeneratedSuccess.value = false;
  aiGeneratedError.value = null;

  try {
    const autoresList = form.value.autores.split(',').map(a => a.trim()).filter(a => a);

    const payload = {
      titulo: form.value.titulo,
      autores: autoresList,
      area: form.value.area,
      tipo: form.value.tipo
    };

    // Chama o endpoint /gerar-resumo
    const resumo = await $fetch(`${api}/publicacoes/gerar-resumo`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify(payload)
    });

    // O resumo vem como texto puro (String)
    form.value.resumo = resumo;
    aiGeneratedSuccess.value = true;

    // Remove mensagem de sucesso após 5 segundos
    setTimeout(() => {
      aiGeneratedSuccess.value = false;
    }, 5000);

  } catch (e) {
    console.error('Erro ao gerar resumo:', e);
    aiGeneratedError.value = 'Erro ao gerar resumo com IA. Tenta novamente ou escreve manualmente.';
    
    setTimeout(() => {
      aiGeneratedError.value = null;
    }, 5000);
  } finally {
    isGenerating.value = false;
  }
}

async function submitPublication() {
  if (tags.value.length === 0) {
    showTagError.value = true;
    return;
  }

  // Se o resumo está vazio, mostra loading de IA
  const willGenerateAI = !form.value.resumo || form.value.resumo.trim() === '';
  
  if (willGenerateAI) {
    isGenerating.value = true;
    aiGeneratedSuccess.value = false;
    aiGeneratedError.value = null;
  }

  try {
    const autoresList = form.value.autores.split(',').map(a => a.trim()).filter(a => a);

    const payload = {
      titulo: form.value.titulo,
      autores: autoresList,
      area: form.value.area,
      tipo: form.value.tipo,
      resumoCurto: form.value.resumo, // Se vazio, backend gera automaticamente
      visivel: form.value.visivel,
      tags: tags.value
    };

    const createdPub = await $fetch(`${api}/publicacoes`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify(payload)
    });

    if (selectedFile.value && createdPub.id) {
      const formData = new FormData();
      formData.append('file', selectedFile.value);

      await $fetch(`${api}/publicacoes/${createdPub.id}/upload`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token.value}` },
        body: formData
      });
    }

    isGenerating.value = false;
    router.push('/');

  } catch (e) {
    console.error(e);
    isGenerating.value = false;
    alert("Erro ao criar publicação. Verifica os campos.");
  }
}
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}
.animate-fadeIn {
  animation: fadeIn 0.2s ease-out;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
.animate-spin {
  animation: spin 1s linear infinite;
}
</style>