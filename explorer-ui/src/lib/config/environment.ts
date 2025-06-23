// explorer-ui/src/lib/config/environment.ts

import { browser } from '$app/environment';

// Get API base URL - hardcoded to match docker-compose.yml
export const API_BASE_URL = 'http://localhost:3011';

// Helper function to build API URLs
export function buildApiUrl(endpoint: string): string {
    const cleanEndpoint = endpoint.startsWith('/') ? endpoint : '/' + endpoint;
    return `${API_BASE_URL}${cleanEndpoint}`;
}

// WebSocket/EventSource URL for real-time updates
export const EVENTS_BASE_URL = API_BASE_URL;

export function buildEventsUrl(endpoint: string): string {
    const cleanEndpoint = endpoint.startsWith('/') ? endpoint : '/' + endpoint;
    return `${EVENTS_BASE_URL}${cleanEndpoint}`;
}

// Log configuration in development
if (browser && typeof window !== 'undefined') {
    console.log('Explorer UI Configuration:', {
        API_BASE_URL,
        EVENTS_BASE_URL,
        NODE_ENV: import.meta.env.MODE
    });
}