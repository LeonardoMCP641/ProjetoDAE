import { defineStore } from "pinia";

export const useAuthStore = defineStore("authStore", () => {
    const token = ref(null);
    const user = ref(null);
    const ready = ref(false);

    const config = useRuntimeConfig();
    const api = config.public.apiBase;

    async function login(username, password) {
        const data = await $fetch(`${api}/auth/login`, {
            method: "POST",
            body: { username, password },
        });

        token.value = data.token;

        if (process.client) {
            localStorage.setItem("token", token.value);
        }

        await fetchUser();
        return navigateTo("/");
    }

    async function fetchUser() {
        if (!token.value) return;
        try {
            const userData = await $fetch(`${api}/auth/user`, {
                headers: { Authorization: `Bearer ${token.value}` },
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
            localStorage.removeItem("token");
        }
        navigateTo("/auth/login");
    }

    const isAuthenticated = computed(() => !!token.value);
    const role = computed(() => user.value?.role ?? null);

    function hasRole(roles) {
        return !!role.value && roles.includes(role.value);
    }

    async function init() {
        if (process.client) {
            const storedToken = localStorage.getItem("token");
            if (storedToken) {
                token.value = storedToken;
                await fetchUser();
            }
        }
        ready.value = true;
    }

    return { token, user, ready, login, logout, isAuthenticated, init, fetchUser, role, hasRole };
});
