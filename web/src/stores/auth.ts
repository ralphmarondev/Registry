import {computed, ref} from 'vue'
import {defineStore} from 'pinia'

export const useAuthStore = defineStore('auth', () => {
	const accessToken = ref<string | null>(
		localStorage.getItem('access_token')
	)

	const refreshToken = ref<string | null>(
		localStorage.getItem('refresh_token')
	)

	const username = ref<string | null>(
		localStorage.getItem('username')
	)

	const role = ref<string | null>(
		localStorage.getItem('role')
	)

	const isAuthenticated = computed(() => !!accessToken.value)

	const setTokens = (
		_accessToken: string,
		_refreshToken?: string
	) => {
		accessToken.value = _accessToken

		localStorage.setItem('access_token', _accessToken)

		if (_refreshToken) {
			refreshToken.value = _refreshToken
			localStorage.setItem('refresh_token', _refreshToken)
		}
	}
	const setAccount = (
		_username: string,
		_role: string
	) => {
		username.value = _username
		role.value = _role

		localStorage.setItem('username', _username)
		localStorage.setItem('role', _role)
	}

	const logout = () => {
		accessToken.value = null
		refreshToken.value = null
		username.value = null
		role.value = null

		localStorage.removeItem('access_token')
		localStorage.removeItem('refresh_token')
		localStorage.removeItem('username')
		localStorage.removeItem('role')
	}

	return {
		accessToken,
		refreshToken,
		username,
		role,
		isAuthenticated,
		setTokens,
		setAccount,
		logout
	}
})