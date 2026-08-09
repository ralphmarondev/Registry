<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import axiosInstance from '@/axiosInstance.ts'

interface Props {
	visible: boolean
	householdId: number | null
	householdCode: string | null
}

const props = defineProps<Props>()
const router = useRouter()

// Emits
const emit = defineEmits<{
	(e: 'update:visible', value: boolean): void
}>()

// State
const household = ref<any>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

// Computed dialog visibility
const dialogVisible = computed({
	get: () => props.visible,
	set: (value) => emit('update:visible', value)
})

// Household type options for display
const householdTypeOptions = [
	{value: 'NUCLEAR', label: 'Nuclear'},
	{value: 'EXTENDED', label: 'Extended'},
	{value: 'JOINT', label: 'Joint'},
	{value: 'SINGLE_PARENT', label: 'Single Parent'}
]

// Housing ownership options for display
const housingOwnershipOptions = [
	{value: 'OWNED', label: 'Owned'},
	{value: 'RENTED', label: 'Rented'},
	{value: 'LEASED', label: 'Leased'},
	{value: 'SHARED', label: 'Shared'},
	{value: 'OTHER', label: 'Other'}
]

// Get label from value
const getLabel = (value: string, options: Array<{ value: string, label: string }>) => {
	const option = options.find(opt => opt.value === value)
	return option ? option.label : value
}

// Get status badge color
const getStatusBadgeClass = (status: string) => {
	const statusMap: Record<string, string> = {
		'APPROVED': 'bg-green-100 text-green-700',
		'PENDING': 'bg-yellow-100 text-yellow-700',
		'REJECTED': 'bg-red-100 text-red-700',
		'DRAFT': 'bg-gray-100 text-gray-700'
	}
	return statusMap[status] || 'bg-gray-100 text-gray-700'
}

// Format value for display (uppercase for codes, natural for names)
const formatDisplayValue = (value: string | null | undefined, field: string) => {
	if (!value) return '—'

	// Uppercase for codes and statuses
	const uppercaseFields = ['code', 'barangay', 'city', 'province']
	if (uppercaseFields.includes(field)) {
		return value.toUpperCase()
	}

	// Natural case for names
	return value
}

// Fetch household details
const fetchHousehold = async () => {
	if (!props.householdId) return

	isLoading.value = true
	error.value = null
	try {
		const response = await axiosInstance.get(`family/${props.householdCode}/`)
		household.value = response.data
	} catch (err) {
		error.value = 'Failed to load household details. Please try again.'
		console.error('Error fetching household:', err)
	} finally {
		isLoading.value = false
	}
}

// Navigate to household members
const goToHouseholdMembers = () => {
	if (props.householdId) {
		dialogVisible.value = false
		router.push(`/households/${props.householdId}/members`)
	}
}

// Close dialog handler
const handleClose = () => {
	dialogVisible.value = false
	household.value = null
	error.value = null
}

// Watch for dialog open to fetch data
watch(() => props.visible, (newVal) => {
	if (newVal && props.householdId) {
		fetchHousehold()
	} else if (!newVal) {
		household.value = null
		error.value = null
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
	<div
			v-if="dialogVisible"
			class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
			@click.self="handleClose">

		<!-- Dialog Content -->
		<div class="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
			<!-- Header -->
			<div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
				<div class="flex items-center gap-3">
					<h3 class="text-xl font-bold text-gray-800">Household Details</h3>
					<span
							v-if="household?.registrationStatus"
							class="inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium uppercase"
							:class="getStatusBadgeClass(household.registrationStatus)">
            {{ household.registrationStatus }}
          </span>
				</div>
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
				<div v-else-if="error"
				     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
					<i class="bx bx-error-circle text-xl"></i>
					{{ error }}
				</div>

				<!-- Content -->
				<div v-else-if="household" class="space-y-6">
					<!-- Basic Information Section -->
					<div>
						<h4 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
							<i class="bx bx-info-circle mr-1"></i>
							Basic Information
						</h4>
						<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Family Code</label>
								<input
										:value="formatDisplayValue(household.code, 'code')"
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
										:value="formatDisplayValue(household.blockNumber, 'code')"
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

					<!-- Address Section -->
					<div>
						<h4 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
							<i class="bx bx-map mr-1"></i>
							Address Information
						</h4>
						<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Barangay</label>
								<input
										:value="formatDisplayValue(household.barangay, 'barangay')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">City</label>
								<input
										:value="formatDisplayValue(household.city, 'city')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Province</label>
								<input
										:value="formatDisplayValue(household.province, 'province')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled
								>
							</div>
						</div>
					</div>

					<!-- Additional Information -->
					<div>
						<h4 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
							<i class="bx bx-detail mr-1"></i>
							Additional Information
						</h4>
						<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Landline Number</label>
								<input
										:value="household.landline || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Household Type</label>
								<input
										:value="getLabel(household.householdType, householdTypeOptions)"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Housing Ownership</label>
								<input
										:value="getLabel(household.housingOwnership, housingOwnershipOptions)"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled
								>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Member Count</label>
								<input
										:value="household.memberCount || 0"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled
								>
							</div>
						</div>
					</div>

					<!-- Deleted Status -->
					<div v-if="household.deleted"
					     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
						<i class="bx bx-trash text-xl"></i>
						This household has been deleted
					</div>
				</div>
			</div>

			<!-- Footer -->
			<div class="flex items-center justify-between px-6 py-4 border-t border-gray-200">
				<button
						@click="goToHouseholdMembers"
						class="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg transition-colors shadow-sm hover:shadow-md">
					<i class="bx bx-group text-lg"></i>
					Manage Household Members
				</button>
				<button
						@click="handleClose"
						class="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
					Close
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

/* Remove default input styling for disabled inputs */
input:disabled {
	-webkit-text-fill-color: #374151;
	opacity: 1;
}
</style>