// src/lib/stores/countryStore.ts
import { writable, derived, type Readable } from 'svelte/store'
import { browser } from '$app/environment'
import { buildApiUrl, buildEventsUrl } from '$lib/config/environment'

// Types
export interface Country {
    id: number
    name: string
    officialName?: string
    nativeNames?: string[]
    isoCode?: string
    capital?: string
    demonym?: string
    area?: number
    waterPercent?: number
    population?: number
    populationDensity?: number
    callingCode?: string
    internetTld?: string
    dateFormat?: string
    timezone?: string
    summerTimezone?: string
    currency?: string
    officialLanguages?: string[]
    recognizedLanguages?: string[]
    flagUrl?: string
    coatOfArmsUrl?: string
}

export interface CountryEvent {
    id: number
    eventType: 'COUNTRY_CREATED' | 'COUNTRY_UPDATED' | 'COUNTRY_DELETED'
    source: string
    entityId: number
    data: string
    timestamp: string
    version: string
}

export interface CountryEventData {
    country: Country
    previousCountry?: Country
    action: 'CREATED' | 'UPDATED' | 'DELETED'
    timestamp: number
}

// Store for all countries
export const countries = writable<Country[]>([])

// Store for loading state
export const countriesLoading = writable<boolean>(true)

// Store for error state
export const countriesError = writable<string | null>(null)

// Store for connection status
export const eventConnectionStatus = writable<'connecting' | 'connected' | 'disconnected' | 'error'>('disconnected')

// Derived store for available countries (for navigation)
export const availableCountries: Readable<Country[]> = derived(
    countries,
    ($countries) => $countries.filter(country => country.name && country.name.trim() !== '')
)

// EventSource for real-time updates
let eventSource: EventSource | null = null
let reconnectTimeout: number | null = null
let reconnectAttempts = 0
const maxReconnectAttempts = 5

export const countryStore = {
    // Load initial countries
    async loadCountries(): Promise<void> {
        if (!browser) return

        try {
            countriesLoading.set(true)
            countriesError.set(null)

            const response = await fetch(buildApiUrl('/api/countries'))
            if (!response.ok) {
                const errorMessage = `HTTP error! status: ${response.status}`
                countriesError.set(errorMessage)
                return
            }

            const countriesData: Country[] = await response.json()
            countries.set(countriesData)
            console.log(`Loaded ${countriesData.length} countries`)
        } catch (error) {
            console.error('Failed to load countries:', error)
            const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred'
            countriesError.set(errorMessage)
        } finally {
            countriesLoading.set(false)
        }
    },

    // Subscribe to real-time country updates
    subscribeToUpdates(): void {
        if (!browser || eventSource) return

        try {
            eventConnectionStatus.set('connecting')
            const eventsUrl = buildEventsUrl('/api/countries/events')
            console.log('Connecting to events stream:', eventsUrl)

            eventSource = new EventSource(eventsUrl)

            eventSource.onmessage = (event: MessageEvent) => {
                try {
                    // Handle different types of messages
                    const messageData = JSON.parse(event.data)

                    // Check if this is a connection status message
                    if (messageData.type === 'connection') {
                        console.log('Connection status:', messageData.status)
                        if (messageData.status === 'connected') {
                            eventConnectionStatus.set('connected')
                            reconnectAttempts = 0
                        }
                        return
                    }

                    // Handle country events
                    if (messageData.eventType && messageData.data) {
                        this.handleCountryEvent(messageData as CountryEvent)
                    }
                } catch (error) {
                    console.error('Failed to parse event data:', error, 'Raw data:', event.data)
                }
            }

            eventSource.onerror = (error) => {
                console.error('EventSource connection error:', error)
                eventConnectionStatus.set('error')

                // Auto-reconnect with exponential backoff
                if (reconnectAttempts < maxReconnectAttempts) {
                    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts), 30000)
                    console.log(`Reconnecting in ${delay}ms (attempt ${reconnectAttempts + 1}/${maxReconnectAttempts})`)

                    reconnectTimeout = window.setTimeout(() => {
                        reconnectAttempts++
                        this.reconnect()
                    }, delay)
                } else {
                    console.error('Max reconnection attempts reached')
                    eventConnectionStatus.set('disconnected')
                }
            }

            eventSource.onopen = () => {
                console.log('Connected to country events stream')
                eventConnectionStatus.set('connected')
                reconnectAttempts = 0
            }
        } catch (error) {
            console.error('Failed to connect to events stream:', error)
            eventConnectionStatus.set('error')
        }
    },

    // Handle incoming country events
    handleCountryEvent(event: CountryEvent): void {
        console.log('Received country event:', event.eventType, 'for entity:', event.entityId)

        const { eventType, data } = event

        let eventData: CountryEventData
        try {
            eventData = JSON.parse(data)
        } catch (error) {
            console.error('Failed to parse event data:', error)
            return
        }

        countries.update((currentCountries: Country[]) => {
            switch (eventType) {
                case 'COUNTRY_CREATED':
                    if (eventData.country) {
                        // Add new country if it doesn't exist
                        const exists = currentCountries.some(c => c.id === eventData.country.id)
                        if (!exists) {
                            console.log('Adding new country:', eventData.country.name)
                            return [...currentCountries, eventData.country].sort((a, b) => a.name.localeCompare(b.name))
                        }
                    }
                    break

                case 'COUNTRY_UPDATED':
                    if (eventData.country) {
                        // Update existing country
                        console.log('Updating country:', eventData.country.name)
                        return currentCountries.map(country =>
                            country.id === eventData.country.id ? eventData.country : country
                        ).sort((a, b) => a.name.localeCompare(b.name))
                    }
                    break

                case 'COUNTRY_DELETED':
                    if (eventData.country) {
                        // Remove deleted country
                        console.log('Removing country:', eventData.country.name)
                        return currentCountries.filter(country =>
                            country.id !== eventData.country.id
                        )
                    }
                    break

                default:
                    console.log('Unknown event type:', eventType)
            }

            return currentCountries
        })
    },

    // Reconnect to event stream
    reconnect(): void {
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout)
            reconnectTimeout = null
        }

        if (eventSource) {
            eventSource.close()
            eventSource = null
        }

        // Small delay before reconnecting
        setTimeout(() => {
            this.subscribeToUpdates()
        }, 100)
    },

    // Close event source connection
    disconnect(): void {
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout)
            reconnectTimeout = null
        }

        if (eventSource) {
            eventSource.close()
            eventSource = null
        }

        eventConnectionStatus.set('disconnected')
        reconnectAttempts = 0
    },

    // Get country by name (for routing)
    getCountryByName(name: string): Country | null {
        let result: Country | null = null
        const unsubscribe = countries.subscribe(countriesData => {
            result = countriesData.find(country =>
                country.name?.toLowerCase() === name.toLowerCase()
            ) || null
        })
        unsubscribe()
        return result
    },

    // Get country by ID
    getCountryById(id: number): Country | null {
        let result: Country | null = null
        const unsubscribe = countries.subscribe(countriesData => {
            result = countriesData.find(country => country.id === id) || null
        })
        unsubscribe()
        return result
    },

    // Manual refresh
    async refresh(): Promise<void> {
        await this.loadCountries()
    }
}