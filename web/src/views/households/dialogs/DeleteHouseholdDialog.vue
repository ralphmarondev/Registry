<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import axiosInstance from '@/axiosInstance'

// Props
interface Props {
	visible: boolean
	householdId: number | null
	householdName: string
	householdCode: string
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
	(e: 'update:visible', value: boolean): void
	(e: 'success'): void
}>()

// State
const household = ref<any>(null)
const isLoading = ref(false)
const fetchError = ref<string | null>(null)
const isDeleting = ref(false)
const error = ref<string | null>(null)
const confirmationCode = ref('')
const showConfirmationError = ref(false)

// Computed dialog visibility
const dialogVisible = computed({
	get: () => props.visible,
	set: (value) => emit('update:visible', value)
})

// Check if confirmation code matches
const isConfirmationValid = computed(() => {
	return confirmationCode.value === props.householdCode
})

// Fetch household data
const fetchHousehold = async () => {
	if (!props.householdId) return

	isLoading.value = true
	fetchError.value = null
	try {
		const response = await axiosInstance.get(`family/${props.householdCode}/`)
		household.value = response.data
	} catch (err) {
		fetchError.value = 'Failed to load household details. Please try again.'
		console.error('Error fetching household:', err)
	} finally {
		isLoading.value = false
	}
}

// Delete handler
const handleDelete = async () => {
	if (!isConfirmationValid.value) {
		showConfirmationError.value = true
		return
	}

	if (!props.householdId) return

	isDeleting.value = true
	error.value = null
	showConfirmationError.value = false

	try {
		await axiosInstance.delete(`family/${props.householdId}/`)
		emit('success')
		dialogVisible.value = false
	} catch (err: any) {
		error.value = err.response?.data?.message || 'Failed to delete household. Please try again.'
		console.error('Error deleting household:', err)
	} finally {
		isDeleting.value = false
	}
}

// Close handler
const handleClose = () => {
	dialogVisible.value = false
	error.value = null
	fetchError.value = null
	confirmationCode.value = ''
	showConfirmationError.value = false
	household.value = null
}

// Watch for dialog open to fetch data
watch(() => props.visible, (newVal) => {
	if (newVal && props.householdId) {
		fetchHousehold()
	} else if (!newVal) {
		household.value = null
		fetchError.value = null
		error.value = null
		confirmationCode.value = ''
		showConfirmationError.value = false
	}
})

// Watch for householdId changes
watch(() => props.householdId, (newVal) => {
	if (props.visible && newVal) {
		fetchHousehold()
	}
})
</script>

