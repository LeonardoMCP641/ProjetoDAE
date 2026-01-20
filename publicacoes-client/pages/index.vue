<template>
  <div class="container mt-4">
    <!-- === VISÃO DE VISITANTE (Não Logado) === -->
    <div v-if="!token" class="text-center py-5">
      <h1 class="display-4 fw-bold text-primary mb-3">Centro de I&D XYZ</h1>
      <p class="lead text-muted mb-4">
        Plataforma de gestão, arquivo e partilha de conhecimento científico.<br />
        Aceda a artigos, teses, datasets e software produzidos pelos nossos
        investigadores.
      </p>

      <div class="d-flex justify-content-center gap-3">
        <NuxtLink to="/auth/login" class="btn btn-primary btn-lg px-5"
          >Entrar na Plataforma</NuxtLink
        >
      </div>

      <div class="row mt-5 pt-4 border-top">
        <div class="col-md-4">
          <h3>🔍</h3>
          <h5>Pesquisa Avançada</h5>
          <p class="small text-muted">
            Encontre publicações por tags, autores ou áreas científicas.
          </p>
        </div>
        <div class="col-md-4">
          <h3>🌐</h3>
          <h5>Partilha de Conhecimento</h5>
          <p class="small text-muted">
            Acesso a Datasets e Software Open Source.
          </p>
        </div>
        <div class="col-md-4">
          <h3>💬</h3>
          <h5>Comentários & Reviews</h5>
          <p class="small text-muted">
            Discuta e analise artigos com os seus pares.
          </p>
        </div>
      </div>
    </div>

    <!-- === VISÃO DE UTILIZADOR LOGADO (Dashboard Geral) === -->
    <div v-else>
      <!-- Cabeçalho de Boas-vindas -->
      <div class="row mb-5 align-items-center">
        <div class="col-md-8">
          <h2 class="fw-bold">Olá, {{ user?.name }} 👋</h2>
          <p class="text-muted">
            O que pretende pesquisar hoje na área de
            <strong>{{ user?.role }}</strong
            >?
          </p>
        </div>
        <div class="col-md-4 text-end">
          <!-- Botão de Upload Rápido (Requisito: Colaborador faz upload) -->
          <NuxtLink to="/publications/create" class="btn btn-success">
            <i class="bi bi-cloud-upload"></i> Submeter Nova Publicação
          </NuxtLink>
        </div>
      </div>

      <!-- Barra de Pesquisa Principal (Foco do Cenário 1) -->
      <div class="card shadow-sm border-0 mb-5 bg-light">
        <div class="card-body p-4">
          <form @submit.prevent="pesquisar">
            <div class="input-group input-group-lg">
              <span class="input-group-text bg-white border-end-0">🔍</span>
              <input
                type="text"
                class="form-control border-start-0"
                placeholder="Pesquisar por título, autor, área (ex: Ciência de Dados)..."
              />
              <button class="btn btn-primary px-4" type="submit">
                Pesquisar
              </button>
            </div>
            <div class="mt-2 small text-muted">
              Sugestões:
              <a href="#" class="text-decoration-none">Ciência de Dados</a>,
              <a href="#" class="text-decoration-none">Materiais</a>,
              <a href="#" class="text-decoration-none">Projeto X</a>
            </div>
          </form>
        </div>
      </div>

      <!-- Áreas de Conteúdo -->
      <div class="row">
        <!-- Coluna Esquerda: Minhas Coisas -->
        <div class="col-md-8">
          <h4 class="mb-3">Últimas Publicações do Centro</h4>

          <!-- Exemplo de Lista de Artigos (Placeholder) -->
          <div
            v-for="p in publicationStore.publications.filter(
              (pub) => pub.visivel,
            )"
            :key="p.id"
            class="list-group-item"
          >
            <NuxtLink :to="`/publications/${p.id}`">
              <h5>{{ p.titulo }}</h5>
              <p>{{ p.resumoCurto }}</p>
            </NuxtLink>
          </div>
        </div>

        <!-- Coluna Direita: Atalhos e Perfil -->
        <div class="col-md-4">
          <div class="card mb-3">
            <div class="card-header bg-white fw-bold">Atalhos</div>
            <ul class="list-group list-group-flush">
              <li class="list-group-item">
                <NuxtLink to="/publications/mine" class="text-decoration-none">
                  📂 Minhas Publicações
                </NuxtLink>
              </li>
              <li class="list-group-item">
                <a href="#" class="text-decoration-none">⭐ Favoritos</a>
              </li>
              <li class="list-group-item">
                <a href="#" class="text-decoration-none"
                  >🔔 Notificações (Tags subscritas)</a
                >
              </li>
            </ul>
          </div>

          <!-- Só mostra isto se for ADMIN -->
          <div
            v-if="user?.role === 'Administrador'"
            class="card border-warning mb-3"
          >
            <div class="card-header bg-warning text-dark fw-bold">
              Área de Gestão
            </div>
            <div class="card-body">
              <p class="small mb-2">Acesso exclusivo a administradores.</p>
              <NuxtLink to="/users" class="btn btn-outline-dark btn-sm w-100"
                >Gerir Utilizadores</NuxtLink
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/auth-store.js";
import { onMounted } from "vue";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";
// import { storeToRefs } from "pinia"; // Nuxt auto-importa

const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);

function pesquisar() {
  alert(
    "Esta funcionalidade será implementada quando criarmos a tabela de Publicações!",
  );
}

const publicationStore = usePublicationStore();

onMounted(() => {
  publicationStore.fetchAll(token.value);
});
</script>
