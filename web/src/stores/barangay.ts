import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useBarangayStore = defineStore('barangay', () => {
	const barangays = ref<string[]>([
		'Amunitan',
		'Batangan',
		'Baua',
		'Cabanban Norte',
		'Cabanban Sur',
		'Cabiraoan',
		'Calayan',
		'Callao',
		'Caroan',
		'Casitan',
		'Flourishing',
		'Ipil',
		'Isca',
		'Magrafil',
		'Minanga',
		'Paradise',
		'Pateng',
		'Progressive',
		'Rebecca Nababacalan',
		'San Jose',
		'Santa Clara',
		'Santa Cruz',
		'Santa Maria',
		'Smart',
		'Tapel'
	])
	return {barangays}
})