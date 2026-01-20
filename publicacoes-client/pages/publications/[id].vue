<template>
  <div class="container mt-4">
    <div v-if="publication">
      <h2>{{ publication.titulo }}</h2>
      <p>{{ publication.resumoCurto }}</p>
      <p>Autores: {{ publication.autores.join(", ") }}</p>

      <!-- Botão para download -->
      <button
        v-if="publication.filename"
        @click="downloadPdf"
        class="btn btn-primary mt-3"
      >
        Download PDF
      </button>

      <!-- Botões Editar e Eliminar -->
      <div class="mt-3">
        <button class="btn btn-warning me-2" @click="goToEdit">
          Editar
        </button>
        <button class="btn btn-danger" @click="deletePublication">
          Eliminar
        </button>
      </div>
    </div>

    <div v-else>
      <p>Carregando publicação...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth-store.js'
import { storeToRefs } from 'pinia'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { token } = storeToRefs(authStore)

const publication = ref(null)

onMounted(async () => {
  if (!token.value) {
    router.push('/auth/login')
    return
  }

  try {
    const id = route.params.id
    const res = await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${id}`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    publication.value = res
  } catch (err) {
    console.error(err)
    router.push('/')
  }
})

// Função para download do PDF
async function downloadPdf() {
  try {
    const id = route.params.id
    const pdfBlob = await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${id}/download`, {
      headers: { Authorization: `Bearer ${token.value}` },
      responseType: 'blob'
    })

    const url = URL.createObjectURL(pdfBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${publication.value.titulo}.pdf`
    link.click()
    URL.revokeObjectURL(url)
  } catch (err) {
    console.error("Erro ao fazer download do PDF", err)
  }
}

// Vai para a página de edição (mesmo formulário do create)
function goToEdit() {
  router.push(`/publications/edit/${publication.value.id}`)
}

// Elimina a publicação com confirmação
async function deletePublication() {
  if (!confirm("Tem certeza que deseja eliminar esta publicação?")) return

  try {
    await $fetch(`http://localhost:8080/publicacoescientificas/api/publicacoes/${publication.value.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token.value}` }
    })
    alert("Publicação eliminada com sucesso!")
    router.push('/') // Redireciona após eliminar
  } catch (err) {
    console.error("Erro ao eliminar publicação", err)
    alert("Não foi possível eliminar a publicação.")
  }
}
</script>
