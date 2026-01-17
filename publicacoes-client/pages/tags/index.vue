<template>
  <div>
    <h1>Gestão de Tags </h1>

    <div v-if="!authStore.isAuthenticated">
      <nuxt-link to="/auth/login">Faz Login para subscreveres tags!</nuxt-link>
    </div>

    <div v-if="error">
      <p style="color: red">Erro a carregar tags: {{ error }}</p>
    </div>

    <div v-else-if="tags && tags.length > 0">
      <table>
        <thead>
        <tr>
          <th>Nome</th>
          <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tag in tags" :key="tag.id">
          <td>{{ tag.name }}</td>
          <td>
            <button v-if="authStore.user?.role === 'Administrador' || authStore.user?.role === 'Responsavel'"
                    @click.prevent="deleteTag(tag.id)">
              Apagar
            </button>

            <button
                v-if="amISubscribed(tag)"
                @click="unsubscribe(tag.id, tag.name)"
                class="btn-unsub">
              Anular Subscrição
            </button>

            <button
                v-else
                @click="subscribe(tag.id, tag.name)"
                class="btn-sub">
              Subscrever
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-else>
      <p>Ainda não há tags criadas... )</p>
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
import { useAuthStore } from "~/stores/auth-store.js";

const config = useRuntimeConfig()
const authStore = useAuthStore()
const api = config.public.apiBase
const message = ref('')
const tagName = ref('')

const { data: tags, error, refresh } = await useFetch(`${api}/tags`, {
  headers: {
    Authorization: `Bearer ${authStore.token}`
  }
})

function amISubscribed(tag) {
  if (!authStore.user) return false;
  if (!tag.subscriberUsernames) return false;
  return tag.subscriberUsernames.includes(authStore.user.username);
}

async function createTag() {
  if (!authStore.isAuthenticated) return alert("Login necessário!");

  try {
    await $fetch(`${api}/tags`, {
      method: 'POST',
      body: { name: tagName.value },
      headers: { Authorization: `Bearer ${authStore.token}` }
    })

    message.value = 'Tag criada com sucesso!'
    tagName.value = ''
    refresh()
  } catch (e) {
    message.value = 'Erro: ' + e
  }
}

async function deleteTag(id) {
  if (!confirm("Tens a certeza?")) return;

  try {
    await $fetch(`${api}/tags/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
    message.value = 'Tag apagada!'
    refresh()
  } catch (e) {
    message.value = 'Erro: ' + e
  }
}

async function subscribe(tagId, tagName) {
  if (!authStore.isAuthenticated) return alert("Login primeiro!");

  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${authStore.token}` }
    })

    message.value = `Subscreveste a tag "${tagName}". `

    refresh()
  } catch (e) {
    message.value = "Erro: " + e
  }
}

async function unsubscribe(tagId, tagName) {
  if (!authStore.isAuthenticated) return;

  try {
    await $fetch(`${api}/tags/${tagId}/subscricao`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authStore.token}` }
    })

    message.value = `Deixaste de seguir a tag "${tagName}".`

    refresh()
  } catch (e) {
    message.value = "Erro: " + e
  }
}
</script>

<style>
table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
th { background-color: #f2f2f2; }
button { cursor: pointer; margin-left: 5px; border: none; border-radius: 5px; padding: 5px 10px; }

.btn-sub { background-color: pink; color: black; }
.btn-unsub { background-color: lightcoral; color: white; }

.mensagem { color: blue; font-weight: bold; margin-top: 10px;}
</style>