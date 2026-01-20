<template>
  <div class="container mt-4">
    <h3 class="mb-4">Editar Publicação</h3>

    <!-- Aviso para não donos -->
    <div v-if="!isOwner" class="alert alert-warning">
      Só podes alterar a visibilidade desta publicação.
    </div>

    <form @submit.prevent="submit">
      <input
        v-model="titulo"
        class="form-control mb-2"
        placeholder="Título"
        :disabled="!isOwner"
        required
      />

      <input
        v-model="autores"
        class="form-control mb-2"
        placeholder="Autores (separados por vírgula)"
        :disabled="!isOwner"
      />

      <input
        v-model="area"
        class="form-control mb-2"
        placeholder="Área"
        :disabled="!isOwner"
      />

      <input
        v-model="tipo"
        class="form-control mb-2"
        placeholder="Tipo"
        :disabled="!isOwner"
      />

      <textarea
        v-model="resumoCurto"
        class="form-control mb-2"
        placeholder="Resumo Curto"
        :disabled="!isOwner"
      />

      <div class="form-check mb-3">
        <input
          type="checkbox"
          class="form-check-input"
          id="visivel"
          v-model="visivel"
        />
        <label class="form-check-label" for="visivel"> Visível </label>
      </div>

      <input
        type="file"
        class="form-control mb-3"
        @change="onFileChange"
        :disabled="!isOwner"
      />

      <button class="btn btn-success">Atualizar</button>
    </form>

    <div v-if="message" class="alert alert-info mt-3">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "#imports";
import { useAuthStore } from "~/stores/auth-store";
import { usePublicationStore } from "~/stores/publication-store";
import { storeToRefs } from "pinia";

const route = useRoute();
const router = useRouter();

const authStore = useAuthStore();
const { token, user } = storeToRefs(authStore);

const publicationStore = usePublicationStore();

const titulo = ref("");
const autores = ref("");
const area = ref("");
const tipo = ref("");
const resumoCurto = ref("");
const visivel = ref(true);
const file = ref(null);
const message = ref("");
const isOwner = ref(false);

onMounted(async () => {
  if (!token.value) {
    router.push("/auth/login");
    return;
  }



  try {
    if (!user.value) {
      await authStore.fetchUser();
    }

    const id = route.params.id;
    const res = await publicationStore.fetchById(id, token.value);

    titulo.value = res.titulo;
    autores.value = res.autores.join(", ");
    area.value = res.area;
    tipo.value = res.tipo;
    resumoCurto.value = res.resumoCurto;
    visivel.value = res.visivel ?? true;

    // Verifica se é dono
    isOwner.value = res.username === user.value.username;
  } catch (err) {
    console.error(err);
    router.push("/");
  }
});

async function deletePublication() {
  if (!confirm("Tens a certeza que queres apagar esta publicação?")) return;

  try {
    const id = route.params.id;
    await publicationStore.delete(id, token.value);
    message.value = "Publicação apagada com sucesso!";
    setTimeout(() => router.push("/"), 1500);
  } catch (err) {
    console.error(err);
    message.value = "Erro ao apagar publicação.";
  }
}

function onFileChange(e) {
  file.value = e.target.files[0];
}

async function submit() {
  try {
    const id = route.params.id;

    const payload = isOwner.value
      ? {
          titulo: titulo.value,
          autores: autores.value.split(",").map((a) => a.trim()),
          area: area.value,
          tipo: tipo.value,
          resumoCurto: resumoCurto.value,
          visivel: visivel.value,
        }
      : {
          visivel: visivel.value,
        };

    await publicationStore.update(id, payload, token.value);

    if (file.value && isOwner.value) {
      await publicationStore.uploadFile(id, file.value, token.value);
    }

    message.value = "Publicação atualizada com sucesso!";
    setTimeout(() => router.push("/"), 1500);
  } catch (err) {
    console.error(err);
    message.value = "Erro ao atualizar publicação.";
  }
}
</script>
