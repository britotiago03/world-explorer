// utils/serviceMap.ts

export function resolveService(path: string): string | undefined {
    if (path.startsWith('/order')) return 'http://localhost:3010'
    if (path.startsWith('/payment')) return 'http://localhost:3011'
    if (path.startsWith('/project')) return 'http://localhost:3012'
    if (path.startsWith('/contract')) return 'http://localhost:3013'
    if (path.startsWith('/monitor')) return 'http://localhost:3014'
    // Add others as needed

    return undefined
}
