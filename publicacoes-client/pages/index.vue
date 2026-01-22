<template>
  <div class="min-h-screen bg-gray-50">

    <div v-if="!token" class="relative overflow-hidden bg-white">
      <div class="mx-auto max-w-2xl py-24 text-center px-4">
        <div class="inline-block p-4 rounded-full bg-blue-50 text-blue-600 mb-6 text-4xl shadow-sm">
          <i class="bi bi-layers-fill"></i>
        </div>
        <h1 class="text-4xl font-extrabold tracking-tight text-gray-900 sm:text-6xl mb-6">
          Centro de I&D <span class="text-blue-600">XYZ</span>
        </h1>
        <p class="mt-6 text-lg leading-8 text-gray-600 mb-10">
          Plataforma de gestão, arquivo e partilha de conhecimento científico.
          Junta-te à comunidade.
        </p>
        <NuxtLink to="/auth/login" class="rounded-xl bg-blue-600 px-8 py-4 text-sm font-bold text-white shadow-lg shadow-blue-200 hover:bg-blue-700 hover:-translate-y-1 transition transform">
          Entrar na Plataforma <i class="bi bi-arrow-right ml-2"></i>
        </NuxtLink>
      </div>
    </div>

    <div v-else class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

      <div class="flex flex-col md:flex-row justify-between items-end mb-8 gap-4">
        <div>
          <h2 class="text-3xl font-bold text-gray-800">Olá, {{ user?.name }} 👋</h2>
          <p class="text-gray-500 mt-1 text-sm">
            <span v-if="isSearching" class="text-blue-600 font-bold">Resultados da pesquisa</span>
            <span v-else>A mostrar o teu feed personalizado (tags que segues)</span>
          </p>
        </div>
        <NuxtLink to="/publications/create" class="bg-green-600 text-white px-5 py-3 rounded-xl font-bold hover:bg-green-700 transition shadow-lg shadow-green-200 flex items-center">
          <i class="bi bi-plus-lg mr-2"></i> Nova Publicação
        </NuxtLink>
      </div>

      <div class="bg-white p-2 rounded-2xl shadow-sm border border-gray-100 mb-8 flex items-center">
        <div class="pl-4 text-gray-400"><i class="bi bi-search text-lg"></i></div>
        <form @submit.prevent="pesquisar" class="flex-1">
          <input
              v-model="searchQuery"
              type="text"
              placeholder="Pesquisar por título, autor ou conteúdo..."
              class="w-full p-4 outline-none text-gray-700 bg-transparent placeholder-gray-400"
          >
        </form>
        <button v-if="isSearching" @click="limparFiltros" class="px-4 py-2 text-gray-400 hover:text-red-500 text-sm font-bold transition">
          <i class="bi bi-x-circle mr-1"></i> Limpar
        </button>
        <button @click="pesquisar" class="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-blue-700 transition m-1">
          Pesquisar
        </button>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">

        <div class="lg:col-span-2 space-y-8">
          <div v-if="loading" class="text-center py-12">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mx-auto"></div>
          </div>

          <div v-else-if="publications.length > 0" class="space-y-6">
            <div v-for="p in publications" :key="p.id" class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition group">
              <div class="p-6 pb-2 flex justify-between items-start">
                <div class="flex flex-wrap gap-2">
                  <span class="px-2 py-1 rounded-md text-[10px] font-bold uppercase bg-blue-50 text-blue-700 border border-blue-100">{{ p.area }}</span>
                  <span v-for="tag in (p.tags || []).slice(0, 3)" :key="tag" class="px-2 py-1 rounded-md text-[10px] font-bold uppercase bg-purple-50 text-purple-700 border border-purple-100">#{{ tag }}</span>
                </div>
                <span class="text-xs text-gray-400 font-medium flex items-center">
                    <i class="bi bi-calendar-event mr-1"></i>
                    {{ p.publicationDate ? new Date(p.publicationDate).toLocaleDateString() : 'Recente' }}
                 </span>
              </div>

              <div class="p-6 pt-2">
                <NuxtLink :to="`/publications/${p.id}`" class="block">
                  <h3 class="text-xl font-bold text-gray-800 mb-2 group-hover:text-blue-600 transition">{{ p.titulo }}</h3>
                </NuxtLink>
                <div class="flex items-center mb-4">
                  <div class="flex text-yellow-400 text-xs mr-2">
                    <i v-for="star in 5" :key="star" class="bi" :class="star <= Math.round(p.ratingAverage || 0) ? 'bi-star-fill' : 'bi-star text-gray-200'"></i>
                  </div>
                  <span class="text-xs text-gray-400">({{ p.ratingCount || 0 }})</span>
                </div>
                <p class="text-gray-500 text-sm line-clamp-2 leading-relaxed">{{ p.resumoCurto }}</p>
              </div>

              <div class="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-between items-center">
                <div class="flex items-center gap-2 text-xs text-gray-500 font-medium">
                  <span class="font-bold text-gray-700">{{ p.username }}</span>
                  <span>•</span>
                  <span>{{ p.comments ? p.comments.length : 0 }} comentários</span>
                </div>
                <NuxtLink :to="`/publications/${p.id}`" class="text-blue-600 text-sm font-bold flex items-center hover:underline">
                  Ler publicação <i class="bi bi-arrow-right ml-1"></i>
                </NuxtLink>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-16 bg-white rounded-3xl border border-dashed border-gray-300">
            <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-blue-50 text-blue-400 mb-6 text-2xl">
              <i class="bi bi-search" v-if="isSearching"></i>
              <i class="bi bi-rss" v-else></i>
            </div>
            <h3 class="text-lg font-bold text-gray-800 mb-2">
              {{ isSearching ? 'Sem resultados.' : 'O teu feed está silencioso.' }}
            </h3>
            <p class="text-gray-500 mb-8 max-w-sm mx-auto">
              {{ isSearching ? 'Tenta outros termos ou limpa os filtros.' : 'Ainda não segues tópicos com publicações recentes.' }}
            </p>
            <button v-if="isSearching" @click="limparFiltros" class="px-6 py-2 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition">
              Limpar Filtros
            </button>
            <div v-else class="flex justify-center gap-4">
              <NuxtLink to="/tags" class="px-6 py-2 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition">Gerir Tags</NuxtLink>
              <NuxtLink to="/explore" class="px-6 py-2 bg-white text-gray-600 border border-gray-200 rounded-xl font-bold hover:bg-gray-50 transition">Explorar Tudo</NuxtLink>
            </div>
          </div>
        </div>

        <div class="lg:col-span-1 space-y-6">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h4 class="font-bold text-gray-800 mb-4 flex items-center">
              <i class="bi bi-funnel-fill text-blue-500 mr-2"></i> Filtros & Ordenação
            </h4>
            <div class="space-y-4">
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase mb-1 block">Ordenar por</label>
                <select v-model="filters.sortBy" @change="pesquisar" class="w-full p-2 rounded-lg border border-gray-200 text-sm outline-none bg-white cursor-pointer">
                  <option value="date">📅 Mais Recentes</option>
                  <option value="popular">🔥 Mais Populares (Comentários)</option>
                  <option value="rating">⭐ Melhor Classificados</option>
                </select>
              </div>
              <hr class="border-gray-100">
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase mb-1 block">Área Científica</label>
                <input v-model="filters.area" class="w-full p-2 rounded-lg border border-gray-200 text-sm focus:ring-2 focus:ring-blue-100 outline-none" placeholder="Ex: Informática">
              </div>
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase mb-1 block">Tipo</label>
                <select v-model="filters.tipo" class="w-full p-2 rounded-lg border border-gray-200 text-sm outline-none bg-white">
                  <option value="">Todos</option>
                  <option value="Artigo">Artigo</option>
                  <option value="Tese">Tese</option>
                  <option value="Relatório">Relatório</option>
                </select>
              </div>
              <button @click="pesquisar" class="w-full py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 font-bold rounded-lg text-sm transition">
                Aplicar Filtros
              </button>
            </div>
          </div>

          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-2">
            <NuxtLink to="/publications/mine" class="flex items-center p-3 hover:bg-gray-50 rounded-xl transition text-gray-700 font-medium group">
              <span class="w-8 h-8 rounded-lg bg-blue-100 text-blue-600 flex items-center justify-center mr-3 group-hover:bg-blue-600 group-hover:text-white transition">
                <i class="bi bi-folder-fill"></i>
              </span>
              Meus Uploads
            </NuxtLink>
            <NuxtLink to="/tags" class="flex items-center p-3 hover:bg-gray-50 rounded-xl transition text-gray-700 font-medium group">
              <span class="w-8 h-8 rounded-lg bg-purple-100 text-purple-600 flex items-center justify-center mr-3 group-hover:bg-purple-600 group-hover:text-white transition">
                <i class="bi bi-hash"></i>
              </span>
              Gerir Subscrições
            </NuxtLink>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useAuthStore } from "~/stores/auth-store.js";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";
