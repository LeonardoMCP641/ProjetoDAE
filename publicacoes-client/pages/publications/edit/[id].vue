<template>
  <div class="container mt-4">
    <h3 class="mb-4">Editar Publicação</h3>

    <form @submit.prevent="submit">
      <input v-model="titulo" class="form-control mb-2" placeholder="Título" required />

      <input
        v-model="autores"
        class="form-control mb-2"
        placeholder="Autores (separados por vírgula)"
      />

      <input v-model="area" class="form-control mb-2" placeholder="Área" />
      <input v-model="tipo" class="form-control mb-2" placeholder="Tipo" />

      <textarea
        v-model="resumoCurto"
        class="form-control mb-2"
        placeholder="Resumo Curto"
      />

      <div class="form-check mb-3">
        <input
          type="checkbox"
          class="form-check-input"
          id="visivel"
          v-model="visivel"
        />
        <label class="form-check-label" for="visivel">
          Visível
        </label>
      </div>

      <input type="file" class="form-control mb-3" @change="onFileChange" />

      <button class="btn btn-success">Atualizar</button>
    </form>

    <div v-if="message" class="alert alert-info mt-3">{{ message }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth-store'
import { storeToRefs } from 'pinia'
import { usePublicationStore } from '~/stores/publication-store'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { token } = storeToRefs(authStore)
const publicationStore = usePublicationStore()

const titulo = ref('')
const autores = ref('')
const area = ref('')
const tipo = ref('')
const resumoCurto = ref('')
const visivel = ref(true)
const file = ref(null)
const message = ref('')

// Carrega os dados da publicação
onMounted(async () => {
  if (!token.value) {
    router.push('/auth/login')
    return
  }

  try {
    const id = route.params.id
    const res = await publicationStore.fetchById(id, token.value)

    titulo.value = res.titulo
    autores.value = res.autores.join(', ')
    area.value = res.area
    tipo.value = res.tipo
    resumoCurto.value = res.resumoCurto
    visivel.value = res.visivel ?? true
  } catch (err) {
    console.error(err)
    router.push('/')
  }
})

function onFileChange(e) {
  file.value = e.target.files[0]
}

// Submete alterações
async function submit() {
  try {
    const id = route.params.id

    await publicationStore.update(
      id,
      {
        titulo: titulo.value,
        autores: autores.value.split(',').map(a => a.trim()),
        area: area.value,
        tipo: tipo.value,
        resumoCurto: resumoCurto.value,
        visivel: visivel.value
      },
      token.value
    )

    if (file.value) {
      await publicationStore.uploadFile(id, file.value, token.value)
    }

    message.value = 'Publicação atualizada com sucesso!'
    setTimeout(() => {
      router.push(`/`)
    }, 1500)
  } catch (err) {
    console.error(err)
    message.value = 'Erro ao atualizar publicação.'
  }
}
</script>
