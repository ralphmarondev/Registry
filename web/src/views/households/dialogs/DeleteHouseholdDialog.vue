<script setup lang="ts">
import { ref, computed, watch } from 'vue'
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
const isDeleting = ref(false)
const error = ref<string | null>(null)

// Computed dialog visibility
const dialogVisible = computed({
	get: () => props.visible,
	set: (value) => emit('update:visible', value)
})

// Delete handler
const handleDelete = async () => {
	if (!props.householdId) return

	isDeleting.value = true
	error.value = null

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
}

// Reset on close
watch(() => props.visible, (newVal) => {
	if (!newVal) {
		error.value = null
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
		<div class="bg-white rounded-xl shadow-2xl w-full max-w-md">
			<!-- Header -->
			<div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
				<div class="flex items-center gap-3">
					<div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
						<i class="bx bx-trash text-xl text-red-600"></i>
					</div>
					<h3 class="text-xl font-bold text-gray-800">Delete Household</h3>
				</div>
				<button
						@click="handleClose"
						class="p-1.5 hover:bg-gray-100 rounded-lg transition-colors text-gray-500 hover:text-gray-700">
					<i class="bx bx-x text-2xl"></i>
				</button>
			</div>

			<!-- Body -->
			<div class="px-6 py-4">
				<!-- Error Alert -->
				<div v-if="error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2 mb-4">
					<i class="bx bx-error-circle text-xl"></i>
					{{ error }}
				</div>

				<!-- Warning Message -->
				<div class="flex items-start gap-3 bg-amber-50 border border-amber-200 rounded-lg p-4 mb-4">
					<i class="bx bx-error text-xl text-amber-600 mt-0.5"></i>
					<div class="text-sm text-amber-800">
						<p class="font-medium mb-1">Warning: This action cannot be undone!</p>
						<p>Deleting this household will permanently remove all associated data from the system.</p>
					</div>
				</div>

				<!-- Household Info -->
				<div class="bg-gray-50 rounded-lg p-4 space-y-2">
					<div class="flex justify-between items-center">
						<span class="text-sm font-medium text-gray-500">Family Code:</span>
						<span class="text-sm font-mono bg-white px-2 py-1 rounded border border-gray-200">
              {{ householdCode || '—' }}
            </span>
					</div>
					<div class="flex justify-between items-center">
						<span class="text-sm font-medium text-gray-500">Family Name:</span>
						<span class="text-sm font-medium text-gray-900">
              {{ householdName || '—' }}
            </span>
					</div>
				</div>

				<!-- Confirmation Text -->
				<p class="text-sm text-gray-600 mt-4">
					Are you sure you want to delete this household? This action is permanent and cannot be reversed.
				</p>
			</div>

			<!-- Footer -->
			<div class="flex items-center justify-end gap-3 px-6 py-4 border-t border-gray-200">
				<button
						@click="handleClose"
						:disabled="isDeleting"
						class="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors">
					Cancel
				</button>
				<button
						@click="handleDelete"
						:disabled="isDeleting"
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