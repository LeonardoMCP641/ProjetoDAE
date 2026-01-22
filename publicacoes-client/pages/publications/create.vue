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
              class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition"
              placeholder="Escreve para pesquisar ou criar tag..."
              autocomplete="off"
          >
          <p class="text-xs text-gray-400 mt-1">Seleciona da lista ou pressiona Enter para criar nova.</p>

          <div v-if="filteredTags.length > 0 && tagInput" class="absolute z-10 w-full bg-white border border-gray-200 rounded-xl shadow-lg mt-1 max-h-48 overflow-y-auto">
            <ul>
              <li
                  v-for="tag in filteredTags"
                  :key="tag.id"
                  @click="selectTag(tag.name)"
                  class="px-4 py-2 hover:bg-blue-50 cursor-pointer text-gray-700 text-sm font-medium flex justify-between items-center"
              >
                <span>#{{ tag.name }}</span>
                <span class="text-xs text-gray-400">{{ tag.usageCount || 0 }} seguidores</span>
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

        <div class="mb-8">
          <label class="block text-gray-700 text-sm font-bold mb-2">
            Resumo Curto
          </label>
          <textarea
              v-model="form.resumo"
              rows="4"
              class="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 outline-none transition resize-none"
              placeholder="Escreve um breve resumo do trabalho..."
          ></textarea>
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
              class="px-8 py-3 rounded-xl bg-blue-600 text-white font-bold hover:bg-blue-700 shadow-lg shadow-blue-200 transform hover:-translate-y-0.5 transition"
          >
            Submeter Publicação
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
  }
  tagInput.value = '';
}

function addTagFromInput() {
  const val = tagInput.value.trim();
  if (val && !tags.value.includes(val)) {
    tags.value.push(val);
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

async function submitPublication() {
  try {
    const autoresList = form.value.autores.split(',').map(a => a.trim()).filter(a => a);

    const payload = {
      titulo: form.value.titulo,
      autores: autoresList,
      area: form.value.area,
      tipo: form.value.tipo,
      resumoCurto: form.value.resumo,
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

    router.push('/');

  } catch (e) {
    console.error(e);
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
</style>