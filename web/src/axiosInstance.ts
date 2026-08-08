import axios from 'axios'

const axiosInstance = axios.create({
	baseURL:  import.meta.env.VITE_BASE_URL || 'http://localhost:3333/api/',
	timeout: 10000,
	headers: {
		'Content-Type': 'application/json'
	}
})

axiosInstance.interceptors.request.use((config) => {
	const accessToken = localStorage.getItem('access_token')

	if (accessToken) {
		config.headers.Authorization = `Bearer ${accessToken}`
	}
	return config
})

export default axiosInstance