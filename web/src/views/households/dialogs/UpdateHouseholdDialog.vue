<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useBarangayStore } from '@/stores/barangay'
import axiosInstance from '@/axiosInstance'

// Props
interface Props {
	visible: boolean
	householdId: number | null
	householdCode: string | null
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
	(e: 'update:visible', value: boolean): void
	(e: 'success'): void
}>()

const barangayStore = useBarangayStore()

// Form state
const formData = ref({
	code: '',
	name: '',
	blockNumber: '',
	barangay: '',
	city: '',
	province: '',
	landline: '',
	householdNumber: '',
	householdType: 'NUCLEAR',
	housingOwnership: 'OWNED',
	registrationStatus: 'APPROVED'
})

// Validation errors
const errors = ref<Record<string, string>>({})
const isSubmitting = ref(false)
const isLoading = ref(false)
const submitError = ref<string | null>(null)
const fetchError = ref<string | null>(null)

// Household type options
const householdTypeOptions = [
	{ value: 'NUCLEAR', label: 'Nuclear' },
	{ value: 'EXTENDED', label: 'Extended' },
	{ value: 'JOINT', label: 'Joint' },
	{ value: 'SINGLE_PARENT', label: 'Single Parent' }
]

// Housing ownership options
const housingOwnershipOptions = [
	{ value: 'OWNED', label: 'Owned' },
	{ value: 'RENTED', label: 'Rented' },
	{ value: 'LEASED', label: 'Leased' },
	{ value: 'SHARED', label: 'Shared' },
	{ value: 'OTHER', label: 'Other' }
]

// Registration status options
const registrationStatusOptions = [
	{ value: 'APPROVED', label: 'Approved' },
	{ value: 'PENDING', label: 'Pending' },
	{ value: 'REJECTED', label: 'Rejected' },
	{ value: 'DRAFT', label: 'Draft' }
]

// Computed dialog visibility
const dialogVisible = computed({
	get: () => props.visible,
	set: (value) => emit('update:visible', value)
})

// Fetch household data
const fetchHousehold = async () => {
	if (!props.householdId) return

	isLoading.value = true
	fetchError.value = null
	try {
		const response = await axiosInstance.get(`family/${props.householdCode}/`)
		const data = response.data
		formData.value = {
			code: data.code || '',
			name: data.name || '',
			blockNumber: data.blockNumber || '',
			barangay: data.barangay || '',
			city: data.city || '',
			province: data.province || '',
			landline: data.landline || '',
			householdNumber: data.householdNumber || '',
			householdType: data.householdType || 'NUCLEAR',
			housingOwnership: data.housingOwnership || 'OWNED',
			registrationStatus: data.registrationStatus || 'APPROVED'
		}
	} catch (err) {
		fetchError.value = 'Failed to load household details. Please try again.'
		console.error('Error fetching household:', err)
	} finally {
		isLoading.value = false
	}
}

// Reset form
const resetForm = () => {
	formData.value = {
		code: '',
		name: '',
		blockNumber: '',
		barangay: '',
		city: '',
		province: '',
		landline: '',
		householdNumber: '',
		householdType: 'NUCLEAR',
		housingOwnership: 'OWNED',
		registrationStatus: 'APPROVED'
	}
	errors.value = {}
	submitError.value = null
	fetchError.value = null
}

// Validate form
const validateForm = (): boolean => {
	const newErrors: Record<string, string> = {}

	if (!formData.value.code.trim()) {
		newErrors.code = 'Family code is required'
	}
	if (!formData.value.name.trim()) {
		newErrors.name = 'Family name is required'
	}
	if (!formData.value.blockNumber.trim()) {
		newErrors.blockNumber = 'Block number is required'
	}
	if (!formData.value.barangay) {
		newErrors.barangay = 'Barangay is required'
	}
	if (!formData.value.city.trim()) {
		newErrors.city = 'City is required'
	}
	if (!formData.value.province.trim()) {
		newErrors.province = 'Province is required'
	}
	if (!formData.value.householdNumber.trim()) {
		newErrors.householdNumber = 'Household number is required'
	}

	errors.value = newErrors
	return Object.keys(newErrors).length === 0
}

// Submit form
const handleSubmit = async () => {
	if (!validateForm()) return
	if (!props.householdId) return

	isSubmitting.value = true
	submitError.value = null

	try {
		await axiosInstance.put(`family/${props.hose}/`, formData.value)
		emit('success')
		dialogVisible.value = false
		resetForm()
	} catch (err: any) {
		submitError.value = err.response?.data?.message || 'Failed to update household. Please try again.'
		console.error('Error updating household:', err)
	} finally {
		isSubmitting.value = false
	}
}

// Close dialog handler
const handleClose = () => {
	dialogVisible.value = false
	resetForm()
}

