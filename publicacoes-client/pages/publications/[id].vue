<template>
  <div v-if="publication" class="max-w-4xl mx-auto mb-16 px-4 sm:px-0">

    <div class="relative bg-gradient-to-br from-blue-700 to-blue-900 rounded-t-3xl p-10 text-white shadow-xl overflow-hidden">

      <div class="absolute top-0 right-0 -mt-10 -mr-10 w-64 h-64 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
      <div class="absolute bottom-0 left-0 -mb-10 -ml-10 w-64 h-64 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20"></div>

      <div class="relative z-10">
        <div class="flex justify-between items-start mb-6">
          <div class="flex gap-2">
            <span class="px-3 py-1 rounded-full text-xs font-bold tracking-wider uppercase bg-white/20 backdrop-blur-md border border-white/10 shadow-sm">
              {{ publication.area }}
            </span>
            <span class="px-3 py-1 rounded-full text-xs font-bold tracking-wider uppercase bg-blue-500/30 backdrop-blur-md border border-white/10 shadow-sm">
              {{ publication.tipo }}
            </span>
            <span v-if="isOwner" class="px-3 py-1 rounded-full text-xs font-bold uppercase border border-white/10"
                  :class="publication.visivel ? 'bg-emerald-500/20 text-emerald-100' : 'bg-rose-500/20 text-rose-100'">
              <i class="bi" :class="publication.visivel ? 'bi-globe' : 'bi-lock-fill'"></i>
              {{ publication.visivel ? 'Público' : 'Privado' }}
            </span>
          </div>

          <div v-if="isOwner" class="flex gap-2">
            <button @click="goToEdit" class="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition backdrop-blur-md">
              <i class="bi bi-pencil-fill text-sm"></i>
            </button>
            <button @click="deletePublication" class="w-8 h-8 rounded-full bg-red-500/20 hover:bg-red-500/40 text-red-200 flex items-center justify-center transition backdrop-blur-md">
              <i class="bi bi-trash-fill text-sm"></i>
            </button>
          </div>
        </div>

        <h1 class="text-3xl md:text-5xl font-bold leading-tight mb-2 tracking-tight">
          {{ publication.titulo }}
        </h1>
      </div>
    </div>

    <div class="bg-white rounded-b-3xl shadow-xl border-x border-b border-gray-100 p-8 md:p-10 -mt-4 relative z-20">

      <div class="flex flex-col md:flex-row justify-between items-center border-b border-gray-100 pb-6 mb-8 gap-4">

        <div class="flex items-center gap-4">
          <div class="w-14 h-14 rounded-full bg-gradient-to-r from-blue-100 to-indigo-100 flex items-center justify-center text-blue-600 font-bold text-xl shadow-inner border-2 border-white ring-2 ring-gray-50">
            {{ publication.username.charAt(0).toUpperCase() }}
          </div>
          <div>
            <p class="text-sm text-gray-400 font-bold uppercase tracking-wide">Publicado por</p>
            <p class="text-lg font-bold text-gray-800">{{ publication.username }}</p>
          </div>
        </div>

        <div class="flex flex-col items-end">
          <div class="flex text-yellow-400 text-lg mb-1">
            <i v-for="star in 5" :key="star" class="bi"
               :class="star <= Math.round(publication.ratingAverage) ? 'bi-star-fill' : 'bi-star text-gray-200'"></i>
          </div>
          <p class="text-xs font-medium text-gray-400">
            Média: <span class="text-gray-700 font-bold">{{ publication.ratingAverage.toFixed(1) }}</span> ({{ publication.ratingCount }} votos)
          </p>
        </div>
      </div>

      <div v-if="publication.autores && publication.autores.length" class="mb-6">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3 flex items-center">
          <i class="bi bi-people mr-2"></i> Autores
        </h3>
        <div class="flex flex-wrap gap-2">
          <span v-for="(autor, i) in publication.autores" :key="i" class="px-3 py-1 bg-gray-50 text-gray-600 rounded-lg text-sm font-medium border border-gray-200">
            {{ autor }}
          </span>
        </div>
      </div>

      <div class="mb-8">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3 flex items-center">
          <i class="bi bi-file-text mr-2"></i> Resumo
        </h3>
        <div class="prose max-w-none text-gray-600 leading-relaxed text-justify bg-blue-50/30 p-6 rounded-2xl border border-blue-100/50">
          {{ publication.resumoCurto }}
        </div>
      </div>

      <div v-if="publication.tags && publication.tags.length > 0" class="mb-10">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3 flex items-center">
          <i class="bi bi-tags mr-2"></i> Tópicos Relacionados
        </h3>
        <div class="flex flex-wrap gap-2">
          <NuxtLink
              v-for="tag in publication.tags"
              :key="tag"
              :to="`/tags/${tag}`"
              class="group flex items-center px-4 py-2 bg-white border border-gray-200 rounded-full text-sm font-medium text-gray-600 hover:border-blue-300 hover:text-blue-600 hover:bg-blue-50 transition shadow-sm"
          >
            <span class="text-blue-400 mr-1 group-hover:text-blue-600">#</span> {{ tag }}
          </NuxtLink>
        </div>
      </div>

      <button
          v-if="publication.filename"
          @click="downloadPdf"
          class="w-full group relative flex items-center justify-center px-8 py-4 bg-gray-900 text-white rounded-xl font-bold overflow-hidden transition-all hover:bg-blue-700 hover:shadow-lg hover:shadow-blue-200 hover:-translate-y-1"
      >
        <div class="absolute inset-0 w-full h-full bg-gradient-to-r from-transparent via-white/10 to-transparent -translate-x-full group-hover:animate-shimmer"></div>
        <i class="bi bi-cloud-arrow-down-fill mr-3 text-xl"></i>
        <span>Download Documento (PDF)</span>
      </button>

      <div v-else class="w-full py-4 bg-gray-50 border border-dashed border-gray-300 rounded-xl text-center text-gray-400 text-sm">
        <i class="bi bi-file-earmark-x block text-2xl mb-1"></i>
        Sem ficheiro disponível
      </div>

    </div>

    <div class="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-8">

      <div class="lg:col-span-1">
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 sticky top-4">
          <h3 class="font-bold text-gray-800 mb-2">Avaliar Publicação</h3>
          <p class="text-xs text-gray-500 mb-4">O que achaste deste trabalho?</p>

          <div class="flex justify-center gap-2 mb-2">
            <button v-for="star in 5" :key="star" @click="submitRating(star)" class="focus:outline-none transition-transform hover:scale-110 active:scale-90">
              <i class="bi text-3xl"
                 :class="star <= myRating ? 'bi-star-fill text-yellow-400 drop-shadow-sm' : 'bi-star text-gray-200 hover:text-yellow-200'"></i>
            </button>
          </div>
          <p v-if="myRating > 0" class="text-center text-xs font-bold text-green-600 bg-green-50 py-1 px-2 rounded-lg inline-block w-full">
            <i class="bi bi-check-circle-fill mr-1"></i> Voto registado!
          </p>
        </div>
      </div>

      <div class="lg:col-span-2">
        <div class="bg-white rounded-2xl p-6 md:p-8 shadow-sm border border-gray-100">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-xl font-bold text-gray-800">Discussão</h3>
            <span class="bg-blue-100 text-blue-700 text-xs font-bold px-2 py-1 rounded-lg">{{ publication.comments.length }}</span>
          </div>

          <div class="space-y-6 mb-8">
            <div v-if="publication.comments.length === 0" class="text-center py-10 bg-gray-50 rounded-xl border border-dashed border-gray-200">
              <i class="bi bi-chat-square-text text-3xl text-gray-300 mb-2 block"></i>
              <p class="text-gray-500 text-sm">Ainda não há comentários.</p>
            </div>

            <div v-else v-for="comment in publication.comments" :key="comment.id" class="animate-fadeIn">
              <div class="flex gap-4">
                <div class="w-10 h-10 rounded-full bg-gray-100 flex-shrink-0 flex items-center justify-center font-bold text-gray-600 border border-gray-200">
                  {{ comment.username.charAt(0).toUpperCase() }}
                </div>
                <div class="flex-1">
                  <div class="bg-gray-50 p-4 rounded-2xl rounded-tl-none border border-gray-100 group hover:border-blue-100 transition">
                    <div class="flex justify-between items-start mb-1">
                      <span class="font-bold text-sm text-gray-900">{{ comment.username }}</span>
                    </div>
                    <p class="text-gray-600 text-sm leading-relaxed">{{ comment.text }}</p>
                    <div class="mt-2 flex items-center gap-3">
                      <button @click="toggleReplyBox(comment.id)" class="text-xs font-bold text-gray-400 hover:text-blue-600 transition flex items-center">
                        <i class="bi bi-reply-fill mr-1"></i> Responder
                      </button>
                    </div>
                  </div>

                  <div v-if="activeReplyId === comment.id" class="mt-3 ml-2">
                    <form @submit.prevent="submitReply(comment.id)" class="flex gap-2">
                      <input v-model="replyText" class="flex-1 bg-white border border-blue-200 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-100" placeholder="Escreve uma resposta..." autoFocus />
                      <button type="submit" class="bg-blue-600 text-white px-3 py-2 rounded-lg text-sm font-bold hover:bg-blue-700">
                        <i class="bi bi-send-fill"></i>
                      </button>
                    </form>
                  </div>

                  <div v-if="comment.replies && comment.replies.length" class="mt-3 space-y-3 pl-4 border-l-2 border-gray-100">
                    <div v-for="reply in comment.replies" :key="reply.id" class="flex gap-3">
                      <div class="w-8 h-8 rounded-full bg-blue-50 text-blue-600 flex-shrink-0 flex items-center justify-center text-xs font-bold">
                        {{ reply.username.charAt(0).toUpperCase() }}
                      </div>
                      <div class="flex-1">
                        <div class="bg-white border border-gray-100 p-3 rounded-xl shadow-sm">
                          <span class="font-bold text-xs text-gray-900 block mb-1">{{ reply.username }}</span>
                          <p class="text-gray-600 text-sm">{{ reply.text }}</p>
                          <button @click="toggleReplyBox(reply.id)" class="mt-2 text-[10px] font-bold text-gray-400 hover:text-blue-600 uppercase">Responder</button>
                        </div>

                        <div v-if="activeReplyId === reply.id" class="mt-2">
                          <form @submit.prevent="submitReply(reply.id)" class="flex gap-2">
                            <input v-model="replyText" class="flex-1 bg-white border border-blue-200 rounded-lg px-2 py-1 text-xs outline-none" placeholder="Resposta..." />
                            <button type="submit" class="text-blue-600"><i class="bi bi-send-fill"></i></button>
                          </form>
                        </div>

                        <div v-if="reply.replies && reply.replies.length" class="mt-2 pl-2 border-l border-gray-100 space-y-2">
                          <div v-for="sub in reply.replies" :key="sub.id" class="bg-gray-50 p-2 rounded text-xs text-gray-600">
                            <span class="font-bold text-gray-800">{{ sub.username }}: </span> {{ sub.text }}
                          </div>
                        </div>

                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="border-t border-gray-100 pt-6">
            <form @submit.prevent="submitComment" class="flex gap-3 items-start">
              <div class="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center text-white shadow-md">
                <i class="bi bi-person-fill"></i>
              </div>
              <div class="flex-1 relative">
                <textarea
                    v-model="newComment"
                    class="w-full bg-gray-50 border border-gray-200 rounded-xl p-3 pl-4 pr-12 focus:bg-white focus:ring-2 focus:ring-blue-100 focus:border-blue-300 outline-none transition-all resize-none text-sm min-h-[50px]"
                    placeholder="Partilha a tua opinião..."
                    rows="2"
                ></textarea>
                <button type="submit" class="absolute right-2 bottom-2 text-blue-500 hover:text-blue-700 p-1 transition disabled:opacity-50" :disabled="!newComment">
                  <i class="bi bi-send-fill text-lg"></i>
                </button>
              </div>
            </form>
          </div>

        </div>
      </div>
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
const { token, user } = storeToRefs(authStore);
const config = useRuntimeConfig();
const api = config.public.apiBase;

