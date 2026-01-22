<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

    <div class="mb-8">
      <h2 class="text-3xl font-bold text-gray-800 flex items-center">
        <i class="bi bi-clock-history mr-3 text-blue-600"></i> Histórico de Atividade
      </h2>
      <p class="text-gray-500 mt-2">Registo global de todas as alterações nas publicações.</p>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">

      <div v-if="loading" class="p-12 text-center">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mx-auto"></div>
        <p class="text-gray-400 mt-4 text-xs font-bold uppercase tracking-widest">A carregar logs...</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm text-left text-gray-500">
          <thead class="text-xs text-gray-700 uppercase bg-gray-50 border-b border-gray-100">
          <tr>
            <th class="px-6 py-4">Data</th>
            <th class="px-6 py-4">Utilizador</th>
            <th class="px-6 py-4">Publicação (ID)</th>
            <th class="px-6 py-4">Campo Alterado</th>
            <th class="px-6 py-4">Antes</th>
            <th class="px-6 py-4">Depois</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(log, index) in logs" :key="index" class="bg-white border-b hover:bg-blue-50/30 transition">

            <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">
              {{ new Date(log.editedAt).toLocaleString() }}
            </td>

            <td class="px-6 py-4">
              <div class="flex items-center">
                <div class="w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-xs font-bold mr-2">
                  {{ log.editor.charAt(0).toUpperCase() }}
                </div>
                {{ log.editor }}
              </div>
            </td>

            <td class="px-6 py-4 font-mono text-blue-600">
              <NuxtLink :to="`/publications/${log.publicationId}`" class="hover:underline">
                #{{ log.publicationId }}
              </NuxtLink>
            </td>

            <td class="px-6 py-4">
                <span class="px-2 py-1 rounded bg-gray-100 text-gray-600 text-xs font-bold border border-gray-200">
                  {{ log.fieldName }}
                </span>
            </td>

            <td class="px-6 py-4 text-red-500 truncate max-w-xs" :title="log.oldValue">
              {{ log.oldValue || '(vazio)' }}
            </td>

            <td class="px-6 py-4 text-green-600 font-medium truncate max-w-xs" :title="log.newValue">
              {{ log.newValue }}
            </td>

          </tr>

          <tr v-if="logs.length === 0">
            <td colspan="6" class="px-6 py-10 text-center text-gray-400">
              Ainda não há registo de atividades.
            </td>
          </tr>

          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from "~/stores/auth-store.js";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";

const config = useRuntimeConfig();
const api = config.public.apiBase;
const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);
const router = useRouter();

const logs = ref([]);
const loading = ref(true);

onMounted(async () => {
  // Segurança Frontend: Só Admin entra
  if (!token.value || user.value?.role !== 'Administrador') {
    router.push('/');
    return;
  }

  try {
    const data = await $fetch(`${api}/publicacoes/history`, {
      headers: { Authorization: `Bearer ${token.value}` }
    });

    // Ordenar por data (mais recente primeiro)
    logs.value = data.sort((a, b) => new Date(b.editedAt) - new Date(a.editedAt));
  } catch (e) {
    console.error("Erro ao carregar histórico", e);
    alert("Erro: Não tens permissão ou o serviço falhou.");
  } finally {
    loading.value = false;
  }
});
</script>