// Watch for dialog open to fetch data
watch(() => props.visible, (newVal) => {
	if (newVal && props.householdId) {
		fetchHousehold()
	} else if (!newVal) {
		resetForm()
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
				<h3 class="text-xl font-bold text-gray-800">Edit Household</h3>
				<button
						@click="handleClose"
						class="p-1.5 hover:bg-gray-100 rounded-lg transition-colors text-gray-500 hover:text-gray-700">
					<i class="bx bx-x text-2xl"></i>
				</button>
			</div>

			<!-- Form Body -->
			<div class="flex-1 overflow-y-auto px-6 py-4">
				<!-- Loading State -->
				<div v-if="isLoading" class="flex items-center justify-center py-12">
					<div class="text-center">
						<i class="bx bx-loader-alt text-4xl text-emerald-500 animate-spin block mb-3"></i>
						<p class="text-gray-500">Loading household details...</p>
					</div>
				</div>

				<!-- Error State -->
				<div v-else-if="fetchError" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
					<i class="bx bx-error-circle text-xl"></i>
					{{ fetchError }}
				</div>

				<!-- Form -->
				<form v-else @submit.prevent="handleSubmit" class="space-y-4">
					<!-- Submit Error Alert -->
					<div v-if="submitError" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
						<i class="bx bx-error-circle text-xl"></i>
						{{ submitError }}
					</div>

					<!-- 2 Column Grid -->
					<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
						<!-- Family Code -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Family Code <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.code"
									type="text"
									placeholder="e.g., FAM-001"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.code ? 'border-red-500' : 'border-gray-300'"
							>
							<p v-if="errors.code" class="mt-1 text-xs text-red-500">{{ errors.code }}</p>
						</div>

						<!-- Family Name -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Family Name <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.name"
									type="text"
									placeholder="e.g., Dela Cruz"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.name ? 'border-red-500' : 'border-gray-300'"
							>
							<p v-if="errors.name" class="mt-1 text-xs text-red-500">{{ errors.name }}</p>
						</div>

						<!-- Block Number -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Block Number <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.blockNumber"
									type="text"
									placeholder="e.g., 972A"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.blockNumber ? 'border-red-500' : 'border-gray-300'"
							>
							<p v-if="errors.blockNumber" class="mt-1 text-xs text-red-500">{{ errors.blockNumber }}</p>
						</div>

						<!-- Household Number -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Household Number <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.householdNumber"
									type="text"
									placeholder="e.g., 3501"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.householdNumber ? 'border-red-500' : 'border-gray-300'"
							>
							<p v-if="errors.householdNumber" class="mt-1 text-xs text-red-500">{{ errors.householdNumber }}</p>
						</div>

						<!-- Barangay -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Barangay <span class="text-red-500">*</span>
							</label>
							<select
									v-model="formData.barangay"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.barangay ? 'border-red-500' : 'border-gray-300'"
							>
								<option value="">Select Barangay</option>
								<option
										v-for="barangay in barangayStore.barangays"
										:key="barangay"
										:value="barangay">
									{{ barangay }}
								</option>
							</select>
							<p v-if="errors.barangay" class="mt-1 text-xs text-red-500">{{ errors.barangay }}</p>
						</div>

						<!-- Landline -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Landline Number
							</label>
							<input
									v-model="formData.landline"
									type="text"
									placeholder="e.g., 333-000-222"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
							>
						</div>

						<!-- City -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								City <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.city"
									type="text"
									placeholder="e.g., Gonzaga"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.city ? 'border-red-500' : 'border-gray-300'"
							>
							<p v-if="errors.city" class="mt-1 text-xs text-red-500">{{ errors.city }}</p>
						</div>

						<!-- Province -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Province <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.province"
									type="text"
									placeholder="e.g., Cagayan"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.province ? 'border-red-500' : 'border-gray-300'"
							>
							<p v-if="errors.province" class="mt-1 text-xs text-red-500">{{ errors.province }}</p>
						</div>

						<!-- Household Type -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Household Type
							</label>
							<select
									v-model="formData.householdType"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
							>
								<option
										v-for="option in householdTypeOptions"
										:key="option.value"
										:value="option.value">
									{{ option.label }}
								</option>
							</select>
						</div>

						<!-- Housing Ownership -->
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Housing Ownership
							</label>
							<select
									v-model="formData.housingOwnership"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
							>
								<option
										v-for="option in housingOwnershipOptions"
										:key="option.value"
										:value="option.value">
									{{ option.label }}
								</option>
							</select>
						</div>

						<!-- Registration Status -->
						<div class="col-span-2">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Registration Status
							</label>
							<select
									v-model="formData.registrationStatus"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
							>
								<option
										v-for="option in registrationStatusOptions"
										:key="option.value"
										:value="option.value">
									{{ option.label }}
								</option>
							</select>
						</div>
					</div>
				</form>
			</div>

			<!-- Footer -->
			<div class="flex items-center justify-end gap-3 px-6 py-4 border-t border-gray-200">
				<button
						@click="handleClose"
						class="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
					Cancel
				</button>
				<button
						@click="handleSubmit"
						:disabled="isSubmitting || isLoading"
						class="px-4 py-2 text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2">
					<i v-if="isSubmitting" class="bx bx-loader-alt animate-spin"></i>
					{{ isSubmitting ? 'Updating...' : 'Update Household' }}
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