<script setup lang="ts">
import {useAuthStore} from '@/stores/auth.ts'
import MainLayout from '@/layouts/MainLayout.vue'
import {onMounted} from 'vue'

const authStore = useAuthStore()
onMounted(() => {
	console.log(`Username: ${authStore.account?.username}`)
})
</script>

<template>
	<MainLayout>
		<span class="text-2xl text-emerald-600">Hello there! Ralph Maron Eda is here!</span>
	</MainLayout>
</template>