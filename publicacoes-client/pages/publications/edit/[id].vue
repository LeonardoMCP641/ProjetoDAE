<template>
  <div class="max-w-3xl mx-auto">
    <div class="mb-6 flex justify-between items-center">
      <h2 class="text-2xl font-bold text-gray-800">Editar Publicação</h2>
      <button @click="$router.back()" class="text-sm text-gray-500 hover:text-gray-800">
        <i class="bi bi-arrow-left"></i> Voltar
      </button>
    </div>

    <div v-if="!isOwner" class="mb-6 p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800 rounded-r-lg shadow-sm">
      <div class="flex">
        <i class="bi bi-exclamation-triangle-fill mr-3 text-xl"></i>
        <div>
          <p class="font-bold">Modo de Moderação</p>
          <p class="text-sm">Como não és o autor, apenas podes alterar a visibilidade desta publicação.</p>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-8">
      <form @submit.prevent="submit" class="space-y-6">

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Título</label>
          <input
              v-model="titulo"
              :disabled="!isOwner"
              :class="!isOwner ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white focus:ring-2 focus:ring-blue-500'"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 outline-none transition"
          />
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Área Científica</label>
          <input
              v-model="area"
              :disabled="!isOwner"
              :class="!isOwner ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white focus:ring-2 focus:ring-blue-500'"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 outline-none transition"
          />
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Tipo de Publicação</label>
          <input
              v-model="tipo"
              :disabled="!isOwner"
              :class="!isOwner ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white focus:ring-2 focus:ring-blue-500'"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 outline-none transition"
          />
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Autores</label>
          <input
              v-model="autores"
              :disabled="!isOwner"
              :class="!isOwner ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white focus:ring-2 focus:ring-blue-500'"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 outline-none transition"
              placeholder="Separados por vírgula"
          />
        </div>

        <div>
          <label class="block text-sm font-bold text-gray-700 mb-2">Resumo</label>
          <textarea
              v-model="resumoCurto"
              :disabled="!isOwner"
              :class="!isOwner ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white focus:ring-2 focus:ring-blue-500'"
              rows="4"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 outline-none transition"
          ></textarea>
        </div>

        <div class="p-4 bg-blue-50 rounded-lg border border-blue-100 flex items-center justify-between">
          <div>
            <span class="font-bold text-gray-800">Visibilidade</span>
            <p class="text-xs text-gray-500">Define se a publicação aparece nas pesquisas globais.</p>
          </div>
          <label class="relative inline-flex items-center cursor-pointer">
            <input type="checkbox" v-model="visivel" class="sr-only peer">
            <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
          </label>
        </div>

        <div v-if="isOwner" class="mt-4">
          <label class="block text-sm font-bold text-gray-700 mb-2">Atualizar Ficheiro (Opcional)</label>
          <input type="file" @change="onFileChange" class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 transition"/>
        </div>

        <div class="flex gap-4 pt-4">
          <button class="flex-1 bg-blue-600 text-white font-bold py-3 px-4 rounded-lg hover:bg-blue-700 shadow-md transition">
            Guardar Alterações
          </button>

          <button v-if="isOwner" @click.prevent="deletePublication" class="px-6 py-3 border border-red-200 text-red-600 font-bold rounded-lg hover:bg-red-50 transition">
            <i class="bi bi-trash"></i>
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "#imports";
import { useAuthStore } from "~/stores/auth-store";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);
const publicationStore = usePublicationStore();

const titulo = ref("");
const autores = ref("");
const area = ref("");
const tipo = ref("");
const resumoCurto = ref("");
const visivel = ref(true);
const file = ref(null);
const message = ref("");
const isOwner = ref(false);

onMounted(async () => {
  if (!token.value) {
    router.push("/auth/login");
    return;
  }

  try {
    if (!user.value) await authStore.fetchUser();

    const id = route.params.id;
    const res = await publicationStore.fetchById(id, token.value);

    titulo.value = res.titulo;
    autores.value = Array.isArray(res.autores) ? res.autores.join(", ") : res.autores;
    area.value = res.area;
    tipo.value = res.tipo;
    resumoCurto.value = res.resumoCurto;
    visivel.value = res.visivel ?? true;

    isOwner.value = res.username === user.value.username;

  } catch (err) {
    console.error(err);
    router.push("/");
  }
});

async function deletePublication() {
  if (!confirm("Tens a certeza que queres apagar permanentemente esta publicação?")) return;
  try {
    await publicationStore.delete(route.params.id, token.value);
    router.push("/");
  } catch (e) {
    alert("Erro ao apagar publicação.");
  }
}

function onFileChange(e) {
  file.value = e.target.files[0];
}

async function submit() {
  try {
    const id = route.params.id;

    const payload = isOwner.value
        ? {
          titulo: titulo.value,
          autores: autores.value.split(",").map(a => a.trim()),
          area: area.value,
          tipo: tipo.value,
          resumoCurto: resumoCurto.value,
          visivel: visivel.value
        }
        : { visivel: visivel.value };

    await publicationStore.update(id, payload, token.value);

    if (file.value && isOwner.value) {
      await publicationStore.uploadFile(id, file.value, token.value);
    }

    router.push("/");
  } catch (e) {
    console.error(e);
    alert("Erro ao atualizar publicação. Verifica os dados.");
  }
}
</script>