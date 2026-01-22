<template>
  <div v-if="publication" class="max-w-4xl mx-auto mb-16 px-4 sm:px-0">

    <div class="relative bg-gradient-to-br from-blue-700 to-blue-900 rounded-t-3xl p-10 text-white shadow-xl overflow-hidden">
      <div class="absolute top-0 right-0 -mt-10 -mr-10 w-64 h-64 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>

      <div class="relative z-10">
        <div class="flex justify-between items-start mb-6">
          <div class="flex gap-2">
            <span class="px-3 py-1 rounded-full text-xs font-bold uppercase bg-white/20 backdrop-blur-md border border-white/10 shadow-sm">{{ publication.area }}</span>
            <span class="px-3 py-1 rounded-full text-xs font-bold uppercase bg-blue-500/30 backdrop-blur-md border border-white/10 shadow-sm">{{ publication.tipo }}</span>
            <span class="px-3 py-1 rounded-full text-xs font-bold uppercase border border-white/10"
                  :class="publication.visivel ? 'bg-emerald-500/20 text-emerald-100' : 'bg-rose-500/20 text-rose-100'">
              <i class="bi" :class="publication.visivel ? 'bi-globe' : 'bi-lock-fill'"></i>
              {{ publication.visivel ? 'Público' : 'Privado' }}
            </span>
          </div>

          <div class="flex gap-2">
            <button v-if="isOwner" @click="goToEdit" class="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition backdrop-blur-md text-white" title="Editar">
              <i class="bi bi-pencil-fill text-sm"></i>
            </button>
            <button v-if="canManage" @click="toggleVisibility" class="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition backdrop-blur-md text-white" :title="publication.visivel ? 'Ocultar' : 'Mostrar'">
              <i class="bi" :class="publication.visivel ? 'bi-eye-slash-fill' : 'bi-eye-fill'"></i>
            </button>
            <button v-if="canManage" @click="deletePublication" class="w-8 h-8 rounded-full bg-red-500/20 hover:bg-red-500/40 text-red-200 flex items-center justify-center transition backdrop-blur-md" title="Apagar">
              <i class="bi bi-trash-fill text-sm"></i>
            </button>
          </div>
        </div>

        <h1 class="text-3xl md:text-5xl font-bold leading-tight mb-2 tracking-tight">{{ publication.titulo }}</h1>
        <p class="text-blue-200 text-sm mt-2 opacity-80 flex items-center">
          <i class="bi bi-calendar-event mr-2"></i>
          {{ publication.publicationDate ? new Date(publication.publicationDate).toLocaleDateString() : 'Data desconhecida' }}
        </p>
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
            <i v-for="star in 5" :key="star" class="bi" :class="star <= Math.round(publication.ratingAverage) ? 'bi-star-fill' : 'bi-star text-gray-200'"></i>
          </div>
          <p class="text-xs font-medium text-gray-400">Média: {{ publication.ratingAverage.toFixed(1) }} ({{ publication.ratingCount }} votos)</p>
        </div>
      </div>

      <div class="mb-8">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3">Resumo</h3>
        <div class="prose max-w-none text-gray-600 leading-relaxed text-justify bg-blue-50/30 p-6 rounded-2xl border border-blue-100/50">
          {{ publication.resumoCurto }}
        </div>
      </div>

      <div class="mb-8" v-if="publication.autores && publication.autores.length > 0">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3">Autores</h3>
        <div class="flex flex-wrap gap-2">
            <span v-for="(autor, i) in publication.autores" :key="i" class="px-3 py-1 bg-gray-50 text-gray-600 rounded-lg text-sm font-medium border border-gray-200">
            <i class="bi bi-person mr-1"></i> {{ autor }}
          </span>
        </div>
      </div>

      <div v-if="publication.tags && publication.tags.length > 0" class="mb-10">
        <h3 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3">Tópicos / Tags</h3>
        <div class="flex flex-wrap gap-2">
          <NuxtLink v-for="tag in publication.tags" :key="tag" :to="`/tags/${tag}`" class="px-4 py-2 bg-white border border-gray-200 rounded-full text-sm font-medium text-gray-600 hover:text-blue-600 hover:border-blue-300 transition">
            # {{ tag }}
          </NuxtLink>
        </div>
      </div>

      <button v-if="publication.filename" @click="downloadPdf" class="w-full flex items-center justify-center px-8 py-4 bg-gray-900 text-white rounded-xl font-bold hover:bg-blue-700 transition shadow-lg transform hover:-translate-y-1">
        <i class="bi bi-cloud-arrow-down-fill mr-3 text-xl"></i> Download PDF
      </button>
      <div v-else class="w-full py-4 bg-gray-50 border border-dashed border-gray-300 rounded-xl text-center text-gray-400 text-sm">
        Sem ficheiro disponível
      </div>
    </div>

    <div class="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-1">
        <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 sticky top-4">
          <h3 class="font-bold text-gray-800 mb-2">Avaliar</h3>
          <div class="flex justify-center gap-2 mb-2">
            <button v-for="star in 5" :key="star" @click="submitRating(star)" class="transition hover:scale-110">
              <i class="bi text-3xl" :class="star <= myRating ? 'bi-star-fill text-yellow-400' : 'bi-star text-gray-200 hover:text-yellow-200'"></i>
            </button>
          </div>
          <p v-if="myRating > 0" class="text-center text-xs text-green-600 font-bold bg-green-50 rounded py-1">Voto registado!</p>
        </div>
      </div>

      <div class="lg:col-span-2">
        <div class="bg-white rounded-2xl p-6 md:p-8 shadow-sm border border-gray-100">
          <h3 class="text-xl font-bold text-gray-800 mb-6">Comentários ({{ publication.comments ? publication.comments.length : 0 }})</h3>

          <div class="space-y-6 mb-8">
            <div v-if="!publication.comments || publication.comments.length === 0" class="text-center text-gray-400 py-4">
              Sê o primeiro a comentar!
            </div>

            <div v-else v-for="comment in publication.comments" :key="comment.id" class="group">

              <div v-if="!comment.visivel && !isChefe" class="bg-gray-50 p-4 rounded-2xl italic text-gray-400 text-sm mb-4 border border-gray-100">
                <i class="bi bi-eye-slash-fill mr-2"></i> Comentário ocultado pela moderação.
              </div>

              <div v-else class="flex gap-3 mb-4" :class="!comment.visivel ? 'opacity-70' : ''">
                <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center font-bold text-gray-600 flex-shrink-0">
                  {{ comment.username.charAt(0).toUpperCase() }}
                </div>
                <div class="flex-1">
                  <div class="bg-gray-50 p-4 rounded-2xl rounded-tl-none relative border"
                       :class="!comment.visivel ? 'border-red-200 bg-red-50' : 'border-gray-100'">

                    <div class="flex justify-between items-start mb-1">
                      <span class="font-bold text-sm text-gray-900">{{ comment.username }}</span>

                      <button v-if="isChefe" @click="toggleCommentVisibility(comment.id)"
                              class="text-gray-400 hover:text-blue-600 transition"
                              :title="comment.visivel ? 'Ocultar comentário' : 'Mostrar comentário'">
                        <i class="bi" :class="comment.visivel ? 'bi-eye-slash' : 'bi-eye-fill'"></i>
                      </button>
                    </div>

                    <p class="text-gray-600 text-sm">
                      <span v-if="!comment.visivel" class="text-red-500 font-bold text-xs uppercase mr-1">[Oculto]</span>
                      {{ comment.text }}
                    </p>
                    <button @click="toggleReplyBox(comment.id)" class="text-xs font-bold text-blue-500 mt-2 hover:underline">Responder</button>
                  </div>

                  <div v-if="activeReplyId === comment.id" class="mt-2 ml-2 animate-fadeIn">
                    <form @submit.prevent="submitReply(comment.id)" class="flex gap-2">
                      <input v-model="replyText" class="flex-1 border rounded-lg px-2 text-sm py-1" placeholder="Escreve uma resposta..." autoFocus />
                      <button type="submit" class="text-blue-600 text-sm font-bold">Enviar</button>
                    </form>
                  </div>

                  <div v-if="comment.replies && comment.replies.length > 0" class="mt-2 pl-4 border-l-2 border-gray-100 space-y-2">
                    <div v-for="reply in comment.replies" :key="reply.id" class="bg-white border border-gray-100 p-3 rounded-xl relative group-reply">

                      <div v-if="!reply.visivel && !isChefe" class="italic text-gray-400 text-xs">
                        <i class="bi bi-eye-slash mr-1"></i> Resposta ocultada.
                      </div>

                      <div v-else :class="!reply.visivel ? 'opacity-60 bg-red-50' : ''">
                        <div class="flex justify-between items-start">
                          <span class="font-bold text-xs block text-gray-800">{{ reply.username }}</span>
                          <button v-if="isChefe" @click="toggleCommentVisibility(reply.id)" class="text-gray-300 hover:text-blue-600 text-xs">
                            <i class="bi" :class="reply.visivel ? 'bi-eye-slash' : 'bi-eye-fill'"></i>
                          </button>
                        </div>
                        <p class="text-gray-500 text-xs mt-1">
                          <span v-if="!reply.visivel" class="text-red-500 font-bold">[Oculto]</span>
                          {{ reply.text }}
                        </p>
                      </div>

                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <form @submit.prevent="submitComment" class="flex gap-3 relative">
            <textarea v-model="newComment" class="w-full bg-gray-50 border border-gray-200 rounded-xl p-3 pl-4 text-sm focus:ring-2 focus:ring-blue-100 outline-none resize-none" placeholder="Escreve um comentário..." rows="2"></textarea>
            <button type="submit" class="absolute right-2 bottom-2 text-blue-600 hover:bg-blue-50 p-2 rounded-lg transition"><i class="bi bi-send-fill"></i></button>
          </form>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
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
const newComment = ref("");
const replyText = ref("");
const activeReplyId = ref(null);
const myRating = ref(0);

