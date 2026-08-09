<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import axiosInstance from '@/axiosInstance'

const route = useRoute()
const router = useRouter()

// State
const householdCode = ref<string | null>(null)
const isLoading = ref(false)
const household = ref<any>(null)
const error = ref<string | null>(null)

// Fetch household details
const fetchHousehold = async () => {
	const code = route.params.code as string
	if (!code) {
		error.value = 'No household code provided'
		return
	}

	householdCode.value = code
	isLoading.value = true
	error.value = null

	try {
		const response = await axiosInstance.get(`family/${code}/`)
		household.value = response.data
	} catch (err) {
		error.value = 'Failed to load household details. Please try again.'
		console.error('Error fetching household:', err)
	} finally {
		isLoading.value = false
	}
}

// Go back to household list
const goBack = () => {
	router.push({name: 'households'})
}

onMounted(() => {
	fetchHousehold()
})
</script>

<template>
	<MainLayout>
		<div class="space-y-4">
			<!-- Header -->
			<div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
				<div class="flex items-center gap-3">
					<button
							@click="goBack"
							class="p-2 hover:bg-gray-100 rounded-lg transition-colors text-gray-600 hover:text-gray-800">
						<i class="bx bx-arrow-back text-xl"></i>
					</button>
					<div>
						<h2 class="text-2xl font-bold text-gray-800">Household Details</h2>
						<p v-if="household" class="text-sm text-gray-500">
							Family Code: {{ household.code }}
						</p>
					</div>
				</div>
			</div>

			<!-- Loading State -->
			<div v-if="isLoading" class="bg-white rounded-lg shadow-sm border border-emerald-100 p-8">
				<div class="flex items-center justify-center py-12">
					<div class="text-center">
						<i class="bx bx-loader-alt text-4xl text-emerald-500 animate-spin block mb-3"></i>
						<p class="text-gray-500">Loading household details...</p>
					</div>
				</div>
			</div>

			<!-- Error State -->
			<div v-else-if="error" class="bg-white rounded-lg shadow-sm border border-emerald-100 p-6">
				<div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
					<i class="bx bx-error-circle text-xl"></i>
					{{ error }}
				</div>
			</div>

			<!-- Content -->
			<div v-else-if="household" class="bg-white rounded-lg shadow-sm border border-emerald-100 p-6">
				<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1">Family Code</label>
						<input
								:value="household.code || '—'"
								type="text"
								class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 font-mono uppercase cursor-default"
								readonly
								disabled
						>
					</div>
					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1">Family Name</label>
						<input
								:value="household.name || '—'"
								type="text"
								class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
								readonly
								disabled
						>
					</div>
					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1">Block Number</label>
						<input
								:value="household.blockNumber || '—'"
								type="text"
								class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
								readonly
								disabled
						>
					</div>
					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1">Barangay</label>
						<input
								:value="household.barangay || '—'"
								type="text"
								class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
								readonly
								disabled>
					</div>
				</div>
			</div>
		</div>
	</MainLayout>
</template>