<script setup lang="ts">
import {ref} from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const sidebarRef = ref<InstanceType<typeof Sidebar>>()

const toggleSidebar = () => {
	sidebarRef.value?.toggleMobile()
}
</script>

<template>
	<div class="flex h-screen w-screen overflow-hidden bg-linear-to-br from-emerald-50 via-white to-green-50 relative">
		<Sidebar ref="sidebarRef" />
		<div class="flex-1 flex flex-col overflow-hidden">
			<Header @toggle-sidebar="toggleSidebar" />
			<main class="flex-1 overflow-y-auto p-4 md:p-6">
				<slot />
			</main>
		</div>
	</div>
</template>