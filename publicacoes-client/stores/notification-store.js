import { defineStore } from "pinia";

export const useNotificationStore = defineStore("notification", {
    state: () => ({ notifications: [] }),
    getters: {
        unreadCount: (state) => state.notifications.length,
    },
    actions: {
        async fetchNotifications(token) {
            const config = useRuntimeConfig();
            try {
                const data = await $fetch(`${config.public.apiBase}/notifications`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                this.notifications = data;
            } catch (e) {
                console.error("Erro notifications", e);
            }
        },
    },
});