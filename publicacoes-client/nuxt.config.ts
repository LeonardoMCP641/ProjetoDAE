export default defineNuxtConfig({
  devtools: { enabled: true },

  // 1. Módulos
  modules: ['@pinia/nuxt','@nuxtjs/tailwindcss'],

  // 2. CSS Framework (Bootstrap 5 via CDN)
  app: {
    head: {
      link: [
        { rel: 'stylesheet', href: 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' }
      ],
      script: [
        { src: 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js', tagPosition: 'bodyClose' }
      ]
    }
  },

  runtimeConfig: {
    public: {

      apiBase: 'http://localhost:8080/publicacoescientificas/api'
    }
  }
})