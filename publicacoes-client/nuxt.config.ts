// nuxt.config.ts
export default defineNuxtConfig({
  // Adiciona a data para o aviso amarelo desaparecer
  compatibilityDate: '2026-01-21',

  devtools: { enabled: true },

  // 1. Módulos (Tailwind + Pinia)
  modules: [
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss'
  ],

  // 2. CSS Global
  // Aqui carregamos SÓ os ícones.
  // Removemos o Bootstrap CSS completo para não estragar o Tailwind.
  css: [
    'bootstrap-icons/font/bootstrap-icons.css'
  ],

  // 3. Configuração da API (Backend)
  runtimeConfig: {
    public: {
      apiBase: 'http://localhost:8080/publicacoescientificas/api'
    }
  }
})