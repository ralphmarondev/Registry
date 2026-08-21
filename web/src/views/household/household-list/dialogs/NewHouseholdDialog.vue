<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'
import {useBarangayStore} from '@/stores/barangay.ts'
import axiosInstance from '@/axiosInstance.ts'

interface Props {
	visible: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
	(e: 'update:visible', value: boolean): void
	(e: 'success'): void
}>()

const barangayStore = useBarangayStore()

const currentStep = ref<number>(1)
const totalSteps = 3

const generateFamilyCode = (): string => {
	return `FAM-${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`
}

interface HouseholdForm {
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
}

const householdForm = ref<HouseholdForm>({
	code: generateFamilyCode(),
	name: '',
	blockNumber: '',
	barangay: '',
	city: 'Gonzaga',
	province: 'Cagayan',
	landline: '',
	householdNumber: '',
	householdType: 'NUCLEAR',
	housingOwnership: 'OWNED',
	registrationStatus: 'APPROVED'
})

interface HeadForm {
	firstName: string
	middleName: string
	lastName: string
	suffix: string
	birthDate: string
	gender: string
	civilStatus: string
	occupation: string
	contactNumber: string
	email: string
}

const headForm = ref<HeadForm>({
	firstName: '',
	middleName: '',
	lastName: '',
	suffix: '',
	birthDate: '',
	gender: 'MALE',
	civilStatus: 'MARRIED',
	occupation: '',
	contactNumber: '',
	email: ''
})

interface AccountForm {
	username: string
	password: string
	confirmPassword: string
	roleId: number
}

const accountForm = ref<AccountForm>({
	username: '',
	password: '',
	confirmPassword: '',
	roleId: 1
})

interface Errors {
	household: Record<string, string>
	head: Record<string, string>
	account: Record<string, string>
}

const errors = ref<Errors>({
	household: {},
	head: {},
	account: {}
})

const isSubmitting = ref<boolean>(false)
const submitError = ref<string | null>(null)

interface Option {
	value: string
	label: string
}

const genderOptions: Option[] = [
	{value: 'MALE', label: 'Male'},
	{value: 'FEMALE', label: 'Female'}
]

const civilStatusOptions: Option[] = [
	{value: 'SINGLE', label: 'Single'},
	{value: 'MARRIED', label: 'Married'},
	{value: 'DIVORCED', label: 'Divorced'},
	{value: 'WIDOWED', label: 'Widowed'},
	{value: 'SEPARATED', label: 'Separated'}
]

const householdTypeOptions: Option[] = [
	{value: 'NUCLEAR', label: 'Nuclear'},
	{value: 'EXTENDED', label: 'Extended'},
	{value: 'JOINT', label: 'Joint'},
	{value: 'SINGLE_PARENT', label: 'Single Parent'}
]

const housingOwnershipOptions: Option[] = [
	{value: 'OWNED', label: 'Owned'},
	{value: 'RENTED', label: 'Rented'},
	{value: 'LEASED', label: 'Leased'},
	{value: 'SHARED', label: 'Shared'},
	{value: 'OTHER', label: 'Other'}
]

const registrationStatusOptions: Option[] = [
	{value: 'APPROVED', label: 'Approved'},
	{value: 'PENDING', label: 'Pending'},
	{value: 'REJECTED', label: 'Rejected'},
	{value: 'DRAFT', label: 'Draft'}
]

interface Step {
	id: number
	title: string
	description: string
	icon: string
}

const steps: Step[] = [
	{
		id: 1,
		title: 'Household Info',
		description: 'Basic household details',
		icon: 'bx-home'
	},
	{
		id: 2,
		title: 'Family Head',
		description: 'Head of family details',
		icon: 'bx-user'
	},
	{
		id: 3,
		title: 'Account Setup',
		description: 'Login credentials',
		icon: 'bx-lock-alt'
	}
]

