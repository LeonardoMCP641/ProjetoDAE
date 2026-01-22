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
              <a href="#" @click.prevent="aplicarSugestao('Materiais')" class="text-decoration-none">Materiais</a>,
              <a href="#" @click.prevent="aplicarSugestao('Projeto X')" class="text-decoration-none">Projeto X</a>
            </div>
          </form>
        </div>
      </div>

      <div class="row">

        <div class="col-md-8">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="mb-0">Últimas Publicações</h4>
            <span v-if="loading" class="spinner-border spinner-border-sm text-primary"></span>
          </div>

          <div  class="card shadow-sm border-0">
            <div class="card-body p-0">
              <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                  <thead class="table-light">
                  <tr>
                    <th scope="col" class="ps-4">Título</th>
                    <th scope="col">Área / Tipo</th>
                    <th scope="col">Data</th>
                    <th scope="col">Autores</th>
                    <th scope="col" class="text-end pe-4">Ação</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="p in publicationStore.publications" :key="p.id">
                    <td class="ps-4">
                      <NuxtLink :to="`/publications/${p.id}`" class="text-decoration-none fw-bold text-dark">
                        {{ p.titulo }}
                      </NuxtLink>
                      <div class="text-muted small text-truncate" style="max-width: 250px;">
                        {{ p.resumoCurto }}
                      </div>
                    </td>

                    <td>
                      <div class="badge bg-primary mb-1">{{ p.area }}</div>
                      <div class="text-muted small">{{ p.tipo }}</div>
                    </td>

                    <td class="small text-nowrap">
                      {{ p.publicationDate ? new Date(p.publicationDate).toLocaleDateString() : 'N/A' }}
                    </td>

                    <td class="small">
                        <span v-if="p.autores && p.autores.length">
                          {{ p.autores.slice(0, 2).join(", ") }}
                          <span v-if="p.autores.length > 2" class="text-muted fst-italic"> +{{ p.autores.length - 2 }}</span>
                        </span>
                      <span v-else class="text-muted">-</span>
                    </td>

                    <td class="text-end pe-4">
                      <NuxtLink :to="`/publications/${p.id}`" class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-eye"></i> Ver
                      </NuxtLink>
                    </td>
                  </tr>
                  </tbody>
                </table>
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
              <button @click="aplicarFiltros" class="btn btn-primary w-100 btn-sm">
                Filtrar Resultados
              </button>
              <button @click="limparFiltros" class="btn btn-link text-muted w-100 btn-sm text-decoration-none mt-1">
                Limpar Filtros
              </button>
            </div>
          </div>

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
                >🔔 Notificações</a
                >
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

// Variáveis de Estado
const searchQuery = ref("");
const loading = ref(false);
const filters = ref({
  area: "",
  tipo: ""
});

// Executa a pesquisa (barra central)
const pesquisar = async () => {
  // Atualiza a URL para manter o estado se fizer refresh
  router.push({ query: { ...route.query, q: searchQuery.value } });
  await fetchResults();
}

// Busca os dados na API (usando a store)
const fetchResults = async () => {
  if(!token.value) return;

  loading.value = true;
  try {
    // Envia o texto e os filtros para a store (que chama o backend)
    await publicationStore.fetchAll(token.value, {
      query: searchQuery.value,
      area: filters.value.area,
      tipo: filters.value.tipo
    });

    // === FORÇAR ORDENAÇÃO POR DATA (MAIS RECENTE PRIMEIRO) ===
    // Caso o backend não garanta a ordem, fazemos aqui no frontend
    if (publicationStore.publications && publicationStore.publications.length > 0) {
      publicationStore.publications.sort((a, b) => {
        const dateA = new Date(a.publicationDate || 0);
        const dateB = new Date(b.publicationDate || 0);
        return dateB - dateA; // Decrescente (B - A)
      });
    }

  } catch (e) {
    console.error("Erro ao pesquisar", e);
  } finally {
    loading.value = false;
  }
}

// Atalho para as sugestões de texto
const aplicarSugestao = (termo) => {
  searchQuery.value = termo;
  pesquisar();
}

// Botão Filtrar
const aplicarFiltros = () => {
  fetchResults();
}

// Botão Limpar
const limparFiltros = () => {
  filters.value.area = "";
  filters.value.tipo = "";
  searchQuery.value = "";
  router.push({ path: '/' }); // Remove query da URL
  fetchResults();
}

// Sincroniza a barra de pesquisa se a URL mudar (ex: botão voltar do browser)
watch(() => route.query.q, (newQ) => {
  if(newQ !== undefined) {
    searchQuery.value = newQ;
    fetchResults();
  }
});

onMounted(() => {
  // Se a URL já tiver ?q=..., preenche a barra
  if(route.query.q) {
    searchQuery.value = route.query.q;
  }
  // Carrega os dados iniciais (já ordenados pela função fetchResults)
  fetchResults();
});
</script>