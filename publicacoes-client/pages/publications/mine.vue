<template>
  <div>
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-gray-800">Os Meus Uploads</h2>
      <p class="text-gray-500">Gere as tuas submissões e verifica o estado de visibilidade.</p>
    </div>

    <div v-if="loading" class="text-center py-20 text-gray-400">
      <p>A carregar...</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">

      <div v-for="p in myPublications" :key="p.id" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition group relative">

        <span class="absolute top-4 right-4 px-2 py-1 rounded text-xs font-bold uppercase tracking-wide"
              :class="p.visivel ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'">
          {{ p.visivel ? 'Público' : 'Oculto' }}
        </span>

        <h3 class="text-lg font-bold text-gray-800 mb-2 group-hover:text-blue-600 transition pr-16">
          <NuxtLink :to="`/publications/${p.id}`">{{ p.titulo }}</NuxtLink>
        </h3>

        <p class="text-sm text-gray-500 mb-4 line-clamp-2">{{ p.resumoCurto }}</p>

        <div class="flex items-center justify-between mt-auto pt-4 border-t border-gray-50">
          <span class="text-xs text-gray-400 font-semibold">{{ p.area }}</span>

          <div class="flex gap-2">
            <NuxtLink :to="`/publications/edit/${p.id}`" class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition">
              <i class="bi bi-pencil"></i>
            </NuxtLink>
            <NuxtLink :to="`/publications/${p.id}`" class="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition">
              <i class="bi bi-eye"></i>
            </NuxtLink>
          </div>
        </div>

      </div>

      <div v-if="myPublications.length === 0" class="col-span-full text-center py-10 bg-gray-50 rounded-xl border border-dashed border-gray-300">
        <i class="bi bi-folder-x text-4xl text-gray-300 mb-2 block"></i>
        <p class="text-gray-500">Ainda não submeteste nenhuma publicação.</p>
        <NuxtLink to="/publications/create" class="text-blue-600 font-bold hover:underline mt-2 inline-block">
          Começar agora
        </NuxtLink>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '~/stores/auth-store.js'
import { usePublicationStore } from '~/stores/publication-store'
import { storeToRefs } from 'pinia'

const authStore = useAuthStore()
const { token, user } = storeToRefs(authStore)
const publicationStore = usePublicationStore()
const myPublications = ref([])
const loading = ref(true)

onMounted(async () => {
  if (!token.value) return
  if (!user.value) await authStore.fetchUser()

  await publicationStore.fetchAll(token.value)

  myPublications.value = publicationStore.publications.filter(
      pub => pub.username === user.value.username
  )
  loading.value = false
})
</script>