<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import axiosInstance from '@/axiosInstance.ts'
import MainLayout from '@/layouts/MainLayout.vue'
import type {Account} from '@/types/accounts.ts'

const accounts = ref<Account[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)
const roles = [
	'Administrator',
	'Staff',
	'FamilyAdmin'
]

const showNewAccountDialog = ref(false)
const showViewAccountDialog = ref(false)
const showUpdateAccountDialog = ref(false)
const showDeleteAccountDialog = ref(false)

const selectedAccountId = ref<number | null>(null)

const searchQuery = ref('')
const selectedRole = ref('')

const currentPage = ref(1)
const itemsPerPage = ref(10)

const filteredAccounts = computed(() => {
	let filtered = accounts.value

	if (searchQuery.value.trim()) {
		const query = searchQuery.value.toLowerCase().trim()
		filtered = filtered.filter(item => {
			item.username.toLowerCase().includes(query)
		})
	}

	if (selectedRole.value) {
		filtered = filtered.filter(item =>
				item.role.name.toLowerCase() === selectedRole.value.toLowerCase()
		)
	}

	return filtered
})

const paginatedData = computed(() => {
	const start = (currentPage.value - 1) * itemsPerPage.value
	const end = start + itemsPerPage.value
	return filteredAccounts.value.slice(start, end)
})

const totalPages = computed(() => {
	return Math.ceil(filteredAccounts.value.length / itemsPerPage.value)
})

const clearFilters = () => {
	searchQuery.value = ''
	selectedRole.value = ''
	currentPage.value = 1
}

const fetchAccounts = async () => {
	isLoading.value = true
	error.value = null
	try {
		const response = await axiosInstance.get('account/')
		accounts.value = response.data

		if (paginatedData.value.length === 0 && currentPage.value > 1) {
			currentPage.value--
		}
	} catch (err) {
		error.value = 'Failed to load accounts. Please try again.'
		console.error('Error fetching accounts:', err)
	} finally {
		isLoading.value = false
	}
}

const handleView = (account: Account) => {
	selectedAccountId.value = account.id
	showViewAccountDialog.value = true
}

const handleEdit = (account: Account) => {
	selectedAccountId.value = account.id
	showUpdateAccountDialog.value = true
}

const handleDelete = (account: Account) => {
	selectedAccountId.value = account.id
	showDeleteAccountDialog.value = true
}

const handleAddNew = () => {
	showNewAccountDialog.value = true
}

const goToPage = (page: number) => {
	if (page >= 1 && page <= totalPages.value) {
		currentPage.value = page
	}
}

const getStatusBadgeClass = (role: string) => {
	const roleMap: Record<string, string> = {
		'Administrator': 'bg-green-100 text-green-700',
		'Staff': 'bg-yellow-100 text-yellow-700',
		'FamilyAdmin': 'bg-red-100 text-red-700'
	}
	return roleMap[role] || 'bg-gray-100 text-gray-700'
}

onMounted(() => {
	fetchAccounts()
})
</script>

<template>
	<MainLayout>
		<div class="space-y-4">
			<div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
				<div>
					<h2 class="text-2xl font-bold text-gray-800">Account Management</h2>
					<p class="text-sm text-gray-500 mt-1">Manage all registered accounts in the system</p>
				</div>
				<button
						@click="handleAddNew"
						class="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg transition-colors shadow-sm hover:shadow-md">
					<i class="bx bx-plus-circle text-xl"></i>
					New Account
				</button>
			</div>

			<!-- Filters Section -->
			<div class="bg-white rounded-lg shadow-sm border border-emerald-100 p-4">
				<div class="flex flex-col sm:flex-row sm:items-center gap-3">
					<div class="flex-1 relative">
						<i class="bx bx-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
						<input
								v-model="searchQuery"
								type="text"
								placeholder="Search by username..."
								class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors"
						>
					</div>

					<!-- Filter dropdown -->
					<div class="sm:w-64 relative">
						<i class="bx bx-map absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
						<select
								v-model="selectedRole"
								class="w-full pl-10 pr-8 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors appearance-none bg-white">
							<option value="">All Roles</option>
							<option
									v-for="role in roles"
									:key="role"
									:value="role">
								{{ role }}
							</option>
						</select>
						<i class="bx bx-chevron-down absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
					</div>

					<!-- Clear filters button -->
					<button
							v-if="searchQuery || selectedRole"
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
		        filteredAccounts.length
          }} account{{ filteredAccounts.length !== 1 ? 's' : '' }}
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
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Username</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Email</th>
							<th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Role</th>
							<th class="px-4 py-3 text-center text-sm font-semibold text-gray-700">Actions</th>
						</tr>
						</thead>
						<tbody>
						<tr v-if="isLoading && accounts.length === 0"
						    class="border-b border-gray-100">
							<td colspan="4" class="px-4 py-8 text-center text-gray-500">
								<i class="bx bx-loader-alt text-4xl block text-gray-300 animate-spin"></i>
								Loading accounts...
							</td>
						</tr>
						<tr v-else-if="paginatedData.length === 0 && !isLoading"
						    class="border-b border-gray-100">
							<td colspan="4" class="px-4 py-8 text-center text-gray-500">
								<i class="bx bx-data text-4xl block text-gray-300"></i>
								No accounts found
							</td>
						</tr>
						<tr v-for="account in paginatedData"
						    :key="account.id"
						    class="border-b border-gray-100 hover:bg-emerald-50/50 transition-colors">
							<td class="px-4 py-3 text-sm text-gray-800 font-medium ">
								{{ account.username }}
							</td>
							<td class="px-4 py-3 text-sm text-gray-700 font-medium">
								{{ account.email || 'No email provided.' }}
							</td>
							<td class="px-4 py-3 text-sm">
                <span class="inline-flex px-2 py-1 rounded-full text-xs font-medium"
                      :class="getStatusBadgeClass(account.role.name)">
                  {{ account.role.name }}
                </span>
							</td>
							<td class="px-4 py-3">
								<div class="flex items-center justify-center gap-1">
									<button
											@click="handleView(account)"
											class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
											title="View Details">
										<i class="bx bx-show text-lg"></i>
									</button>
									<button
											@click="handleEdit(account)"
											class="p-1.5 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
											title="Edit">
										<i class="bx bx-edit-alt text-lg"></i>
									</button>
									<button
											@click="handleDelete(account)"
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
								class="px-3 py-1 rounded-lg border transition-colors text-sm min-w-8"
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
				<div v-if="filteredAccounts.length > 0"
				     class="px-4 py-2 bg-gray-50 border-t border-gray-100 flex items-center justify-end gap-2 text-sm text-gray-600">
					<label for="itemsPerPage" class="text-sm">Show:</label>
					<select
							id="itemsPerPage"
							v-model="itemsPerPage"
							@change="currentPage = 1"
							class="border border-gray-200 rounded-lg px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-200">
						<option :value="5">5</option>
						<option :value="10">10</option>
						<option :value="25">25</option>
						<option :value="50">50</option>
					</select>
					<span class="text-sm text-gray-500">per page</span>
				</div>
			</div>
		</div>
	</MainLayout>
</template>