const dialogVisible = computed({
	get: (): boolean => props.visible,
	set: (value: boolean): void => emit('update:visible', value)
})

const validateStep1 = (): boolean => {
	const newErrors: Record<string, string> = {}

	if (!householdForm.value.code.trim()) {
		newErrors.code = 'Family code is required'
	}
	if (!householdForm.value.name.trim()) {
		newErrors.name = 'Family name is required'
	}
	if (!householdForm.value.blockNumber.trim()) {
		newErrors.blockNumber = 'Block number is required'
	}
	if (!householdForm.value.barangay) {
		newErrors.barangay = 'Barangay is required'
	}
	if (!householdForm.value.city.trim()) {
		newErrors.city = 'City is required'
	}
	if (!householdForm.value.province.trim()) {
		newErrors.province = 'Province is required'
	}
	if (!householdForm.value.householdNumber.trim()) {
		newErrors.householdNumber = 'Household number is required'
	}

	errors.value.household = newErrors
	return Object.keys(newErrors).length === 0
}

const validateStep2 = (): boolean => {
	const newErrors: Record<string, string> = {}

	if (!headForm.value.firstName.trim()) {
		newErrors.firstName = 'First name is required'
	}
	if (!headForm.value.lastName.trim()) {
		newErrors.lastName = 'Last name is required'
	}
	if (!headForm.value.birthDate) {
		newErrors.birthDate = 'Birth date is required'
	}
	if (!headForm.value.gender) {
		newErrors.gender = 'Gender is required'
	}
	if (!headForm.value.civilStatus) {
		newErrors.civilStatus = 'Civil status is required'
	}
	if (!headForm.value.contactNumber.trim()) {
		newErrors.contactNumber = 'Contact number is required'
	}
	if (headForm.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(headForm.value.email)) {
		newErrors.email = 'Please enter a valid email address'
	}

	errors.value.head = newErrors
	return Object.keys(newErrors).length === 0
}

const validateStep3 = (): boolean => {
	const newErrors: Record<string, string> = {}

	if (!accountForm.value.username.trim()) {
		newErrors.username = 'Username is required'
	} else if (accountForm.value.username.length < 3) {
		newErrors.username = 'Username must be at least 3 characters'
	}

	if (!accountForm.value.password) {
		newErrors.password = 'Password is required'
	} else if (accountForm.value.password.length < 6) {
		newErrors.password = 'Password must be at least 6 characters'
	}

	if (!accountForm.value.confirmPassword) {
		newErrors.confirmPassword = 'Please confirm your password'
	} else if (accountForm.value.password !== accountForm.value.confirmPassword) {
		newErrors.confirmPassword = 'Passwords do not match'
	}

	errors.value.account = newErrors
	return Object.keys(newErrors).length === 0
}

const goToNextStep = (): void => {
	let isValid = true

	switch (currentStep.value) {
		case 1:
			isValid = validateStep1()
			break
		case 2:
			isValid = validateStep2()
			break
	}

	if (isValid && currentStep.value < totalSteps) {
		currentStep.value++
	}
}

const goToPreviousStep = (): void => {
	if (currentStep.value > 1) {
		currentStep.value--
	}
}

const goToStep = (step: number): void => {
	if (step < currentStep.value) {
		currentStep.value = step
	}
}

const resetForm = (): void => {
	currentStep.value = 1
	householdForm.value = {
		code: generateFamilyCode(),
		name: '',
		blockNumber: '',
		barangay: '',
		city: 'Gonzaga',
		province: 'Cagayan',
		landline: '',
		householdNumber: '',
		householdType: 'NUCLEAR',
		housingOwnership: 'OWNED',
		registrationStatus: 'APPROVED'
	}
	headForm.value = {
		firstName: '',
		middleName: '',
		lastName: '',
		suffix: '',
		birthDate: '',
		gender: 'MALE',
		civilStatus: 'MARRIED',
		occupation: '',
		contactNumber: '',
		email: ''
	}
	accountForm.value = {
		username: '',
		password: '',
		confirmPassword: '',
		roleId: 1
	}
	errors.value = {
		household: {},
		head: {},
		account: {}
	}
	submitError.value = null
}

