// utils/serviceMap.ts

export function resolveService(path: string): string | undefined {
    // Use container names for Docker networking
    if (path.startsWith('/api/countries')) return 'http://country-service:3020'
    // Add others as needed

    return undefined
}