import { useRouter, useRoute } from "vue-router";

const config = useRuntimeConfig();
const api = config.public.apiBase;
const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);
const publicationStore = usePublicationStore();
const router = useRouter();
const route = useRoute();

const loading = ref(true);
const publications = ref([]);
const searchQuery = ref("");
const isSearching = ref(false);
const myTagNames = ref([]);

const filters = ref({
  area: "",
  tipo: "",
  sortBy: "date"
});

onMounted(async () => {
  if (token.value) {
    if(route.query.q) {
      searchQuery.value = route.query.q;
      await pesquisar();
    } else {
      await loadInitialData();
    }
  }
});

// 1. CARREGA O FEED INICIAL (Só tags subscritas)
async function loadInitialData() {
  loading.value = true;
  isSearching.value = false;

  try {
    // Vamos buscar tudo primeiro
    const allPubs = await $fetch(`${api}/publicacoes`, {
      headers: { Authorization: `Bearer ${token.value}` }
    });

    const allTags = await $fetch(`${api}/tags`, {
      headers: { Authorization: `Bearer ${token.value}` }
    });

    // Descobrimos quais as tags que tu segues
    myTagNames.value = allTags
        .filter(t => t.subscriberUsernames && t.subscriberUsernames.includes(user.value.username))
        .map(t => t.name);

    // Filtramos o feed inicial pelas tuas tags
    if (myTagNames.value.length > 0) {
      publications.value = allPubs.filter(p =>
          p.tags && p.tags.some(tag => myTagNames.value.includes(tag))
      );
    } else {
      publications.value = [];
    }

    // Ordenamos por data para o feed ficar fresquinho
    publications.value.sort((a, b) => new Date(b.publicationDate || 0) - new Date(a.publicationDate || 0));

  } catch (e) {
    console.error("Erro no feed inicial", e);
  } finally {
    loading.value = false;
  }
}

