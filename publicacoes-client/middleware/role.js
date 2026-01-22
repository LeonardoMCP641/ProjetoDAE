export default defineNuxtRouteMiddleware(async (to) => {
    const auth = useAuthStore()

    if (!auth.ready) {
        await auth.init()
    }


    const requiredRoles = to.meta.requiredRoles || []


    if (!requiredRoles.length) return

    if (!auth.isAuthenticated) {
        return navigateTo('/auth/login')
    }
    if (!auth.user) {
        await auth.fetchUser()
    }

    const role = auth.user?.role
    if (!role || !requiredRoles.includes(role)) {
        return navigateTo('/')
    }
})
