export default defineNuxtRouteMiddleware(async (to) => {
    const auth = useAuthStore()

    const publicPaths = ['/auth/login', '/auth/forgot-password']

    if (publicPaths.includes(to.path)) return

    // Garante que o token do localStorage é carregado
    if (!auth.ready) {
        await auth.init()
    }

    // Se não estiver autenticado, manda para login
    if (!auth.isAuthenticated) {
        return navigateTo('/auth/login')
    }
})