// 2. PESQUISA GLOBAL COM FILTROS E ORDENAÇÃO
async function pesquisar() {
  if(!token.value) return;

  loading.value = true;
  // Se houver texto ou se mudámos o sortBy/filtros, estamos em modo "pesquisa"
  isSearching.value = true;

  if (searchQuery.value) {
    router.push({ query: { ...route.query, q: searchQuery.value } });
  }

  try {
    const data = await $fetch(`${api}/publicacoes/pesquisa`, {
      headers: { Authorization: `Bearer ${token.value}` },
      params: {
        q: searchQuery.value || "",
        area: filters.value.area || "",
        tipo: filters.value.tipo || "",
        sortBy: filters.value.sortBy || "date"
      }
    });

    // Se a pesquisa estiver vazia mas mudámos a ordem,
    // filtramos os resultados pelas tuas tags para manter o Feed personalizado
    if (!searchQuery.value) {
      publications.value = data.filter(p =>
          p.tags && p.tags.some(tag => myTagNames.value.includes(tag))
      );
    } else {
      // Se escreveste algo, mostramos a pesquisa global
      publications.value = [...data];
    }

  } catch (e) {
    console.error("Erro na pesquisa", e);
  } finally {
    loading.value = false;
  }
}

// Watchers para reagir a mudanças nos filtros
watch(() => filters.value.sortBy, () => pesquisar());
watch(() => filters.value.tipo, () => pesquisar());

async function limparFiltros() {
  searchQuery.value = "";
  filters.value.area = "";
  filters.value.tipo = "";
  filters.value.sortBy = "date";
  router.push({ path: '/' });
  await loadInitialData();
}

watch(() => route.query.q, (newQ) => {
  if(newQ !== undefined && newQ !== searchQuery.value) {
    searchQuery.value = newQ;
    pesquisar();
  }
});
</script>