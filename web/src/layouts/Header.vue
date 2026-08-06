<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const emit = defineEmits<{
	(e: 'toggle-sidebar'): void
}>()

const isDropdownOpen = ref(false)

const displayName = computed(() => authStore.account?.username ?? 'Guest User')
const userInitials = computed(() => {
	const name = displayName.value
	if (name === 'Guest User') return 'GU'
	return name
			.split(' ')
			.map(word => word[0])
			.join('')
			.toUpperCase()
			.slice(0, 2)
})
const userRole = computed(() => authStore.account?.role.name ?? '')

const toggleDropdown = () => {
	isDropdownOpen.value = !isDropdownOpen.value
}

const closeDropdown = () => {
	isDropdownOpen.value = false
}

// Close dropdown when clicking outside
const handleClickOutside = (event: MouseEvent) => {
	const target = event.target as HTMLElement
	if (!target.closest('.relative')) {
		closeDropdown()
	}
}

const handleLogout = async () => {
	authStore.logout()
	await router.push({name: 'login'})
}

onMounted(() => {
	document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
	document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
	<header class="h-16 bg-white/80 backdrop-blur-md border-b border-emerald-100/50 flex items-center justify-between px-4 md:px-6 shrink-0">
		<div class="flex items-center gap-3">
			<!-- Mobile menu toggle -->
			<button
					@click="emit('toggle-sidebar')"
					class="md:hidden p-2 rounded-lg hover:bg-emerald-50 transition-colors">
				<i class="bx bx-menu text-2xl text-gray-600"></i>
			</button>
			<h1 class="text-lg font-semibold text-gray-800">
				<!-- Page title can go here -->
			</h1>
		</div>

		<!-- Right Section -->
		<div class="flex items-center gap-3 md:gap-4">
			<!-- Notification Bell -->
			<button class="relative p-2 text-gray-600 hover:text-emerald-600 transition-colors rounded-full hover:bg-emerald-50">
				<i class="bx bx-bell text-xl"></i>
				<span class="absolute top-1.5 right-1.5 w-2 h-2 bg-emerald-500 rounded-full"></span>
			</button>

			<!-- User Menu -->
			<div class="relative" @click.stop="toggleDropdown">
				<div class="flex items-center gap-2 md:gap-3 p-1.5 rounded-lg hover:bg-emerald-50 transition-colors cursor-pointer">
					<!-- Avatar -->
					<span class="w-8 h-8 bg-linear-to-br from-emerald-600 to-green-500 rounded-full flex items-center justify-center text-white font-semibold text-sm">
						{{ userInitials }}
					</span>

					<!-- User Info -->
					<div class="hidden sm:block text-left">
						<p class="text-sm font-medium text-gray-800">{{ displayName }}</p>
						<p class="text-xs text-gray-500">{{ userRole }}</p>
					</div>

					<!-- Chevron -->
					<i class="bx bx-chevron-down text-gray-400 transition-transform"
					   :class="{ 'rotate-180': isDropdownOpen }">
					</i>
				</div>

				<!-- Dropdown -->
				<div
						v-if="isDropdownOpen"
						class="absolute right-0 mt-2 w-48 bg-white border border-emerald-100 rounded-lg shadow-lg py-1 z-50">
					<div class="px-4 py-2 border-b border-emerald-100 sm:hidden">
						<p class="text-sm font-medium text-gray-800">{{ displayName }}</p>
						<p class="text-xs text-gray-500">{{ userRole }}</p>
					</div>
					<router-link
							to="/profile"
							class="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
							@click="closeDropdown">
						<i class="bx bx-user text-lg"></i>
						Profile
					</router-link>
					<router-link
							to="/settings"
							class="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition"
							@click="closeDropdown">
						<i class="bx bx-cog text-lg"></i>
						Settings
					</router-link>
					<hr class="my-1 border-emerald-100">
					<button
							@click="handleLogout"
							class="flex items-center gap-2 w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition">
						<i class="bx bx-log-out text-lg"></i>
						Logout
					</button>
				</div>
			</div>
		</div>
	</header>
</template>