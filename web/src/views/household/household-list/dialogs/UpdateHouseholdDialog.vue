<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {useBarangayStore} from '@/stores/barangay.ts'
import axiosInstance from '@/axiosInstance.ts'

interface Props {
	visible: boolean
	id: number | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
	(e: 'update:visible', value: boolean): void
	(e: 'success'): void
}>()

const barangayStore = useBarangayStore()

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

const errors = ref<Record<string, string>>({})
const isSubmitting = ref(false)
const isLoading = ref(false)
const submitError = ref<string | null>(null)
const fetchError = ref<string | null>(null)

const householdTypeOptions = [
	{value: 'NUCLEAR', label: 'Nuclear'},
	{value: 'EXTENDED', label: 'Extended'},
	{value: 'JOINT', label: 'Joint'},
	{value: 'SINGLE_PARENT', label: 'Single Parent'}
]

const housingOwnershipOptions = [
	{value: 'OWNED', label: 'Owned'},
	{value: 'RENTED', label: 'Rented'},
	{value: 'LEASED', label: 'Leased'},
	{value: 'SHARED', label: 'Shared'},
	{value: 'OTHER', label: 'Other'}
]

const registrationStatusOptions = [
	{value: 'APPROVED', label: 'Approved'},
	{value: 'PENDING', label: 'Pending'},
	{value: 'REJECTED', label: 'Rejected'},
	{value: 'DRAFT', label: 'Draft'}
]

const dialogVisible = computed({
	get: () => props.visible,
	set: (value) => emit('update:visible', value)
})

const getStatusBadgeClass = (status: string) => {
	const statusMap: Record<string, string> = {
		'APPROVED': 'bg-green-100 text-green-700',
		'PENDING': 'bg-yellow-100 text-yellow-700',
		'REJECTED': 'bg-red-100 text-red-700',
		'DRAFT': 'bg-gray-100 text-gray-700'
	}
	return statusMap[status] || 'bg-gray-100 text-gray-700'
}

const fetchHousehold = async () => {
	if (!props.id) return

	isLoading.value = true
	fetchError.value = null
	try {
		const response = await axiosInstance.get(`family/${props.id}/`)
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

const handleSubmit = async () => {
	if (!validateForm()) return
	if (!props.id) return

	isSubmitting.value = true
	submitError.value = null

	try {
		await axiosInstance.put(`family/${props.id}/`, formData.value)
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

const handleClose = () => {
	dialogVisible.value = false
	resetForm()
}

watch(() => props.visible, (newVal) => {
	if (newVal && props.id) {
		fetchHousehold()
	} else if (!newVal) {
		resetForm()
	}
})

watch(() => props.id, (newVal) => {
	if (props.visible && newVal) {
		fetchHousehold()
	}
})
</script>

<template>
	<div v-if="dialogVisible"
	     class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
	     @click.self="handleClose">
		<div class="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
			<div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
				<div class="flex items-center gap-3">
					<h3 class="text-xl font-bold text-gray-800">Edit Household</h3>
					<span
							v-if="formData.registrationStatus"
							class="inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium uppercase"
							:class="getStatusBadgeClass(formData.registrationStatus)">
            {{ formData.registrationStatus }}
          </span>
				</div>
				<button
						@click="handleClose"
						class="p-1.5 hover:bg-gray-100 rounded-lg transition-colors text-gray-500 hover:text-gray-700">
					<i class="bx bx-x text-2xl"></i>
				</button>
			</div>

			<div class="flex-1 overflow-y-auto px-6 py-4">
				<div v-if="isLoading" class="flex items-center justify-center py-12">
					<div class="text-center">
						<i class="bx bx-loader-alt text-4xl text-emerald-500 animate-spin block mb-3"></i>
						<p class="text-gray-500">Loading household details...</p>
					</div>
				</div>

				<div v-else-if="fetchError"
				     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
					<i class="bx bx-error-circle text-xl"></i>
					{{ fetchError }}
				</div>

				<form v-else @submit.prevent="handleSubmit" class="space-y-4">
					<div v-if="submitError"
					     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
						<i class="bx bx-error-circle text-xl"></i>
						{{ submitError }}
					</div>

					<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Family Code <span class="text-red-500">*</span>
							</label>
							<div class="relative">
								<input
										v-model="formData.code"
										type="text"
										class="w-full px-3 py-2 border rounded-lg bg-gray-50 text-gray-600 uppercase cursor-not-allowed"
										readonly
										disabled>
								<i class="bx bx-lock-alt absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
							</div>
							<p class="mt-1 text-xs text-gray-500">
								<i class="bx bx-info-circle mr-1"></i>
								Family code cannot be changed
							</p>
						</div>

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Family Name <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.name"
									type="text"
									placeholder="e.g., Dela Cruz"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.name ? 'border-red-500' : 'border-gray-300'">
							<p v-if="errors.name" class="mt-1 text-xs text-red-500">{{ errors.name }}</p>
						</div>
						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Block Number <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.blockNumber"
									type="text"
									placeholder="e.g., 972A"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.blockNumber ? 'border-red-500' : 'border-gray-300'">
							<p v-if="errors.blockNumber" class="mt-1 text-xs text-red-500">{{ errors.blockNumber }}</p>
						</div>

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Household Number <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.householdNumber"
									type="text"
									placeholder="e.g., 3501"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.householdNumber ? 'border-red-500' : 'border-gray-300'">
							<p v-if="errors.householdNumber" class="mt-1 text-xs text-red-500">{{ errors.householdNumber }}</p>
						</div>

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Barangay <span class="text-red-500">*</span>
							</label>
							<select
									v-model="formData.barangay"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.barangay ? 'border-red-500' : 'border-gray-300'">
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

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Landline Number
							</label>
							<input
									v-model="formData.landline"
									type="text"
									placeholder="e.g., 333-000-222"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
						</div>

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								City <span class="text-red-500">*</span>
							</label>
							<input
									v-model="formData.city"
									type="text"
									placeholder="e.g., Gonzaga"
									class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
									:class="errors.city ? 'border-red-500' : 'border-gray-300'">
							<p v-if="errors.city" class="mt-1 text-xs text-red-500">{{ errors.city }}</p>
						</div>

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

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Household Type
							</label>
							<select
									v-model="formData.householdType"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
								<option
										v-for="option in householdTypeOptions"
										:key="option.value"
										:value="option.value">
									{{ option.label }}
								</option>
							</select>
						</div>

						<div class="col-span-1">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Housing Ownership
							</label>
							<select
									v-model="formData.housingOwnership"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
								<option
										v-for="option in housingOwnershipOptions"
										:key="option.value"
										:value="option.value">
									{{ option.label }}
								</option>
							</select>
						</div>

						<div class="col-span-2">
							<label class="block text-sm font-medium text-gray-700 mb-1">
								Registration Status
							</label>
							<select
									v-model="formData.registrationStatus"
									class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
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

			<div class="flex items-center justify-between px-6 py-4 border-t border-gray-200">
				<router-link
						:to="{ name: 'household-details', params: { id: id } }"
						class="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg transition-colors shadow-sm hover:shadow-md"
						@click="handleClose">
					<i class="bx bx-group text-lg"></i>
					Manage Household Members
				</router-link>
				<div class="flex items-center gap-3">
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
	</div>
</template>