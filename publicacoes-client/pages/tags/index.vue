<template>
  <div>
    <h1>Gestão de Tags </h1>

    <div v-if="authStore.isAuthenticated">
      <p>Olá! Estás logada. Podes subscrever tags.</p>
    </div>
    <div v-else>
      <nuxt-link to="/auth/login">Faz Login para subscreveres tags!</nuxt-link>
    </div>

    <div v-if="tags && tags.length > 0">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Nome</th>
          <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tag in tags" :key="tag.id">
          <td>{{ tag.id }}</td>
          <td>{{ tag.name }}</td>
          <td>
            <button @click.prevent="deleteTag(tag.id)">Apagar</button>

            <button @click="subscribe(tag.id)" class="btn-sub">Subscrever</button>

            <button @click="unsubscribe(tag.id)">Anular Subscrição</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-else>
      <p>Ainda não há tags criadas...</p>
    </div>

    <hr>

    <h2>Criar Nova Tag</h2>
    <form @submit.prevent="createTag">
      <input v-model="tagName" placeholder="Nome da Tag (Ex: Java)" required />
      <button type="submit">Criar</button>
    </form>

    <p v-if="message" class="mensagem">{{ message }}</p>
  </div>
</template>

<script setup>
const config = useRuntimeConfig()
const authStore = useAuthStore()
const api = config.public.apiBase
const message = ref('')
const tagName = ref('')

const { data: tags, error, refresh } = await useFetch(`${api}/tags`)

async function createTag() {
  try {
    await $fetch(`${api}/tags`, {
      method: 'POST',
      body: { name: tagName.value }
    })

    message.value = 'Tag criada com sucesso! '
    tagName.value = ''
    refresh()
  } catch (e) {
    message.value = 'Erro ao criar tag: ' + e
  }
}

async function deleteTag(id) {
  if (!confirm("Tens a certeza que queres apagar?")) return;

  try {
    await $fetch(`${api}/tags/${id}`, {
      method: 'DELETE'
    })
    message.value = 'Tag apagada!'
    refresh()
  } catch (e) {
    message.value = 'Erro ao apagar: ' + e
  }
}

async function subscribe(tagId) {
  if (!authStore.isAuthenticated) {
    return alert("Tens de fazer login primeiro!")
  }

  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    message.value = `Subscreveste a tag ${tagId}! 🎉`
  } catch (e) {
    message.value = "Erro ao subscrever: " + e
  }
}

async function unsubscribe(tagId) {
  if (!authStore.isAuthenticated) return;

  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    message.value = `Deixaste de seguir a tag ${tagId}.`
  } catch (e) {
    message.value = "Erro: " + e
  }
}

</script>

<style>
table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
th { background-color: #f2f2f2; }
button { cursor: pointer; margin-left: 5px; border: none; border-radius: 5px; }
.btn-sub { background-color: pink; color: black; border: none; margin-left: 5px;}
.mensagem { color: blue; font-weight: bold; }
</style>
