<template>
  <div class="container">
    <h3 class="mb-4">Submeter Nova Publicação</h3>

    <form @submit.prevent="submit">
      <input v-model="titulo" class="form-control mb-2" placeholder="Título" required />

      <input
        v-model="autores"
        class="form-control mb-2"
        placeholder="Autores (separados por vírgula)"
      />

      <input v-model="area" class="form-control mb-2" placeholder="Área" />
      <input v-model="tipo" class="form-control mb-2" placeholder="Tipo" />

      

      <div class="form-check mb-3">
        <input type="checkbox" class="form-check-input" id="visivel" v-model="visivel" />
        <label class="form-check-label" for="visivel">Visível</label>
      </div>

      <input type="file" class="form-control mb-3" @change="onFileChange" />

      <button class="btn btn-success">Submeter</button>
    </form>

    <div v-if="message" class="alert alert-info mt-3">{{ message }}</div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '~/stores/auth-store'
import { usePublicationStore } from '~/stores/publication-store'
import { useRouter } from 'vue-router' 

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

function onFileChange(e) {
  file.value = e.target.files[0]
}

async function submit() {
  try {
    // Cria publicação
    const publication = await publicationStore.create(
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

    // Upload de arquivo opcional
    if (file.value) {
      await publicationStore.uploadFile(publication.id, file.value, token.value)
    }

    message.value = 'Publicação submetida com sucesso!'

    setTimeout(() => {
      router.push('/')
    }, 1500)

  } catch (error) {
    console.error(error)
    message.value = 'Erro ao submeter publicação.'
  }
}
</script>
