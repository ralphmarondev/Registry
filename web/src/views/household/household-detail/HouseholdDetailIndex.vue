<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import axiosInstance from '@/axiosInstance'

const route = useRoute()
const router = useRouter()

const isLoading = ref(false)
const household = ref<any>(null)
const familyMembers = ref<any[]>([])
const error = ref<string | null>(null)

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

const getLabel = (value: string, options: Array<{ value: string, label: string }>) => {
	const option = options.find(opt => opt.value === value)
	return option ? option.label : value
}

const getStatusBadgeClass = (status: string) => {
	const statusMap: Record<string, string> = {
		'APPROVED': 'bg-green-100 text-green-700',
		'PENDING': 'bg-yellow-100 text-yellow-700',
		'REJECTED': 'bg-red-100 text-red-700',
		'DRAFT': 'bg-gray-100 text-gray-700'
	}
	return statusMap[status] || 'bg-gray-100 text-gray-700'
}

const getRelationshipBadgeClass = (relationship: string) => {
	const relationshipMap: Record<string, string> = {
		'SELF': 'bg-blue-100 text-blue-700',
		'SPOUSE': 'bg-pink-100 text-pink-700',
		'CHILD': 'bg-green-100 text-green-700',
		'SIBLING': 'bg-purple-100 text-purple-700',
		'PARENT': 'bg-orange-100 text-orange-700',
		'RELATIVE': 'bg-gray-100 text-gray-700'
	}
	return relationshipMap[relationship] || 'bg-gray-100 text-gray-700'
}

const getRelationshipIcon = (relationship: string) => {
	const iconMap: Record<string, string> = {
		'SELF': 'bx-user-check',
		'SPOUSE': 'bx-heart',
		'CHILD': 'bx-baby',
		'SIBLING': 'bx-user-voice',
		'PARENT': 'bx-user',
		'RELATIVE': 'bx-group'
	}
	return iconMap[relationship] || 'bx-user'
}

const formatDisplayValue = (value: string | null | undefined, field: string) => {
	if (!value) return '—'
	const uppercaseFields = ['code', 'barangay', 'city', 'province', 'blockNumber']
	if (uppercaseFields.includes(field)) {
		return value.toUpperCase()
	}
	return value
}

const formatDate = (dateString: string) => {
	if (!dateString) return '—'
	const date = new Date(dateString)
	return date.toLocaleDateString('en-US', {
		year: 'numeric',
		month: 'long',
		day: 'numeric'
	})
}

const fetchHousehold = async () => {
	const id = route.params.id as string
	if (!id) {
		error.value = 'No household id provided'
		return
	}

	isLoading.value = true
	error.value = null

	try {
		const response = await axiosInstance.get(`family/${id}/`)
		household.value = response.data

		try {
			const membersResponse = await axiosInstance.get(`member/family/${id}/`)
			familyMembers.value = membersResponse.data || []
		} catch (err) {
			console.log('No family members found or endpoint not available')
			familyMembers.value = []
		}
	} catch (err) {
		error.value = 'Failed to load household details. Please try again.'
		console.error('Error fetching household:', err)
	} finally {
		isLoading.value = false
	}
}

const goToEdit = () => {
	if (household.value.id) {
		router.push(`/family/${household.value.id}/`)
	}
}

const getMemberCount = () => {
	return familyMembers.value.length || household.value?.memberCount || 0
}

const getHeadOfHousehold = () => {
	const head = familyMembers.value.find(member => member.relationship === 'HEAD')
	if (head) {
		return `${head.firstName} ${head.lastName}`
	}
	return 'Not set'
}

onMounted(() => {
	fetchHousehold()
})
</script>

