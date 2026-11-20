import type { CurrentUser } from '@/types/auth'

let currentUser: CurrentUser | null = null

export function setCurrentUser(user: CurrentUser): void {
  currentUser = user
}

export function getCurrentUser(): CurrentUser | null {
  return currentUser
}

export function clearCurrentUser(): void {
  currentUser = null
}
