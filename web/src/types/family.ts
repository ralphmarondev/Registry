export interface Family {
	id: number
	code: string
	name: string
	head: string
	members: number
	status: 'Active' | 'Inactive'
	barangay: string
	createdAt: string
	address: string
	contact: string
}