<template>
	<MainLayout>
		<div class="space-y-4">
			<div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
				<div>
					<div class="flex items-center gap-3">
						<h2 class="text-2xl font-bold text-gray-800">Household Details</h2>
						<span
								v-if="household?.registrationStatus"
								class="inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium uppercase"
								:class="getStatusBadgeClass(household.registrationStatus)">
              {{ household.registrationStatus }}
            </span>
					</div>
					<p v-if="household" class="text-sm text-gray-500">
						{{ household.code }} - {{ household.name }}
					</p>
				</div>
				<button
						@click="goToEdit"
						class="inline-flex items-center gap-2 px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white rounded-lg transition-colors shadow-sm hover:shadow-md text-sm">
					<i class="bx bx-edit-alt text-lg"></i>
					Edit Household
				</button>
			</div>

			<div v-if="isLoading" class="bg-white rounded-lg shadow-sm border border-emerald-100 p-8">
				<div class="flex items-center justify-center py-12">
					<div class="text-center">
						<i class="bx bx-loader-alt text-4xl text-emerald-500 animate-spin block mb-3"></i>
						<p class="text-gray-500">Loading household details...</p>
					</div>
				</div>
			</div>

			<div v-else-if="error" class="bg-white rounded-lg shadow-sm border border-emerald-100 p-6">
				<div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
					<i class="bx bx-error-circle text-xl"></i>
					{{ error }}
				</div>
			</div>

			<template v-else-if="household">
				<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
					<div class="bg-white rounded-lg shadow-sm border border-emerald-100 p-4">
						<div class="flex items-center gap-3">
							<div class="w-10 h-10 bg-emerald-100 rounded-lg flex items-center justify-center">
								<i class="bx bx-user text-emerald-600 text-xl"></i>
							</div>
							<div>
								<p class="text-xs text-gray-500">Total Members</p>
								<p class="text-lg font-bold text-gray-800">{{ getMemberCount() }}</p>
							</div>
						</div>
					</div>
					<div class="bg-white rounded-lg shadow-sm border border-emerald-100 p-4">
						<div class="flex items-center gap-3">
							<div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
								<i class="bx bx-user-check text-blue-600 text-xl"></i>
							</div>
							<div>
								<p class="text-xs text-gray-500">Head of Household</p>
								<p class="text-sm font-medium text-gray-800 truncate">{{ getHeadOfHousehold() }}</p>
							</div>
						</div>
					</div>
					<div class="bg-white rounded-lg shadow-sm border border-emerald-100 p-4">
						<div class="flex items-center gap-3">
							<div class="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
								<i class="bx bx-home text-purple-600 text-xl"></i>
							</div>
							<div>
								<p class="text-xs text-gray-500">Household Type</p>
								<p class="text-sm font-medium text-gray-800">
									{{ getLabel(household.householdType, householdTypeOptions) }}
								</p>
							</div>
						</div>
					</div>
					<div class="bg-white rounded-lg shadow-sm border border-emerald-100 p-4">
						<div class="flex items-center gap-3">
							<div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
								<i class="bx bx-map text-orange-600 text-xl"></i>
							</div>
							<div>
								<p class="text-xs text-gray-500">Barangay</p>
								<p class="text-sm font-medium text-gray-800 uppercase">{{ household.barangay || '—' }}</p>
							</div>
						</div>
					</div>
				</div>

				<div class="bg-white rounded-lg shadow-sm border border-emerald-100 overflow-hidden">
					<div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
						<h4 class="text-sm font-semibold text-gray-700">
							<i class="bx bx-info-circle mr-2"></i>
							Household Information
						</h4>
					</div>
					<div class="p-6">
						<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Family Code</label>
								<input
										:value="formatDisplayValue(household.code, 'code')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 font-mono uppercase cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Family Name</label>
								<input
										:value="household.name || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Block Number</label>
								<input
										:value="formatDisplayValue(household.blockNumber, 'blockNumber')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Household Number</label>
								<input
										:value="household.householdNumber || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Barangay</label>
								<input
										:value="formatDisplayValue(household.barangay, 'barangay')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">City</label>
								<input
										:value="formatDisplayValue(household.city, 'city')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Province</label>
								<input
										:value="formatDisplayValue(household.province, 'province')"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 uppercase cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Landline Number</label>
								<input
										:value="household.landline || '—'"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Household Type</label>
								<input
										:value="getLabel(household.householdType, householdTypeOptions)"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled>
							</div>
							<div>
								<label class="block text-sm font-medium text-gray-700 mb-1">Housing Ownership</label>
								<input
										:value="getLabel(household.housingOwnership, housingOwnershipOptions)"
										type="text"
										class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-700 cursor-default"
										readonly
										disabled>
							</div>
						</div>

						<div v-if="household.deleted"
						     class="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg flex items-center gap-2">
							<i class="bx bx-trash text-xl"></i>
							This household has been deleted
						</div>
					</div>
				</div>

				<div class="bg-white rounded-lg shadow-sm border border-emerald-100 overflow-hidden">
					<div class="px-6 py-4 border-b border-gray-200 bg-gray-50 flex items-center justify-between">
						<h4 class="text-sm font-semibold text-gray-700">
							<i class="bx bx-group mr-2"></i>
							Family Members ({{ getMemberCount() }})
						</h4>
						<button class="inline-flex items-center gap-1 px-3 py-1 text-sm text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors">
							<i class="bx bx-plus"></i>
							Add Member
						</button>
					</div>

					<div v-if="familyMembers.length === 0" class="p-8 text-center">
						<i class="bx bx-user-x text-4xl text-gray-300 block mb-2"></i>
						<p class="text-sm text-gray-500">No family members added yet</p>
					</div>

					<div v-else class="overflow-x-auto">
						<table class="w-full">
							<thead class="bg-gray-50 border-b border-gray-200">
							<tr>
								<th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Member</th>
								<th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
									Relationship
								</th>
								<th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Birth Date
								</th>
								<th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Contact</th>
								<th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Account</th>
								<th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions
								</th>
							</tr>
							</thead>
							<tbody class="divide-y divide-gray-200">
							<tr
									v-for="member in familyMembers"
									:key="member.id"
									class="hover:bg-gray-50 transition-colors">
								<td class="px-4 py-3">
									<div class="flex items-center gap-3">
										<div
												class="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-medium"
												:class="member.relationship === 'HEAD' ? 'bg-blue-500' : 'bg-gray-400'">
											<i :class="['bx', getRelationshipIcon(member.relationship)]"></i>
										</div>
										<div>
											<p class="text-sm font-medium text-gray-800">
												{{ member.firstName }} {{ member.middleName ? member.middleName + ' ' : '' }}{{
													member.lastName
												}}
											</p>
											<p v-if="member.occupation" class="text-xs text-gray-500">{{ member.occupation }}</p>
										</div>
									</div>
								</td>
								<td class="px-4 py-3">
                    <span
		                    class="inline-flex px-2 py-0.5 rounded-full text-xs font-medium"
		                    :class="getRelationshipBadgeClass(member.relationship)">
                      {{ member.relationship || 'MEMBER' }}
                    </span>
								</td>
								<td class="px-4 py-3 text-sm text-gray-600">
									{{ formatDate(member.birthDate) }}
								</td>
								<td class="px-4 py-3 text-sm text-gray-600">
									{{ member.contactNumber || '—' }}
								</td>
								<td class="px-4 py-3">
                    <span v-if="member.hasAccount" class="inline-flex items-center gap-1 text-xs text-green-600">
                      <i class="bx bx-check-circle"></i>
                      Active
                    </span>
									<span v-else class="inline-flex items-center gap-1 text-xs text-gray-400">
                      <i class="bx bx-x-circle"></i>
                      None
                    </span>
								</td>
								<td class="px-4 py-3">
									<div class="flex items-center justify-center gap-1">
										<button class="p-1 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors" title="View">
											<i class="bx bx-show text-sm"></i>
										</button>
										<button class="p-1 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors" title="Edit">
											<i class="bx bx-edit-alt text-sm"></i>
										</button>
										<button class="p-1 text-red-600 hover:bg-red-50 rounded-lg transition-colors" title="Remove">
											<i class="bx bx-trash text-sm"></i>
										</button>
									</div>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</template>
		</div>
	</MainLayout>
</template>