const publication = ref(null);
const isOwner = ref(false);
const newComment = ref("");
const replyText = ref("");
const activeReplyId = ref(null);
const myRating = ref(0);

async function fetchPublication() {
  try {
    const res = await $fetch(`${api}/publicacoes/${route.params.id}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    publication.value = res;
    if (user.value) isOwner.value = user.value.username === publication.value.username;
  } catch (err) { router.push("/"); }
}

onMounted(async () => {
  if (!token.value) { router.push("/auth/login"); return; }
  if (!user.value) await authStore.fetchUser();
  fetchPublication();
});

async function submitRating(value) {
  try {
    myRating.value = value;
    await $fetch(`${api}/publicacoes/${publication.value.id}/rating`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { value: value }
    });
    await fetchPublication();
  } catch (e) { console.error(e); alert("Erro ao votar."); }
}

async function submitComment() {
  if (!newComment.value.trim()) return;
  try {
    await $fetch(`${api}/publicacoes/${publication.value.id}/comentarios`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { text: newComment.value }
    });
    newComment.value = "";
    fetchPublication();
  } catch (e) { alert("Erro ao comentar."); }
}

function toggleReplyBox(commentId) {
  activeReplyId.value = activeReplyId.value === commentId ? null : commentId;
  replyText.value = "";
}

async function submitReply(commentId) {
  if (!replyText.value.trim()) return;
  try {
    await $fetch(`${api}/publicacoes/${publication.value.id}/comentarios/${commentId}/reply`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { text: replyText.value }
    });
    activeReplyId.value = null;
    replyText.value = "";
    fetchPublication();
  } catch (e) { alert("Erro ao responder."); }
}

async function downloadPdf() {
  try {
    const pdfBlob = await $fetch(`${api}/publicacoes/${publication.value.id}/download`, {
      headers: { Authorization: `Bearer ${token.value}` },
      responseType: "blob",
    });
    const url = URL.createObjectURL(pdfBlob);
    const link = document.createElement("a");
    link.href = url;
    link.download = publication.value.filename || "documento.pdf";
    link.click();
    URL.revokeObjectURL(url);
  } catch (err) { alert("Erro no download ou ficheiro inexistente."); }
}

async function deletePublication() {
  if (!confirm("Apagar publicação permanentemente?")) return;
  try {
    await $fetch(`${api}/publicacoes/${publication.value.id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token.value}` },
    });
    router.push("/");
  } catch (err) { alert("Erro ao apagar."); }
}

function goToEdit() { router.push(`/publications/edit/${publication.value.id}`); }
</script>

<style scoped>
.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes shimmer {
  100% { transform: translateX(100%); }
}
.group-hover\:animate-shimmer {
  animation: shimmer 1.5s infinite;
}
</style>