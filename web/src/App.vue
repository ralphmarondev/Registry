<script setup lang="ts">
import {useAuthStore} from '@/stores/auth.ts'
import {onMounted} from 'vue'

const authStore = useAuthStore()

onMounted(async () => {
	authStore.loadTokens()

	if (authStore.isAuthenticated) {
		try {
			await authStore.loadAccount()
			console.log('')
		} catch {
			authStore.logout()
		}
	}
})
</script>

<template>
	<RouterView />
</template>