<template>
  <div class="min-h-screen bg-gray-50">

    <div v-if="!token" class="relative overflow-hidden bg-white">
      <div class="mx-auto max-w-2xl py-24 text-center px-4">
        <div class="inline-block p-4 rounded-full bg-blue-50 text-blue-600 mb-6 text-4xl shadow-sm">
          <i class="bi bi-layers-fill"></i>
        </div>
        <h1 class="text-4xl font-extrabold tracking-tight text-gray-900 sm:text-6xl mb-6">
          O Centro de Conhecimento <span class="text-blue-600">XYZ</span>
        </h1>
        <p class="mt-6 text-lg leading-8 text-gray-600 mb-10">
          A plataforma central para partilha de artigos, teses e recursos científicos.
          Junta-te à comunidade e começa a explorar.
        </p>
        <NuxtLink to="/auth/login" class="rounded-xl bg-blue-600 px-8 py-4 text-sm font-bold text-white shadow-lg shadow-blue-200 hover:bg-blue-700 hover:-translate-y-1 transition transform">
          Entrar na Plataforma <i class="bi bi-arrow-right ml-2"></i>
        </NuxtLink>
      </div>
    </div>

    <div v-else class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

      <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
        <div>
          <h2 class="text-3xl font-bold text-gray-800">O Meu Feed 📰</h2>
          <p class="text-gray-500 mt-2 text-sm flex items-center">
            <i class="bi bi-funnel-fill mr-2 text-blue-500"></i>
            A mostrar conteúdos das tags:
            <span v-if="myTagNames.length" class="ml-2 font-bold text-gray-700 bg-white px-2 py-1 rounded border border-gray-200 shadow-sm">{{ myTagNames.join(', ') }}</span>
            <span v-else class="ml-2 text-orange-500 font-medium bg-orange-50 px-2 py-1 rounded">Nenhuma tag seguida</span>
          </p>
        </div>
        <NuxtLink to="/publications/create" class="bg-green-600 text-white px-5 py-3 rounded-xl font-bold hover:bg-green-700 transition shadow-lg shadow-green-200 flex items-center">
          <i class="bi bi-plus-lg mr-2"></i> Nova Publicação
        </NuxtLink>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">

        <div class="lg:col-span-2 space-y-8">

          <div v-if="loading" class="text-center py-12">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mx-auto"></div>
          </div>

          <div v-else-if="myFeed.length > 0" class="space-y-6">
            <div v-for="p in myFeed" :key="p.id" class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition group">

              <div class="p-6 pb-2 flex justify-between items-start">
                <div class="flex flex-wrap gap-2">
                   <span v-for="tag in getMatchingTags(p)" :key="tag" class="px-3 py-1 rounded-lg text-xs font-bold uppercase tracking-wider bg-purple-50 text-purple-700 border border-purple-100 flex items-center">
                     <i class="bi bi-hash mr-1 text-purple-400"></i> {{ tag }}
                   </span>
                </div>
                <span class="text-xs text-gray-400 font-medium">{{ p.tipo }}</span>
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
              <i class="bi bi-rss"></i>
            </div>
            <h3 class="text-lg font-bold text-gray-800 mb-2">O teu feed está silencioso.</h3>
            <p class="text-gray-500 mb-8 max-w-sm mx-auto">
              Ainda não segues tópicos com publicações recentes. Subscreve mais tags para veres conteúdo aqui.
            </p>
            <div class="flex justify-center gap-4">
              <NuxtLink to="/tags" class="px-6 py-2 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition">Gerir Tags</NuxtLink>
              <NuxtLink to="/explore" class="px-6 py-2 bg-white text-gray-600 border border-gray-200 rounded-xl font-bold hover:bg-gray-50 transition">Explorar Tudo</NuxtLink>
            </div>
          </div>

        </div>

        <div class="lg:col-span-1 space-y-6">
          <div class="bg-gradient-to-br from-indigo-600 to-blue-700 rounded-2xl p-6 text-white shadow-xl relative overflow-hidden">
            <div class="relative z-10">
              <h3 class="font-bold text-lg mb-2">Queres ver mais?</h3>
              <p class="text-indigo-100 text-sm mb-4">O teu feed é personalizado. Para veres todo o arquivo, visita o Explorar.</p>
              <NuxtLink to="/explore" class="block w-full text-center bg-white text-blue-600 hover:bg-blue-50 py-3 rounded-xl font-bold text-sm transition shadow-sm">
                Ir para Explorar 🌍
              </NuxtLink>
            </div>
            <div class="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-white opacity-10 rounded-full blur-xl"></div>
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
import { ref, onMounted, computed } from "vue";
import { useAuthStore } from "~/stores/auth-store.js";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";

const config = useRuntimeConfig();
const api = config.public.apiBase;
const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);
const publicationStore = usePublicationStore();
const loading = ref(true);
const myTagNames = ref([]);

onMounted(async () => {
  if (token.value) {
    await publicationStore.fetchAll(token.value);

    try {
      const allTags = await $fetch(`${api}/tags`, {
        headers: { Authorization: `Bearer ${token.value}` }
      });
      myTagNames.value = allTags
          .filter(t => t.subscriberUsernames && t.subscriberUsernames.includes(user.value.username))
          .map(t => t.name);
    } catch (e) { console.error("Erro tags", e); }

    loading.value = false;
  }
});

const myFeed = computed(() => {
  if (myTagNames.value.length === 0) return [];
  return publicationStore.publications.filter(p => {
    if (!p.tags || p.tags.length === 0) return false;
    return p.tags.some(tag => myTagNames.value.includes(tag));
  });
});

function getMatchingTags(publication) {
  if (!publication.tags) return [];
  return publication.tags.filter(t => myTagNames.value.includes(t));
}
</script>