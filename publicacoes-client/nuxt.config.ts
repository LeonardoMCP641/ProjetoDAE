// nuxt.config.ts
export default defineNuxtConfig({
  // Remove o aviso amarelo
  compatibilityDate: '2026-01-21',

  devtools: { enabled: true },

  // Módulos
  modules: [
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss'
  ],

  // CSS global (nenhum Bootstrap CSS aqui)
  css: [],

  // Head global (Bootstrap Icons)
  app: {
    head: {
      link: [
        {
          rel: 'stylesheet',
          href: 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css'
        }
      ]
    }
  },

  // Configuração do backend
  runtimeConfig: {
    public: {
      apiBase: 'http://localhost:8080/publicacoescientificas/api'
    }
  }
})
