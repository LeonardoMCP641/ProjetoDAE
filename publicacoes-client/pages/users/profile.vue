<template>
  <div class="min-h-screen bg-slate-50 py-12 px-4">
    <!-- Contentor Alargado para caberem dois lados -->
    <div class="max-w-6xl mx-auto">

      <!-- CABEÇALHO CENTRADO -->
      <div class="text-center mb-10">
        <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">Definições de Perfil</h1>
        <p class="text-slate-500 mt-2">Faça a gestão dos seus dados e segurança numa vista única.</p>
      </div>

      <!-- GRELHA: 1 coluna em mobile, 2 colunas em desktop (lg) -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start">

        <!-- COLUNA ESQUERDA: INFORMAÇÕES DA CONTA -->
        <section class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden h-full">
          <div class="p-6 border-b border-slate-100 bg-slate-50/50">
            <h2 class="font-bold text-slate-800 flex items-center text-lg">
              <span class="w-2.5 h-2.5 bg-blue-600 rounded-full mr-3"></span>
              Informações da Conta
            </h2>
          </div>

          <form @submit.prevent="updateEmail" class="p-8 space-y-5">
            <div class="flex flex-col">
              <label class="text-sm font-semibold text-slate-700 mb-1.5 ml-1">Nome Completo</label>
              <input :value="user?.name" disabled class="w-full px-4 py-3 rounded-xl border border-slate-200 bg-slate-50 text-slate-400 cursor-not-allowed text-sm outline-none" />
            </div>

            <div class="flex flex-col">
              <label class="text-sm font-semibold text-slate-700 mb-1.5 ml-1">Username</label>
              <input :value="user?.username" disabled class="w-full px-4 py-3 rounded-xl border border-slate-200 bg-slate-50 text-slate-400 cursor-not-allowed text-sm outline-none" />
            </div>

            <div class="flex flex-col">
              <label class="text-sm font-semibold text-slate-700 mb-1.5 ml-1">Cargo / Role</label>
              <div class="w-full px-4 py-3 rounded-xl border border-slate-200 bg-slate-50 flex items-center">
                <span class="px-2.5 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wider" :class="getRoleClass(user?.role)">
                  {{ user?.role }}
                </span>
              </div>
            </div>

            <div class="flex flex-col pt-2">
              <label class="text-sm font-semibold text-slate-900 mb-1.5 ml-1">Endereço de E-mail</label>
              <input v-model="emailForm.email" type="email" required class="w-full px-4 py-3 rounded-xl border border-blue-200 bg-white text-slate-800 text-sm focus:ring-4 focus:ring-blue-50/50 focus:border-blue-500 outline-none transition-all shadow-sm" />
              <p class="text-[11px] text-slate-400 mt-2 ml-1 italic leading-relaxed">Este e-mail é utilizado para todas as notificações do Centro.</p>
            </div>

            <div class="pt-4">
              <button type="submit" :disabled="loadingEmail" class="w-full py-3.5 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-md hover:shadow-lg transition-all active:scale-[0.98] disabled:opacity-50 flex justify-center items-center">
                <svg v-if="loadingEmail" class="animate-spin h-4 w-4 mr-2 border-2 border-white border-t-transparent rounded-full" viewBox="0 0 24 24"></svg>
                {{ loadingEmail ? 'A guardar...' : 'Atualizar E-mail' }}
              </button>
            </div>
          </form>
        </section>

        <!-- COLUNA DIREITA: SEGURANÇA -->
        <section class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden h-full">
          <div class="p-6 border-b border-slate-100 bg-slate-50/50">
            <h2 class="font-bold text-slate-800 flex items-center text-lg">
              <span class="w-2.5 h-2.5 bg-slate-800 rounded-full mr-3"></span>
              Segurança e Acesso
            </h2>
          </div>

          <form @submit.prevent="changePassword" class="p-8 space-y-5">
            <div class="flex flex-col">
              <label class="text-sm font-semibold text-slate-700 mb-1.5 ml-1">Palavra-passe Atual</label>
              <input v-model="passData.oldPassword" type="password" required placeholder="••••••••" class="w-full px-4 py-3 rounded-xl border border-slate-200 text-sm outline-none focus:ring-4 focus:ring-slate-50 focus:border-slate-400 transition-all" />
            </div>

            <div class="flex flex-col">
              <label class="text-sm font-semibold text-slate-700 mb-1.5 ml-1">Nova Palavra-passe</label>
              <input v-model="passData.newPassword" type="password" required placeholder="Mínimo 8 caracteres" class="w-full px-4 py-3 rounded-xl border border-slate-200 text-sm outline-none focus:ring-4 focus:ring-slate-50 focus:border-slate-400 transition-all" />
            </div>

            <div class="pt-4">
              <button type="submit" class="w-full py-3.5 px-4 bg-slate-800 hover:bg-black text-white font-bold rounded-xl shadow-md hover:shadow-lg transition-all active:scale-[0.98]">
                Alterar Palavra-passe
              </button>
            </div>
          </form>
        </section>

      </div>

      <!-- RODAPÉ -->
      <div class="text-center mt-12">
        <p class="text-xs text-slate-400 uppercase tracking-widest font-medium">Plataforma de Gestão Científica &bull; 2025</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const { user, token } = storeToRefs(authStore);
const config = useRuntimeConfig();

const loadingEmail = ref(false);
const emailForm = reactive({ email: '' });
const passData = reactive({ oldPassword: '', newPassword: '' });

watchEffect(() => {
  if (user.value) emailForm.email = user.value.email;
});

async function updateEmail() {
  loadingEmail.value = true;
  try {
    console.log("Email do form" + emailForm.email);
    await $fetch(`${config.public.apiBase}/users/${user.value.id}/email`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { email: emailForm.email }
    });
    await authStore.fetchUser();
    alert("Perfil atualizado com sucesso!");
    } catch (e) {
    console.error("Erro completo:", e); // Isto imprime o objeto de erro todo na consola
    console.log("Resposta do servidor:", e.data); // Isto imprime a mensagem vinda do Java
    alert("Erro: " + (e.data || "Ver consola para detalhes"));
  } finally {
    loadingEmail.value = false;
  }
}

async function changePassword() {
  try {
    // ALTERAÇÃO: Mudar de user.value.username para user.value.id
    await $fetch(`${config.public.apiBase}/users/${user.value.id}/password`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token.value}` },
      body: passData // passData já tem oldPassword e newPassword
    });

    alert("Password alterada com sucesso!");

    // Limpar os campos após o sucesso
    passData.oldPassword = '';
    passData.newPassword = '';

  } catch (e) {
    // ALTERAÇÃO: Mostrar a mensagem específica enviada pelo Java (ex: "Password antiga incorreta")
    console.error("Erro ao mudar password:", e);
    alert("Erro: " + (e.data || "Verifique se a password atual está correta."));
  }
}

function getRoleClass(role) {
  switch(role) {
    case 'Administrador': return 'bg-red-100 text-red-700 border border-red-200';
    case 'Responsável': return 'bg-amber-100 text-amber-700 border border-amber-200';
    case 'Colaborador': return 'bg-emerald-100 text-emerald-700 border border-emerald-200';
    default: return 'bg-slate-100 text-slate-600';
  }
}
</script>