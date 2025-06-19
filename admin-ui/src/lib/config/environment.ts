// admin-ui/src/lib/config/environment.ts

import { browser } from '$app/environment';

// Get API base URL from environment variable or fallback
export const API_BASE_URL = 'http://localhost:3011';

// Helper function to build API URLs
export function buildApiUrl(endpoint: string): string {
    return `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;
}

// Log configuration in development
if (browser && typeof window !== 'undefined') {
    console.log('Admin UI Configuration:', {
        API_BASE_URL,
        NODE_ENV: import.meta.env.MODE
    });
}