// PERMISSÕES 🛡️
const isOwner = computed(() => user.value && publication.value && user.value.username === publication.value.username);
// "Chefe" = Admin ou Responsável (para moderação global)
const isChefe = computed(() => user.value && (user.value.role === 'Administrador' || user.value.role === 'Responsavel'));
// Gestão da Página = Dono ou Chefe
const canManage = computed(() => isOwner.value || isChefe.value);

async function fetchPublication() {
  try {
    const res = await $fetch(`${api}/publicacoes/${route.params.id}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    publication.value = res;
  } catch (err) { router.push("/"); }
}

onMounted(async () => {
  if (!token.value) { router.push("/auth/login"); return; }
  if (!user.value) await authStore.fetchUser();
  fetchPublication();
});

// 👁️ MODERAÇÃO DE COMENTÁRIOS
async function toggleCommentVisibility(commentId) {
  if (!confirm("Alterar a visibilidade deste comentário?")) return;
  try {
    await $fetch(`${api}/publicacoes/comentarios/${commentId}/visibilidade`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token.value}` }
    });
    await fetchPublication(); // Recarrega para ver a mudança
  } catch (e) { alert("Erro ao moderar comentário."); }
}

// 👁️ MODERAÇÃO DA PUBLICAÇÃO
async function toggleVisibility() {
  const novoEstado = !publication.value.visivel;
  const acao = novoEstado ? "tornar visível" : "ocultar";
  if (!confirm(`Queres ${acao} esta publicação?`)) return;

  try {
    const payload = { ...publication.value, visivel: novoEstado };
    await $fetch(`${api}/publicacoes/${publication.value.id}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token.value}` },
      body: payload
    });
    publication.value.visivel = novoEstado;
    alert(`Publicação ${novoEstado ? 'visível' : 'oculta'}!`);
  } catch (e) { alert("Erro ao alterar visibilidade."); }
}

async function submitRating(value) {
  try {
    myRating.value = value;
    await $fetch(`${api}/publicacoes/${publication.value.id}/rating`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { value: value }
    });
    fetchPublication();
  } catch (e) { alert("Erro ao votar."); }
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
  if(!replyText.value.trim()) return;
  try {
    await $fetch(`${api}/publicacoes/${publication.value.id}/comentarios/${commentId}/reply`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { text: replyText.value }
    });
    activeReplyId.value = null;
    replyText.value = "";
    fetchPublication();
  } catch(e) { alert("Erro ao responder."); }
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
    link.download = publication.value.filename || `${publication.value.titulo}.pdf`;
    link.click();
  } catch (err) { alert("Erro no download."); }
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
.animate-fadeIn { animation: fadeIn 0.3s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
</style>