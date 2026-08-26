<script setup lang="ts">
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/auth.ts'
import axiosInstance from '@/axiosInstance.ts'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isError = ref(false)
const isLoggingIn = ref(false)

const authStore = useAuthStore()
const router = useRouter()

const login = async () => {
	if (isLoggingIn.value) return

	isLoggingIn.value = true
	isError.value = false
	errorMessage.value = ''

	try {
		const response = await axiosInstance.post('account/login/', {
			username: username.value.trim(),
			password: password.value.trim()
		})
		const {accessToken, refreshToken, account} = response.data
		authStore.setTokens(accessToken, refreshToken)
		authStore.setAccount(account)
		await router.push({name: 'dashboard'})
	} catch (e: any) {
		if (e.response?.data?.message) {
			errorMessage.value = e.response.data.message
		} else {
			errorMessage.value = 'Login failed. Please try again later.'
		}
		isError.value = true
	} finally {
		isLoggingIn.value = false
	}
}
</script>

<template>
	<div class="min-h-screen flex flex-col bg-linear-to-br from-emerald-50 via-white to-green-50">
		<main class="flex-1 flex items-center justify-center px-6">
			<div class="w-full max-w-sm">
				<div class="text-center mb-8">
					<router-link to="/" class="inline-block hover:opacity-80 transition-opacity">
						<img src="/favicon.png" alt="Logo" class="w-16 h-16 mx-auto mb-4">
					</router-link>
					<h2 class="text-2xl font-bold text-gray-800 mb-2">
						Log in to your account
					</h2>
					<p class="text-sm text-gray-500">
						Enter your username and password below to log in.
					</p>
				</div>

				<!-- Error Alert -->
				<div v-if="isError" class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
					{{ errorMessage }}
				</div>

				<form @submit.prevent="login" class="space-y-4">
					<div>
						<label class="block text-sm font-medium text-gray-700 mb-1.5">
							Username
						</label>
						<input
								v-model="username"
								type="text"
								placeholder="Username"
								class="w-full px-4 py-2.5 bg-emerald-50/50 border border-emerald-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all duration-200"
								required>
					</div>
					<div>
						<div class="flex items-center justify-between mb-1.5">
							<label class="block text-sm font-medium text-gray-700">
								Password
							</label>
						</div>
						<input
								v-model="password"
								type="password"
								placeholder="Password"
								class="w-full px-4 py-2.5 bg-emerald-50/50 border border-emerald-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all duration-200"
								required>
					</div>
					<button
							type="submit"
							class="w-full py-2.5 bg-linear-to-r from-emerald-600 to-green-500 text-white font-medium rounded-lg hover:shadow-lg hover:shadow-emerald-500/30 hover:scale-[1.02] transition-all duration-300 disabled:opacity-70 disabled:cursor-not-allowed disabled:hover:scale-100 mt-2"
							:disabled="isLoggingIn">
						<span v-if="!isLoggingIn">
							Log in
						</span>
						<span v-else>
							Logging in...
						</span>
					</button>
				</form>
			</div>
		</main>
	</div>
</template>