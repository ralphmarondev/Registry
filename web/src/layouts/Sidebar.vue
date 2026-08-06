<script setup lang="ts">
import {onMounted, onUnmounted, ref, watch} from 'vue'

const isMobileOpen = ref(false)

const menuItems = [
	{label: 'Dashboard', path: '/dashboard', icon: 'bx-grid-alt'},
	{label: 'Households', path: '/households', icon: 'bx-home'},
	{label: 'Residents', path: '/residents', icon: 'bx-user'},
	{label: 'Reports', path: '/reports', icon: 'bx-bar-chart-alt-2'},
	{label: 'Accounts', path: '/accounts', icon: 'bx-user-circle'}
]

const toggleMobile = () => {
	isMobileOpen.value = !isMobileOpen.value
}

const closeMobile = () => {
	isMobileOpen.value = false
}

const handleResize = () => {
	if (window.innerWidth >= 768) {
		closeMobile()
	}
}

const handleEscape = (event: KeyboardEvent) => {
	if (event.key === 'Escape' && isMobileOpen.value) {
		closeMobile()
	}
}

const toggleBodyScroll = (shouldPrevent: boolean) => {
	if (shouldPrevent) {
		document.body.style.overflow = 'hidden'
	} else {
		document.body.style.overflow = ''
	}
}

watch(isMobileOpen, (newVal) => {
	toggleBodyScroll(newVal)
})

defineExpose({
	toggleMobile,
	closeMobile
})

onMounted(() => {
	window.addEventListener('resize', handleResize)
	document.addEventListener('keydown', handleEscape)
})

onUnmounted(() => {
	window.removeEventListener('resize', handleResize)
	document.removeEventListener('keydown', handleEscape)
	toggleBodyScroll(false)
})
</script>

<template>
	<!-- Mobile overlay-->
	<div v-if="isMobileOpen"
	     class="fixed inset-0 bg-black/20 z-40 md:hidden transition-opacity duration-300"
	     @click="closeMobile" />

	<aside
			class="bg-white border-r border-emerald-100 flex flex-col shrink-0 transition-transform duration-300 z-50 w-64"
			:class="[isMobileOpen ? 'fixed left-0 top-0 h-full shadow-2xl translate-x-0' : 'hidden md:flex -translate-x-full md:translate-x-0']">
		<button
				v-if="isMobileOpen"
				@click="closeMobile"
				class="absolute top-4 right-4 md:hidden p-2 rounded-lg hover:bg-emerald-50 transition-colors text-gray-600">
			<i class="bx bx-x text-2xl"></i>
		</button>
		<div class="h-16 flex items-center px-4 border-b border-emerald-100">
			<div class="flex items-center gap-3">
				<div class="w-8 h-8 bg-linear-to-br from-emerald-600 to-green-500 rounded-lg flex items-center justify-center text-white font-bold text-sm shrink-0">
					E
				</div>
				<span class="text-lg font-semibold text-gray-800 whitespace-nowrap">
					EProfile
				</span>
			</div>
		</div>

		<!-- Navigation -->
		<nav class="flex-1 py-4 overflow-y-auto">
			<ul class="space-y-1 px-3">
				<li v-for="item in menuItems" :key="item.path">
					<router-link
							:to="item.path"
							class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-gray-600 hover:bg-emerald-50 hover:text-emerald-600 transition-all duration-200 group relative"
							active-class="bg-emerald-50 text-emerald-600"
							exact-active-class="bg-emerald-100 text-emerald-700"
							@click="closeMobile">
						<i :class="['bx', item.icon, 'text-xl']"></i>
						<span class="text-sm font-medium whitespace-nowrap">
							{{ item.label }}
						</span>
					</router-link>
				</li>
			</ul>
		</nav>
		<div class="border-t border-emerald-100"></div>
	</aside>
</template>