<template>
	<!-- Dialog Overlay -->
	<div
			v-if="dialogVisible"
			class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
			@click.self="handleClose">

		<!-- Dialog Content -->
		<div class="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
			<!-- Header -->
			<div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
				<h3 class="text-xl font-bold text-gray-800">Delete Household</h3>
				<button
						@click="handleClose"
						class="p-1.5 hover:bg-gray-100 rounded-lg transition-colors text-gray-500 hover:text-gray-700">
					<i class="bx bx-x text-2xl"></i>
				</button>
			</div>

			<!-- Body -->
			<div class="flex-1 overflow-y-auto px-6 py-4">
				<!-- Loading State -->
				<div v-if="isLoading" class="flex items-center justify-center py-12">
					<div class="text-center">
						<i class="bx bx-loader-alt text-4xl text-emerald-500 animate-spin block mb-3"></i>
						<p class="text-gray-500">Loading household details...</p>
					</div>
				</div>

				<!-- Error State -->
				<div v-else-if="fetchError"
				     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2 mb-4">
					<i class="bx bx-error-circle text-xl"></i>
					{{ fetchError }}
				</div>

				<!-- Content -->
				<template v-else-if="household">
					<!-- Delete Error Alert -->
					<div v-if="error"
					     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2 mb-4">
						<i class="bx bx-error-circle text-xl"></i>
						{{ error }}
					</div>

					<!-- Warning Message -->
					<div class="flex items-start gap-3 bg-amber-50 border border-amber-200 rounded-lg p-4 mb-6">
						<i class="bx bx-error text-xl text-amber-600 mt-0.5"></i>
						<div class="text-sm text-amber-800">
							<p class="font-medium mb-1">Warning: This action cannot be undone!</p>
							<p>Deleting this household will permanently remove all associated data from the system.</p>
						</div>
					</div>

					<!-- Household Information Section -->
					<div class="mb-6">
						<h4 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
							<i class="bx bx-info-circle mr-1"></i>
							Household Information
						</h4>
						<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
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
								<label class="block text-sm font-medium text-gray-700 mb-1">Household Number</label>
								<input
										:value="household.householdNumber || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled
								>
							</div>
						</div>
					</div>

					<!-- Address Information Section -->
					<div class="mb-6">
						<h4 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
							<i class="bx bx-map mr-1"></i>
							Address Information
						</h4>
						<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Barangay</label>
								<input
										:value="household.barangay || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">City</label>
								<input
										:value="household.city || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Province</label>
								<input
										:value="household.province || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled
								>
							</div>
						</div>
					</div>

					<!-- Confirmation Section -->
					<div>
						<h4 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
							<i class="bx bx-shield mr-1"></i>
							Confirmation Required
						</h4>
						<div class="bg-gray-50 rounded-lg p-4">
							<p class="text-sm text-gray-600 mb-3">
								To confirm deletion, please type the family code
								<span class="font-mono font-bold text-gray-800">{{ household.code }}</span>
								in the field below.
							</p>
							<div class="relative">
								<input
										v-model="confirmationCode"
										type="text"
										placeholder="Type the family code to confirm"
										class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-red-500 focus:ring-2 focus:ring-red-200 transition-colors"
										:class="showConfirmationError && !isConfirmationValid ? 'border-red-500' : 'border-gray-300'"
										@input="showConfirmationError = false"
								>
								<i v-if="isConfirmationValid"
								   class="bx bx-check-circle absolute right-3 top-1/2 -translate-y-1/2 text-green-500 text-lg"></i>
								<i v-else-if="confirmationCode && !isConfirmationValid"
								   class="bx bx-x-circle absolute right-3 top-1/2 -translate-y-1/2 text-red-500 text-lg"></i>
							</div>
							<p v-if="showConfirmationError && !isConfirmationValid" class="mt-1 text-xs text-red-500">
								<i class="bx bx-error-circle mr-1"></i>
								Please enter the correct family code to confirm deletion
							</p>
							<p v-else-if="isConfirmationValid" class="mt-1 text-xs text-green-600">
								<i class="bx bx-check-circle mr-1"></i>
								Code verified. You can proceed with deletion.
							</p>
						</div>
					</div>
				</template>
			</div>

			<!-- Footer -->
			<div class="flex items-center justify-end gap-3 px-6 py-4 border-t border-gray-200">
				<button
						@click="handleClose"
						:disabled="isDeleting"
						class="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors disabled:opacity-50">
					Cancel
				</button>
				<button
						@click="handleDelete"
						:disabled="isDeleting || !isConfirmationValid || isLoading"
						class="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2">
					<i v-if="isDeleting" class="bx bx-loader-alt animate-spin"></i>
					{{ isDeleting ? 'Deleting...' : 'Delete Household' }}
				</button>
			</div>
		</div>
	</div>
</template>

<style scoped>
/* Animation for dialog */
.fixed {
	animation: fadeIn 0.2s ease-out;
}

.bg-white {
	animation: slideUp 0.3s ease-out;
}

@keyframes fadeIn {
	from {
		opacity: 0;
	}
	to {
		opacity: 1;
	}
}

@keyframes slideUp {
	from {
		transform: translateY(20px) scale(0.95);
		opacity: 0;
	}
	to {
		transform: translateY(0) scale(1);
		opacity: 1;
	}
}
</style>