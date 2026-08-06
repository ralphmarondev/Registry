export interface Role {
	id: number
	name: string
}

export interface Account {
	id: number
	username: string
	email: string | null
	role: Role
}