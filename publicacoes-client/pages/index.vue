<template>
  <div class="container mt-4">
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

    <div v-else>
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
          <NuxtLink to="/publications/create" class="btn btn-success">
            <i class="bi bi-cloud-upload"></i> Submeter Nova Publicação
          </NuxtLink>
        </div>
      </div>

      <div class="card shadow-sm border-0 mb-5 bg-light">
        <div class="card-body p-4">
          <form @submit.prevent="pesquisar">
            <div class="input-group input-group-lg">
              <span class="input-group-text bg-white border-end-0">🔍</span>
              <input
                  v-model="searchQuery"
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
              <a href="#" @click.prevent="aplicarSugestao('Ciência de Dados')" class="text-decoration-none">Ciência de Dados</a>,
              <a href="#" @click.prevent="aplicarSugestao('Materiais')" class="text-decoration-none">Materiais</a>
            </div>
          </form>
        </div>
      </div>

      <div class="row">
        <div class="col-md-8">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="mb-0">Últimas Publicações do Centro</h4>
            <span v-if="loading" class="spinner-border spinner-border-sm text-primary"></span>
          </div>

          <div v-if="!loading && publicationStore.publications.length === 0" class="alert alert-info">
            Nenhuma publicação encontrada. Tente outros termos.
          </div>

          <div class="list-group shadow-sm">
            <div
                v-for="p in publicationStore.publications"
                :key="p.id"
                class="list-group-item list-group-item-action flex-column align-items-start p-4"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">
                  <NuxtLink :to="`/publications/${p.id}`" class="text-decoration-none text-dark fw-bold">
                    {{ p.titulo }}
                  </NuxtLink>
                </h5>
                <small class="text-muted">{{ p.publicationDate ? new Date(p.publicationDate).toLocaleDateString() : '2025' }}</small>
              </div>
              <p class="mb-2 text-secondary">{{ p.resumoCurto }}</p>

              <div class="mt-2">
                <span class="badge bg-primary me-2">{{ p.area }}</span>
                <span class="badge bg-light text-dark border">{{ p.tipo }}</span>
              </div>

              <div class="mt-2 text-muted small">
                Tags:
                <span v-for="tag in p.tags" :key="tag.id" class="me-1">#{{ tag.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-4">

          <div class="card mb-4 shadow-sm border-0 bg-light">
            <div class="card-header bg-white fw-bold">Filtros Avançados</div>
            <div class="card-body">
              <div class="mb-3">
                <label class="form-label small fw-bold">Área Científica</label>
                <input v-model="filters.area" class="form-control" placeholder="Ex: Informática">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-bold">Tipo</label>
                <select v-model="filters.tipo" class="form-select">
                  <option value="">Todos</option>
                  <option value="Artigo">Artigo</option>
                  <option value="Tese">Tese</option>
                  <option value="Relatório">Relatório</option>
                </select>
              </div>
              <button @click="aplicarFiltros" class="btn btn-outline-primary w-100 btn-sm">
                Filtrar Resultados
              </button>
              <button @click="limparFiltros" class="btn btn-link text-muted w-100 btn-sm text-decoration-none mt-1">
                Limpar Filtros
              </button>
            </div>
          </div>
          <div class="card mb-3 shadow-sm border-0">
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
                <NuxtLink to="/tags" class="text-decoration-none">
                  🏷️ Gerir Tags
                </NuxtLink>
              </li>
            </ul>
          </div>

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
import { onMounted, ref, watch } from "vue";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";
import { useRouter, useRoute } from "vue-router";

const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);
const publicationStore = usePublicationStore();
const router = useRouter();
const route = useRoute();

// Variáveis de Estado (ADICIONADO)
const searchQuery = ref("");
const loading = ref(false);
const filters = ref({
  area: "",
  tipo: ""
});

// Função de Pesquisa Principal (ALTERADO de Alert para Lógica Real)
const pesquisar = async () => {
  // Atualiza a URL com a query para manter estado se der refresh
  router.push({ query: { ...route.query, q: searchQuery.value } });
  await fetchResults();
}

// Função para buscar dados da store com filtros
const fetchResults = async () => {
  if(!token.value) return;
  loading.value = true;
  try {
    await publicationStore.fetchAll(token.value, {
      query: searchQuery.value, // Parâmetro 'q' ou 'query' esperado pelo backend
      area: filters.value.area,
      tipo: filters.value.tipo
    });
  } catch (e) {
    console.error("Erro na pesquisa", e);
  } finally {
    loading.value = false;
  }
}

// Função auxiliar para sugestões
const aplicarSugestao = (termo) => {
  searchQuery.value = termo;
  pesquisar();
}

const aplicarFiltros = () => {
  fetchResults();
}

const limparFiltros = () => {
  filters.value.area = "";
  filters.value.tipo = "";
  searchQuery.value = "";
  router.push({ path: '/' }); // Limpa a query da URL
  fetchResults();
}

// Watch para reagir a mudanças na URL (ex: clicar 'voltar' no browser)
watch(() => route.query.q, (newQ) => {
  if(newQ !== undefined) {
    searchQuery.value = newQ;
    fetchResults();
  }
});

onMounted(() => {
  // Se houver algo na URL, preenche a barra de pesquisa
  if(route.query.q) {
    searchQuery.value = route.query.q;
  }
  // Faz o fetch inicial
  fetchResults();
});
</script>