const handleSubmit = async (): Promise<void> => {
	if (!validateStep3()) return

	isSubmitting.value = true
	submitError.value = null

	try {
		const payload = {
			...householdForm.value,
			head: {
				firstName: headForm.value.firstName,
				middleName: headForm.value.middleName,
				lastName: headForm.value.lastName,
				suffix: headForm.value.suffix,
				birthDate: headForm.value.birthDate,
				gender: headForm.value.gender,
				civilStatus: headForm.value.civilStatus,
				occupation: headForm.value.occupation,
				contactNumber: headForm.value.contactNumber,
				email: headForm.value.email
			},
			account: {
				username: accountForm.value.username,
				password: accountForm.value.password,
				roleId: accountForm.value.roleId
			}
		}

		await axiosInstance.post('family/', payload)
		emit('success')
		dialogVisible.value = false
		resetForm()
	} catch (err: unknown) {
		const error = err as { response?: { data?: { message?: string } } }
		submitError.value = error.response?.data?.message || 'Failed to create household. Please try again.'
		console.error('Error creating household:', err)
	} finally {
		isSubmitting.value = false
	}
}

const handleClose = (): void => {
	if (isSubmitting.value) return
	dialogVisible.value = false
	resetForm()
}

watch(() => props.visible, (newVal: boolean): void => {
	if (!newVal) {
		resetForm()
	}
})

const getStepStatus = (stepId: number): 'active' | 'completed' | 'pending' => {
	if (stepId === currentStep.value) return 'active'
	if (stepId < currentStep.value) return 'completed'
	return 'pending'
}

onMounted(() => {
	householdForm.value.code = generateFamilyCode()
})
</script>

