import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/auth.ts'

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: '/',
			name: 'welcome',
			meta: {title: 'Welcome'},
			component: () => import('@/views/welcome/WelcomeIndex.vue')
		},
		{
			path: '/login',
			name: 'login',
			meta: {title: 'Login'},
			component: () => import('@/views/auth/login/LoginIndex.vue')
		},
		{
			path: '/dashboard',
			name: 'dashboard',
			meta: {requiresAuth: true, title: 'Dashboard'},
			component: () => import('@/views/dashboard/DashboardIndex.vue')
		},
		{
			path: '/households',
			name: 'households',
			meta: {requiresAuth: true, title: 'Households'},
			component: () => import('@/views/households/HouseholdIndex.vue')
		},
		{
			path: '/residents',
			name: 'residents',
			meta: {requiresAuth: true, title: 'Residents'},
			component: () => import('@/views/residents/ResidentIndex.vue')
		},
		{
			path: '/reports',
			name: 'reports',
			meta: {requiresAuth: true, title: 'Reports'},
			component: () => import('@/views/reports/ReportsIndex.vue')
		},
		{
			path: '/accounts',
			name: 'accounts',
			meta: {requiresAuth: true, title: 'Accounts'},
			component: () => import('@/views/accounts/AccountIndex.vue')
		}
	]
})

router.beforeEach((to) => {
	const authStore = useAuthStore()
	authStore.loadTokens()

	if (to.meta.requiresAuth && !authStore.accessToken) {
		return {name: 'login'}
	}

	if (to.name === 'login' && authStore.accessToken) {
		return {name: 'dashboard'}
	}

	document.title = `${to.meta.title ?? 'Registry'} | Registry`
})

export default router
