// ~/stores/publication-store.js
import { defineStore } from "pinia";

export const usePublicationStore = defineStore("publication", {
  state: () => ({
    publications: [],
  }),

  actions: {
      async fetchAll(token, params = {}) { // Adicionado params
          const config = useRuntimeConfig();
          // Converter objeto params para query string
          const query = new URLSearchParams(params).toString();

          this.publications = await $fetch(`${config.public.apiBase}/publicacoes?${query}`, {
              headers: {
                  Authorization: `Bearer ${token}`,
              },
          });
      },

    async create(data, token) {
      const config = useRuntimeConfig();
      return await $fetch(`${config.public.apiBase}/publicacoes`, {
        method: "POST",
        body: data,
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    },

    async update(id, data, token) {
      const config = useRuntimeConfig();
      return await $fetch(`${config.public.apiBase}/publicacoes/${id}`, {
        method: "PUT", // ou "PATCH" dependendo da tua API
        body: data,
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    },

    async uploadFile(publicationId, file, token) {
      const config = useRuntimeConfig();
      const formData = new FormData();
      formData.append("file", file);

      return await $fetch(
        `${config.public.apiBase}/publicacoes/${publicationId}/upload`,
        {
          method: "POST",
          body: formData,
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
    },

    async download(publicationId, token) {
      const config = useRuntimeConfig();
      return await $fetch(
        `${config.public.apiBase}/publicacoes/${publicationId}/download`,
        {
          responseType: "blob",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
    },
    async fetchById(id, token) {
      const config = useRuntimeConfig();
      return await $fetch(`${config.public.apiBase}/publicacoes/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
    },
  },

  async delete(id, token) {
    return $fetch(`/publicacoes/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });
  },
});