<template>
	<div v-if="dialogVisible"
	     class="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-black/50 backdrop-blur-sm"
	     @click.self="handleClose">
		<div class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] flex flex-col md:flex-row overflow-hidden">
			<!-- Desktop -->
			<div class="hidden md:flex w-64 bg-emerald-50/50 border-r border-emerald-100 p-6 flex-col shrink-0 overflow-y-auto">
				<div class="mb-6">
					<h3 class="text-lg font-bold text-gray-800">New Household</h3>
					<p class="text-sm text-gray-500">Complete all steps</p>
				</div>

				<nav class="flex-1 space-y-2">
					<div
							v-for="step in steps"
							:key="step.id"
							class="flex items-start gap-3 p-3 rounded-lg cursor-pointer transition-all duration-200"
							:class="{
                'bg-emerald-50 border border-emerald-200 shadow-sm': getStepStatus(step.id) === 'active',
                'hover:bg-emerald-50/50': getStepStatus(step.id) !== 'active',
                'opacity-50': getStepStatus(step.id) === 'pending'
              }"
							@click="goToStep(step.id)">
						<div
								class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium shrink-0 transition-all duration-200"
								:class="{
                  'bg-emerald-600 text-white ring-4 ring-emerald-100': getStepStatus(step.id) === 'active',
                  'bg-emerald-100 text-emerald-600': getStepStatus(step.id) === 'completed',
                  'bg-gray-200 text-gray-500': getStepStatus(step.id) === 'pending'
                }">
							<i v-if="getStepStatus(step.id) === 'completed'" class="bx bx-check text-lg"></i>
							<span v-else>{{ step.id }}</span>
						</div>
						<div class="flex-1 min-w-0">
							<div class="flex items-center gap-2">
								<p class="text-sm font-medium truncate" :class="{
                  'text-emerald-700': getStepStatus(step.id) === 'active',
                  'text-gray-900': getStepStatus(step.id) === 'completed',
                  'text-gray-400': getStepStatus(step.id) === 'pending'
                }">
									{{ step.title }}
								</p>
								<i v-if="getStepStatus(step.id) === 'active'" class="bx bx-chevron-right text-emerald-600 text-sm"></i>
							</div>
							<p class="text-xs truncate" :class="{
                'text-emerald-600': getStepStatus(step.id) === 'active',
                'text-gray-500': getStepStatus(step.id) === 'completed',
                'text-gray-400': getStepStatus(step.id) === 'pending'
              }">
								{{ step.description }}
							</p>
						</div>
					</div>
				</nav>

				<div class="mt-4 pt-4 border-t border-emerald-100">
					<div class="flex items-center justify-between text-sm">
						<span class="text-gray-500">Progress</span>
						<span class="font-medium text-emerald-600">{{ currentStep }}/{{ totalSteps }}</span>
					</div>
					<div class="mt-2 w-full bg-gray-200 rounded-full h-1.5">
						<div
								class="bg-emerald-600 rounded-full h-1.5 transition-all duration-500"
								:style="{ width: `${(currentStep / totalSteps) * 100}%` }">
						</div>
					</div>
				</div>
			</div>
			<!-- Mobile -->
			<div class="md:hidden bg-emerald-50/50 border-b border-emerald-100 px-3 py-2 shrink-0">
				<div class="flex items-center justify-between mb-1">
					<h3 class="text-sm font-bold text-gray-800">New Household</h3>
					<span class="text-xs font-medium text-emerald-600">{{ currentStep }}/{{ totalSteps }}</span>
				</div>
				<div class="flex items-center gap-1">
					<div
							v-for="step in steps"
							:key="step.id"
							class="flex items-center flex-1"
							:class="{ 'last:flex-none': step.id === steps.length }">
						<div
								class="flex items-center justify-center w-7 h-7 rounded-full text-xs font-medium transition-all duration-200 shrink-0 cursor-pointer"
								:class="{
                  'bg-emerald-600 text-white ring-2 ring-emerald-100': getStepStatus(step.id) === 'active',
                  'bg-emerald-100 text-emerald-600': getStepStatus(step.id) === 'completed',
                  'bg-gray-200 text-gray-500': getStepStatus(step.id) === 'pending'
                }"
								@click="goToStep(step.id)">
							<i v-if="getStepStatus(step.id) === 'completed'" class="bx bx-check text-sm"></i>
							<span v-else>{{ step.id }}</span>
						</div>
						<div
								v-if="step.id < steps.length"
								class="flex-1 h-0.5 mx-1 transition-colors"
								:class="step.id < currentStep ? 'bg-emerald-600' : 'bg-gray-200'">
						</div>
					</div>
				</div>
				<div class="mt-0.5 text-center">
					<p class="text-[10px] text-gray-500">
						{{ steps[currentStep - 1]!!.title }}
					</p>
				</div>
			</div>

			<div class="flex-1 flex flex-col min-w-0 max-h-[90vh] md:max-h-none">
				<div class="hidden md:flex items-center justify-between px-6 py-4 border-b border-gray-200 shrink-0">
					<div>
						<h4 class="text-lg font-semibold text-gray-800">
							{{ steps[currentStep - 1]!!.title }}
						</h4>
						<p class="text-sm text-gray-500">
							{{ steps[currentStep - 1]!!.description }}
						</p>
					</div>
					<button
							@click="handleClose"
							:disabled="isSubmitting"
							class="p-1.5 hover:bg-gray-100 rounded-lg transition-colors text-gray-500 hover:text-gray-700 disabled:opacity-50">
						<i class="bx bx-x text-2xl"></i>
					</button>
				</div>

				<div class="md:hidden flex items-center justify-between px-3 py-2 border-b border-gray-200 shrink-0">
					<h4 class="text-sm font-semibold text-gray-800">
						{{ steps[currentStep - 1]!!.title }}
					</h4>
					<button
							@click="handleClose"
							:disabled="isSubmitting"
							class="p-1 hover:bg-gray-100 rounded-lg transition-colors text-gray-500 hover:text-gray-700 disabled:opacity-50">
						<i class="bx bx-x text-xl"></i>
					</button>
				</div>

				<div class="flex-1 overflow-y-auto px-3 sm:px-4 md:px-6 py-3 sm:py-4">
					<div v-if="submitError"
					     class="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded-lg flex items-center gap-2 mb-3 text-sm">
						<i class="bx bx-error-circle text-lg"></i>
						{{ submitError }}
					</div>

					<form @submit.prevent="handleSubmit" class="space-y-3 sm:space-y-4">
						<div v-show="currentStep === 1" class="space-y-3 sm:space-y-4">
							<div class="grid grid-cols-1 md:grid-cols-2 gap-3 sm:gap-4">
								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Family Code <span class="text-red-500">*</span>
									</label>
									<div class="relative">
										<input
												v-model="householdForm.code"
												type="text"
												class="w-full px-3 py-2 text-sm border rounded-lg bg-gray-50 text-gray-600 cursor-not-allowed"
												readonly
												disabled>
										<i class="bx bx-lock-alt absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
									</div>
									<p class="mt-1 text-xs text-gray-500">
										<i class="bx bx-info-circle mr-1"></i>
										Auto-generated
									</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Family Name <span class="text-red-500">*</span>
									</label>
									<input
											v-model="householdForm.name"
											type="text"
											placeholder="e.g., Eda"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.household.name ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.household.name" class="mt-1 text-xs text-red-500">{{ errors.household.name }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Block Number <span class="text-red-500">*</span>
									</label>
									<input
											v-model="householdForm.blockNumber"
											type="text"
											placeholder="e.g., 972A"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.household.blockNumber ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.household.blockNumber" class="mt-1 text-xs text-red-500">
										{{ errors.household.blockNumber }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Household Number <span class="text-red-500">*</span>
									</label>
									<input
											v-model="householdForm.householdNumber"
											type="text"
											placeholder="e.g., 3501"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.household.householdNumber ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.household.householdNumber" class="mt-1 text-xs text-red-500">
										{{ errors.household.householdNumber }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Barangay <span class="text-red-500">*</span>
									</label>
									<select
											v-model="householdForm.barangay"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.household.barangay ? 'border-red-500' : 'border-gray-300'">
										<option value="">Select Barangay</option>
										<option
												v-for="barangay in barangayStore.barangays"
												:key="barangay"
												:value="barangay">
											{{ barangay }}
										</option>
									</select>
									<p v-if="errors.household.barangay" class="mt-1 text-xs text-red-500">
										{{ errors.household.barangay }}
									</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Landline Number
									</label>
									<input
											v-model="householdForm.landline"
											type="text"
											placeholder="e.g., 333-000-222"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										City <span class="text-red-500">*</span>
									</label>
									<input
											v-model="householdForm.city"
											type="text"
											placeholder="e.g., Gonzaga"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.household.city ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.household.city" class="mt-1 text-xs text-red-500">{{ errors.household.city }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Province <span class="text-red-500">*</span>
									</label>
									<input
											v-model="householdForm.province"
											type="text"
											placeholder="e.g., Cagayan"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.household.province ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.household.province" class="mt-1 text-xs text-red-500">
										{{ errors.household.province }}
									</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Household Type
									</label>
									<select
											v-model="householdForm.householdType"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
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
											v-model="householdForm.housingOwnership"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
										<option
												v-for="option in housingOwnershipOptions"
												:key="option.value"
												:value="option.value">
											{{ option.label }}
										</option>
									</select>
								</div>

								<div class="col-span-1 md:col-span-2">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Registration Status
									</label>
									<select
											v-model="householdForm.registrationStatus"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
										<option
												v-for="option in registrationStatusOptions"
												:key="option.value"
												:value="option.value">
											{{ option.label }}
										</option>
									</select>
								</div>
							</div>
						</div>

						<div v-show="currentStep === 2" class="space-y-3 sm:space-y-4">
							<div class="bg-emerald-50 border border-emerald-200 rounded-lg p-2 sm:p-3">
								<p class="text-xs sm:text-sm text-emerald-800">
									<i class="bx bx-info-circle mr-1"></i>
									Please provide the details of the family head who will be the primary contact.
								</p>
							</div>
							<div class="grid grid-cols-1 md:grid-cols-2 gap-3 sm:gap-4">
								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										First Name <span class="text-red-500">*</span>
									</label>
									<input
											v-model="headForm.firstName"
											type="text"
											placeholder="e.g., Ralph Maron"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.firstName ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.head.firstName" class="mt-1 text-xs text-red-500">{{ errors.head.firstName }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Middle Name
									</label>
									<input
											v-model="headForm.middleName"
											type="text"
											placeholder="e.g., Avila"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Last Name <span class="text-red-500">*</span>
									</label>
									<input
											v-model="headForm.lastName"
											type="text"
											placeholder="e.g., Eda"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.lastName ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.head.lastName" class="mt-1 text-xs text-red-500">{{ errors.head.lastName }}</p>
									<p v-if="householdForm.name && !headForm.lastName" class="mt-1 text-xs text-gray-500">
										<i class="bx bx-info-circle mr-1"></i>
										Will be auto-filled from family name
									</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Suffix
									</label>
									<input
											v-model="headForm.suffix"
											type="text"
											placeholder="e.g., Jr., Sr., III"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Birth Date <span class="text-red-500">*</span>
									</label>
									<input
											v-model="headForm.birthDate"
											type="date"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.birthDate ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.head.birthDate" class="mt-1 text-xs text-red-500">{{ errors.head.birthDate }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Gender <span class="text-red-500">*</span>
									</label>
									<select
											v-model="headForm.gender"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.gender ? 'border-red-500' : 'border-gray-300'">
										<option
												v-for="option in genderOptions"
												:key="option.value"
												:value="option.value">
											{{ option.label }}
										</option>
									</select>
									<p v-if="errors.head.gender" class="mt-1 text-xs text-red-500">{{ errors.head.gender }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Civil Status <span class="text-red-500">*</span>
									</label>
									<select
											v-model="headForm.civilStatus"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.civilStatus ? 'border-red-500' : 'border-gray-300'">
										<option
												v-for="option in civilStatusOptions"
												:key="option.value"
												:value="option.value">
											{{ option.label }}
										</option>
									</select>
									<p v-if="errors.head.civilStatus" class="mt-1 text-xs text-red-500">{{ errors.head.civilStatus }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Occupation
									</label>
									<input
											v-model="headForm.occupation"
											type="text"
											placeholder="e.g., Farmer"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-200 transition-colors">
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Contact Number <span class="text-red-500">*</span>
									</label>
									<input
											v-model="headForm.contactNumber"
											type="text"
											placeholder="e.g., 09123456789"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.contactNumber ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.head.contactNumber" class="mt-1 text-xs text-red-500">
										{{ errors.head.contactNumber }}
									</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Email Address
									</label>
									<input
											v-model="headForm.email"
											type="email"
											placeholder="e.g., ralphmaron@example.com"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.head.email ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.head.email" class="mt-1 text-xs text-red-500">{{ errors.head.email }}</p>
								</div>
							</div>
						</div>

						<div v-show="currentStep === 3" class="space-y-3 sm:space-y-4">
							<div class="bg-amber-50 border border-amber-200 rounded-lg p-2 sm:p-3">
								<p class="text-xs sm:text-sm text-amber-800">
									<i class="bx bx-shield-alt mr-1"></i>
									Create login credentials for the family head to access the system.
								</p>
							</div>
							<div class="grid grid-cols-1 md:grid-cols-2 gap-3 sm:gap-4">
								<div class="col-span-1 md:col-span-2">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Username <span class="text-red-500">*</span>
									</label>
									<input
											v-model="accountForm.username"
											type="text"
											placeholder="e.g., ralphmaron"
											class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
											:class="errors.account.username ? 'border-red-500' : 'border-gray-300'">
									<p v-if="errors.account.username" class="mt-1 text-xs text-red-500">{{ errors.account.username }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Password <span class="text-red-500">*</span>
									</label>
									<div class="relative">
										<input
												v-model="accountForm.password"
												type="password"
												placeholder="Min. 6 characters"
												class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
												:class="errors.account.password ? 'border-red-500' : 'border-gray-300'">
										<i class="bx bx-lock-alt absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
									</div>
									<p v-if="errors.account.password" class="mt-1 text-xs text-red-500">{{ errors.account.password }}</p>
								</div>

								<div class="col-span-1">
									<label class="block text-sm font-medium text-gray-700 mb-1">
										Confirm Password <span class="text-red-500">*</span>
									</label>
									<div class="relative">
										<input
												v-model="accountForm.confirmPassword"
												type="password"
												placeholder="Confirm your password"
												class="w-full px-3 py-2 text-sm border rounded-lg focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 transition-colors"
												:class="errors.account.confirmPassword ? 'border-red-500' : 'border-gray-300'">
										<i class="bx bx-check-shield absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
									</div>
									<p v-if="errors.account.confirmPassword" class="mt-1 text-xs text-red-500">
										{{ errors.account.confirmPassword }}</p>
								</div>
							</div>

							<div class="bg-gray-50 rounded-lg p-3 text-xs text-gray-600 space-y-1">
								<p class="font-medium">Password Requirements:</p>
								<ul class="list-disc list-inside space-y-0.5 ml-2">
									<li>At least 6 characters long</li>
									<li>Include at least one uppercase letter</li>
									<li>Include at least one lowercase letter</li>
									<li>Include at least one number</li>
								</ul>
							</div>
						</div>
					</form>
				</div>

				<div class="flex items-center justify-between px-3 sm:px-4 md:px-6 py-3 sm:py-4 border-t border-gray-200 shrink-0">
					<button
							v-if="currentStep > 1"
							@click="goToPreviousStep"
							:disabled="isSubmitting"
							class="px-3 sm:px-4 py-2 text-xs sm:text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors disabled:opacity-50 flex items-center gap-1">
						<i class="bx bx-chevron-left"></i>
						<span class="hidden xs:inline">Back</span>
					</button>
					<div v-else></div>

					<div class="flex items-center gap-2 sm:gap-3">
						<button
								@click="handleClose"
								:disabled="isSubmitting"
								class="hidden sm:inline-block px-3 sm:px-4 py-2 text-xs sm:text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors disabled:opacity-50">
							Cancel
						</button>

						<button
								v-if="currentStep < totalSteps"
								@click="goToNextStep"
								class="px-3 sm:px-4 py-2 text-xs sm:text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg transition-colors flex items-center gap-1">
							<span>Next</span>
							<i class="bx bx-chevron-right"></i>
						</button>

						<button
								v-else
								@click="handleSubmit"
								:disabled="isSubmitting"
								class="px-3 sm:px-4 py-2 text-xs sm:text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1">
							<i v-if="isSubmitting" class="bx bx-loader-alt animate-spin"></i>
							<span>{{ isSubmitting ? 'Creating...' : 'Create' }}</span>
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
.overflow-y-auto::-webkit-scrollbar {
	width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
	background: #f1f1f1;
	border-radius: 10px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
	background: #c1c1c1;
	border-radius: 10px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
	background: #a8a8a8;
}
</style>