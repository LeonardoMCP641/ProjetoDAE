import { defineStore } from "pinia";

export const useAuthStore = defineStore("authStore", () => {

    const token = ref(null);
    const user = ref(null);
    const config = useRuntimeConfig();
    const api = config.public.apiBase;

    // Actions tornam-se funções normais
    async function login(username, password) {
        try {
            const data = await $fetch(`${api}/auth/login`, {
                method: 'POST',
                body: { username, password }
            });

            token.value = data.token;

            if (process.client) {
                localStorage.setItem('token', token.value);
            }

            await fetchUser();
            return navigateTo('/');
        } catch (error) {
            throw error;
        }
    }

    async function fetchUser() {
        if (!token.value) return;
        try {
            const userData = await $fetch(`${api}/auth/user`, {
                headers: { Authorization: `Bearer ${token.value}` }
            });
            user.value = userData;
        } catch (e) {
            logout();
        }
    }

    function logout() {
        token.value = null;
        user.value = null;
        if (process.client) {
            localStorage.removeItem('token');
        }
        navigateTo('/auth/login');
    }

    // getters (computeds)
    const isAuthenticated = computed(() => !!token.value);


    function init() {
        if (process.client) {
            const storedToken = localStorage.getItem('token');
            if (storedToken) {
                token.value = storedToken;
                fetchUser();
            }
        }
    }


    return { token, user, login, logout, isAuthenticated, init };
});