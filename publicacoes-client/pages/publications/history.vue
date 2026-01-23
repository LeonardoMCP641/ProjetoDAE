<template>
  <div class="max-w-4xl mx-auto px-4 py-10">

    <!-- Cabeçalho -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 flex items-center gap-2">
        <i class="bi bi-clock-history text-blue-600"></i>
        Histórico de Edições
      </h1>
      <p class="text-gray-500 mt-1">
        Registo completo das alterações feitas nesta publicação.
      </p>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-12 text-gray-400">
      <i class="bi bi-arrow-repeat animate-spin text-3xl mb-3 block"></i>
      A carregar histórico...
    </div>

    <!-- Timeline -->
    <div
      v-else-if="history.length"
      class="relative border-l-2 border-blue-100 pl-6 space-y-8"
    >
      <div v-for="(h, index) in history" :key="index" class="relative">

        <!-- Ponto timeline -->
        <div
          class="absolute -left-[11px] top-4 w-5 h-5 bg-blue-600 rounded-full border-4 border-white shadow"
        ></div>

        <!-- Card -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">

          <!-- Publicação + Editor -->
          <div class="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-3 mb-4">
            <div>
              <p class="text-xs text-gray-400 uppercase font-bold tracking-wide">
                Publicação
              </p>
              <p class="text-sm font-semibold text-gray-800">
                Publicação #{{ h.publicationId }}
              </p>
            </div>

            <div class="flex items-center gap-2 text-sm text-gray-500">
              <i class="bi bi-person-circle text-blue-600"></i>
              <span>
                Editado por
                <strong class="text-gray-700">
                  {{ h.editor || "Desconhecido" }}
                </strong>
              </span>
            </div>
          </div>

          <!-- Campo + Data -->
          <div class="flex justify-between items-start mb-4">
            <span class="text-xs font-bold uppercase tracking-wide text-blue-600">
              {{ h.fieldName }}
            </span>
            <span class="text-xs text-gray-400">
              {{ formatDate(h.editedAt) }}
            </span>
          </div>

          <!-- Valores -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="bg-red-50 border border-red-100 rounded-xl p-3">
              <p class="text-xs font-bold text-red-500 uppercase mb-1">
                Valor Antigo
              </p>
              <p class="text-sm text-gray-700 break-words">
                {{ h.oldValue || "—" }}
              </p>
            </div>

            <div class="bg-emerald-50 border border-emerald-100 rounded-xl p-3">
              <p class="text-xs font-bold text-emerald-600 uppercase mb-1">
                Valor Novo
              </p>
              <p class="text-sm text-gray-700 break-words">
                {{ h.newValue || "—" }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sem histórico -->
    <div
      v-else
      class="bg-blue-50 border border-blue-100 text-blue-600 rounded-2xl p-6 text-center"
    >
      <i class="bi bi-info-circle text-xl mb-2"></i>
      <p class="font-medium">
        Esta publicação ainda não tem histórico de edições.
      </p>
    </div>

    <!-- Voltar -->
    <div class="mt-10">
      <button
        @click="goBack"
        class="inline-flex items-center gap-2 px-6 py-3 bg-gray-900 text-white rounded-xl font-bold hover:bg-blue-700 transition shadow"
      >
        <i class="bi bi-arrow-left"></i>
        Voltar
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const route = useRoute();
const router = useRouter();

const authStore = useAuthStore();
const { token } = storeToRefs(authStore);

const history = ref([]);
const loading = ref(true);

onMounted(async () => {
  if (!token.value) {
    router.push("/auth/login");
    return;
  }

  try {
    const publicationId = route.query.id;
    if (!publicationId) {
      router.back();
      return;
    }

    const res = await $fetch(
      `http://localhost:8080/publicacoescientificas/api/publicacoes/history/${publicationId}`,
      {
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      }
    );

    history.value = res;
  } catch (err) {
    console.error("Erro ao carregar histórico", err);
    router.back();
  } finally {
    loading.value = false;
  }
});

function formatDate(date) {
  return new Date(date).toLocaleString("pt-PT");
}

function goBack() {
  router.back();
}
</script>
