import {computed, ref} from 'vue'
import {defineStore} from 'pinia'
import type {Account} from '@/types/accounts'
import axiosInstance from '@/axiosInstance.ts'

export const useAuthStore = defineStore('auth', () => {
	const accessToken = ref<string | null>(null)
	const refreshToken = ref<string | null>(null)
	const account = ref<Account | null>(null)
	const isAuthenticated = computed(() => !!accessToken.value)

	const setTokens = (_accessToken: string, _refreshToken?: string) => {
		accessToken.value = _accessToken
		if (_refreshToken) refreshToken.value = _refreshToken

		localStorage.setItem('access_token', _accessToken)
		if (_refreshToken) localStorage.setItem('refresh_token', _refreshToken)
	}

	const loadTokens = () => {
		accessToken.value = localStorage.getItem('access_token')
		refreshToken.value = localStorage.getItem('refresh_token')
	}

	const fetchMe = async () => {
		const {data} = await axiosInstance.get<Account>('account/me/')
		account.value = data
	}

	const logout = () => {
		accessToken.value = null
		refreshToken.value = null
		account.value = null

		localStorage.removeItem('access_token')
		localStorage.removeItem('refresh_token')
	}

	return {accessToken, refreshToken, account, isAuthenticated, setTokens, loadTokens, fetchMe, logout}
})
