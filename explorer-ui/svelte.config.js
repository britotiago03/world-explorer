import { mdsvex } from 'mdsvex';
import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	preprocess: [vitePreprocess(), mdsvex()],

	kit: {
		adapter: adapter(),
		prerender: {
			handleHttpError: ({ path, message }) => {
				// Don't throw errors for missing pages during build
				if (path.startsWith('/countries/') || path === '/about') {
					console.warn(`Warning: ${message} (${path})`);
					return;
				}
				throw new Error(message);
			}
		}
	},

	extensions: ['.svelte', '.svx']
};

export default config;