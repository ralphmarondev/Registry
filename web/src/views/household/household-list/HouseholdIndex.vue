<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useBarangayStore} from '@/stores/barangay.ts'
import axiosInstance from '@/axiosInstance.ts'
import NewHouseholdDialog from './dialogs/NewHouseholdDialog.vue'
import ViewHouseholdDialog from './dialogs/ViewHouseholdDialog.vue'
import UpdateHouseholdDialog from './dialogs/UpdateHouseholdDialog.vue'
import DeleteHouseholdDialog from './dialogs/DeleteHouseholdDialog.vue'
import MainLayout from '@/layouts/MainLayout.vue'

// Types based on API response
interface Household {
	id: number
	code: string
	name: string
	blockNumber: string
	barangay: string
	city: string
	province: string
	landline: string
	householdNumber: string
	householdType: string
	housingOwnership: string
	registrationStatus: string
	memberCount: number
	createDate: string
	updateDate: string | null
	deleted: boolean
}

const router = useRouter()
const barangayStore = useBarangayStore()

// State
const households = ref<Household[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

// Dialog states
const showNewHouseholdDialog = ref(false)
const showViewHouseholdDialog = ref(false)
const showUpdateHouseholdDialog = ref(false)
const showDeleteHouseholdDialog = ref(false)

// Selected household data
const selectedHouseholdId = ref<number | null>(null)
const selectedHouseholdName = ref('')
const selectedHouseholdCode = ref('')

// Filter states
const searchQuery = ref('')
const selectedBarangay = ref('')

// Pagination states
const currentPage = ref(1)
const itemsPerPage = ref(10)

// Computed filtered data
const filteredHouseholds = computed(() => {
	let filtered = households.value

	// Search filter - search by code or name
	if (searchQuery.value.trim()) {
		const query = searchQuery.value.toLowerCase().trim()
		filtered = filtered.filter(item =>
				item.code.toLowerCase().includes(query) ||
				item.name.toLowerCase().includes(query)
		)
	}

	// Barangay filter
	if (selectedBarangay.value) {
		filtered = filtered.filter(item =>
				item.barangay === selectedBarangay.value
		)
	}

	return filtered
})

// Paginated data
const paginatedData = computed(() => {
	const start = (currentPage.value - 1) * itemsPerPage.value
	const end = start + itemsPerPage.value
	return filteredHouseholds.value.slice(start, end)
})

// Total pages for pagination
const totalPages = computed(() => {
	return Math.ceil(filteredHouseholds.value.length / itemsPerPage.value)
})

// Clear all filters
const clearFilters = () => {
	searchQuery.value = ''
	selectedBarangay.value = ''
	currentPage.value = 1
}

// Fetch households from API
const fetchHouseholds = async () => {
	isLoading.value = true
	error.value = null
	try {
		const response = await axiosInstance.get('family/')
		households.value = response.data
	} catch (err) {
		error.value = 'Failed to load households. Please try again.'
		console.error('Error fetching households:', err)
	} finally {
		isLoading.value = false
	}
}

// Action handlers
const handleView = (household: Household) => {
	selectedHouseholdId.value = household.id
	selectedHouseholdCode.value = household.code
	showViewHouseholdDialog.value = true
}

const handleEdit = (household: Household) => {
	selectedHouseholdId.value = household.id
	selectedHouseholdCode.value = household.code
	showUpdateHouseholdDialog.value = true
}

const handleDelete = (household: Household) => {
	selectedHouseholdId.value = household.id
	selectedHouseholdName.value = household.name
	selectedHouseholdCode.value = household.code
	showDeleteHouseholdDialog.value = true
}

const handleAddNew = () => {
	showNewHouseholdDialog.value = true
}

// Dialog success handlers
const handleHouseholdCreated = async () => {
	await fetchHouseholds()
}

const handleHouseholdUpdated = async () => {
	await fetchHouseholds()
}

const handleHouseholdDeleted = async () => {
	await fetchHouseholds()
	// Reset to first page if current page is now empty
	if (paginatedData.value.length === 0 && currentPage.value > 1) {
		currentPage.value--
	}
}

// Page change handler
const goToPage = (page: number) => {
	if (page >= 1 && page <= totalPages.value) {
		currentPage.value = page
	}
}

// Get status badge color
const getStatusBadgeClass = (status: string) => {
	const statusMap: Record<string, string> = {
		'APPROVED': 'bg-green-100 text-green-700',
		'PENDING': 'bg-yellow-100 text-yellow-700',
		'REJECTED': 'bg-red-100 text-red-700'
	}
	return statusMap[status] || 'bg-gray-100 text-gray-700'
}

onMounted(() => {
	fetchHouseholds()
})
</script>

<template>
	<MainLayout>
		<div class="space-y-4">
			<!-- Header with title and add button -->
			<div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
				<div>
					<h2 class="text-2xl font-bold text-gray-800">Household Management</h2>
					<p class="text-sm text-gray-500 mt-1">Manage all registered households in the system</p>
				</div>
				<button
						@click="handleAddNew"
						class="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg transition-colors shadow-sm hover:shadow-md">
					<i class="bx bx-plus-circle text-xl"></i>
					New Household
				</button>
			</div>

			<!-- Filters Section -->
			<div class="bg-white rounded-lg shadow-sm border border-emerald-100 p-4">
				<div class="flex flex-col sm:flex-row sm:items-center gap-3">
					<!-- Search input -->
					<div class="flex-1 relative">
						<i class="bx bx-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
						<input
								v-model="searchQuery"
								type="text"
								placeholder="Search by Family Code or Name..."
								class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
						>
					</div>

					<!-- Barangay dropdown -->
					<div class="sm:w-64 relative">
						<i class="bx bx-map absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
						<select
								v-model="selectedBarangay"
								class="w-full pl-10 pr-8 py-2 border border-gray-200 rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors appearance-none bg-white">
							<option value="">All Barangays</option>
							<option
									v-for="barangay in barangayStore.barangays"
									:key="barangay"
									:value="barangay">
								{{ barangay }}
							</option>
						</select>
						<i class="bx bx-chevron-down absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
					</div>

					<!-- Clear filters button -->
					<button
							v-if="searchQuery || selectedBarangay"
							@click="clearFilters"
							class="px-3 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors whitespace-nowrap">
						<i class="bx bx-x mr-1"></i>
						Clear Filters
					</button>
				</div>

				<!-- Results count -->
				<div class="mt-3 flex items-center justify-between text-sm text-gray-500">
        <span>
          Showing {{ paginatedData.length }} of {{
		        filteredHouseholds.length
          }} household{{ filteredHouseholds.length !== 1 ? 's' : '' }}
        </span>
					<span v-if="isLoading" class="flex items-center gap-1">
          <i class="bx bx-loader-alt animate-spin"></i>
          Loading...
        </span>
				</div>
			</div>

			<!-- Error Alert -->
			<div v-if="error"
			     class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
				<i class="bx bx-error-circle text-xl"></i>
				{{ error }}
			</div>

			<!-- Data Table -->
			<div class="bg-white rounded-lg shadow-sm border border-emerald-100 overflow-hidden">
				<div class="overflow-x-auto">
					<table class="w-full">
						<thead class="bg-emerald-50 border-b border-emerald-100">
						<tr>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Family Code</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Family Name</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Block No.</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Barangay</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Landline</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Status</th>
							<th class="px-4 py-3 text-center text-sm font-semibold text-gray-700">Actions</th>
						</tr>
						</thead>
						<tbody>
						<tr
								v-if="isLoading && households.length === 0"
								class="border-b border-gray-100">
							<td colspan="7" class="px-4 py-8 text-center text-gray-500">
								<i class="bx bx-loader-alt text-4xl block mb-2 text-gray-300 animate-spin"></i>
								Loading households...
							</td>
						</tr>
						<tr
								v-else-if="paginatedData.length === 0 && !isLoading"
								class="border-b border-gray-100">
							<td colspan="7" class="px-4 py-8 text-center text-gray-500">
								<i class="bx bx-data text-4xl block mb-2 text-gray-300"></i>
								No households found
							</td>
						</tr>
						<tr
								v-for="household in paginatedData"
								:key="household.id"
								class="border-b border-gray-100 hover:bg-emerald-50/50 transition-colors">
							<td class="px-4 py-3 text-sm font-medium text-gray-800">
                <span class="font-mono text-xs bg-gray-100 px-2 py-1 rounded">
                  {{ household.code }}
                </span>
							</td>
							<td class="px-4 py-3 text-sm text-gray-700 font-medium">
								{{ household.name }}
							</td>
							<td class="px-4 py-3 text-sm text-gray-700">
								{{ household.blockNumber || '—' }}
							</td>
							<td class="px-4 py-3 text-sm text-gray-700">
                <span class="inline-flex px-2 py-1 bg-emerald-100 text-emerald-700 rounded-full text-xs font-medium">
                  {{ household.barangay }}
                </span>
							</td>
							<td class="px-4 py-3 text-sm text-gray-700">
								{{ household.landline || '—' }}
							</td>
							<td class="px-4 py-3 text-sm">
                <span class="inline-flex px-2 py-1 rounded-full text-xs font-medium"
                      :class="getStatusBadgeClass(household.registrationStatus)">
                  {{ household.registrationStatus }}
                </span>
							</td>
							<td class="px-4 py-3">
								<div class="flex items-center justify-center gap-1">
									<button
											@click="handleView(household)"
											class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
											title="View Details">
										<i class="bx bx-show text-lg"></i>
									</button>
									<button
											@click="handleEdit(household)"
											class="p-1.5 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
											title="Edit">
										<i class="bx bx-edit-alt text-lg"></i>
									</button>
									<button
											@click="handleDelete(household)"
											class="p-1.5 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
											title="Delete">
										<i class="bx bx-trash text-lg"></i>
									</button>
								</div>
							</td>
						</tr>
						</tbody>
					</table>
				</div>

				<!-- Pagination -->
				<div v-if="totalPages > 1"
				     class="px-4 py-3 bg-emerald-50/50 border-t border-emerald-100 flex flex-col sm:flex-row items-center justify-between gap-3">
					<div class="text-sm text-gray-600">
						Page {{ currentPage }} of {{ totalPages }}
					</div>
					<div class="flex gap-1 flex-wrap justify-center">
						<button
								@click="goToPage(1)"
								:disabled="currentPage === 1"
								class="px-3 py-1 rounded-lg border border-gray-200 bg-white hover:bg-emerald-50 hover:border-emerald-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-colors text-sm">
							<i class="bx bx-chevrons-left"></i>
						</button>
						<button
								@click="goToPage(currentPage - 1)"
								:disabled="currentPage === 1"
								class="px-3 py-1 rounded-lg border border-gray-200 bg-white hover:bg-emerald-50 hover:border-emerald-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-colors">
							<i class="bx bx-chevron-left"></i>
						</button>
						<button
								v-for="page in totalPages"
								:key="page"
								@click="goToPage(page)"
								class="px-3 py-1 rounded-lg border transition-colors text-sm min-w-[32px]"
								:class="[
                page === currentPage
                  ? 'bg-emerald-600 text-white border-emerald-600'
                  : 'bg-white border-gray-200 hover:bg-emerald-50 hover:border-emerald-300'
              ]">
							{{ page }}
						</button>
						<button
								@click="goToPage(currentPage + 1)"
								:disabled="currentPage === totalPages"
								class="px-3 py-1 rounded-lg border border-gray-200 bg-white hover:bg-emerald-50 hover:border-emerald-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-colors">
							<i class="bx bx-chevron-right"></i>
						</button>
						<button
								@click="goToPage(totalPages)"
								:disabled="currentPage === totalPages"
								class="px-3 py-1 rounded-lg border border-gray-200 bg-white hover:bg-emerald-50 hover:border-emerald-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-white transition-colors text-sm">
							<i class="bx bx-chevrons-right"></i>
						</button>
					</div>
				</div>

				<!-- Items per page selector -->
				<div v-if="filteredHouseholds.length > 0"
				     class="px-4 py-2 bg-gray-50 border-t border-gray-100 flex items-center justify-end gap-2 text-sm text-gray-600">
					<label for="itemsPerPage" class="text-sm">Show:</label>
					<select
							id="itemsPerPage"
							v-model="itemsPerPage"
							@change="currentPage = 1"
							class="border border-gray-200 rounded-lg px-2 py-1 text-sm focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200">
						<option :value="5">5</option>
						<option :value="10">10</option>
						<option :value="25">25</option>
						<option :value="50">50</option>
					</select>
					<span class="text-sm text-gray-500">per page</span>
				</div>
			</div>

			<!-- Dialogs -->
			<NewHouseholdDialog
					v-model:visible="showNewHouseholdDialog"
					@success="handleHouseholdCreated"
			/>

			<ViewHouseholdDialog
					v-model:visible="showViewHouseholdDialog"
					:household-id="selectedHouseholdId"
					:household-code="selectedHouseholdCode"
			/>

			<UpdateHouseholdDialog
					v-model:visible="showUpdateHouseholdDialog"
					:household-id="selectedHouseholdId"
					:household-code="selectedHouseholdCode"
					@success="handleHouseholdUpdated"
			/>

			<DeleteHouseholdDialog
					v-model:visible="showDeleteHouseholdDialog"
					:household-id="selectedHouseholdId"
					:household-name="selectedHouseholdName"
					:household-code="selectedHouseholdCode"
					@success="handleHouseholdDeleted"
			/>
		</div>
	</MainLayout>
</template>