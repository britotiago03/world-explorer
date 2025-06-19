<!-- admin-ui/src/routes/+page.svelte -->
<script>
	import { onMount } from 'svelte';
	import { API_BASE_URL, buildApiUrl } from '$lib/config/environment.js';

	let stats = {
		totalCountries: 0,
		loading: true,
		error: null
	};

	onMount(async () => {
		await loadStats();
	});

	async function loadStats() {
		try {
			stats.loading = true;
			stats.error = null;

			const response = await fetch(buildApiUrl('/api/countries'));
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}

			const countries = await response.json();
			stats.totalCountries = countries.length;
		} catch (error) {
			console.error('Failed to load stats:', error);
			stats.error = 'Failed to load statistics';
		} finally {
			stats.loading = false;
		}
	}
</script>

<svelte:head>
	<title>Admin Dashboard - World Explorer</title>
</svelte:head>

<div class="py-8">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Page Header -->
		<div class="mb-8">
			<h1 class="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
			<p class="mt-2 text-gray-600">Manage countries data for World Explorer</p>
			<p class="text-sm text-gray-500">API: {API_BASE_URL}</p>
		</div>

		<!-- Statistics Cards -->
		<div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
			<!-- Total Countries Card -->
			<div class="bg-white rounded-lg shadow p-6">
				<div class="flex items-center">
					<div class="flex-shrink-0">
						<svg class="h-8 w-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
						</svg>
					</div>
					<div class="ml-5 w-0 flex-1">
						<dl>
							<dt class="text-sm font-medium text-gray-500 truncate">Total Countries</dt>
							<dd class="text-lg font-medium text-gray-900">
								{#if stats.loading}
									<div class="animate-pulse bg-gray-200 h-6 w-12 rounded"></div>
								{:else if stats.error}
									<span class="text-red-600">Error</span>
								{:else}
									{stats.totalCountries}
								{/if}
							</dd>
						</dl>
					</div>
				</div>
			</div>

			<!-- Quick Add Card -->
			<div class="bg-white rounded-lg shadow p-6">
				<div class="flex items-center">
					<div class="flex-shrink-0">
						<svg class="h-8 w-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
						</svg>
					</div>
					<div class="ml-5 w-0 flex-1">
						<dl>
							<dt class="text-sm font-medium text-gray-500 truncate">Quick Actions</dt>
							<dd class="text-lg font-medium text-gray-900">
								<a href="/countries/new" class="text-green-600 hover:text-green-800 transition">
									Add Country
								</a>
							</dd>
						</dl>
					</div>
				</div>
			</div>

			<!-- Import Data Card -->
			<div class="bg-white rounded-lg shadow p-6">
				<div class="flex items-center">
					<div class="flex-shrink-0">
						<svg class="h-8 w-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"></path>
						</svg>
					</div>
					<div class="ml-5 w-0 flex-1">
						<dl>
							<dt class="text-sm font-medium text-gray-500 truncate">Data Management</dt>
							<dd class="text-lg font-medium text-gray-900">
								<a href="/bulk-import" class="text-purple-600 hover:text-purple-800 transition">
									Bulk Import
								</a>
							</dd>
						</dl>
					</div>
				</div>
			</div>
		</div>

		<!-- Quick Actions -->
		<div class="bg-white rounded-lg shadow p-6">
			<h2 class="text-lg font-medium text-gray-900 mb-4">Quick Actions</h2>
			<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
				<a href="/countries" class="block p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:shadow-md transition">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-blue-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
						</svg>
						<span class="font-medium">View All Countries</span>
					</div>
					<p class="text-sm text-gray-500 mt-1">Browse and manage existing countries</p>
				</a>

				<a href="/countries/new" class="block p-4 border border-gray-200 rounded-lg hover:border-green-500 hover:shadow-md transition">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-green-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
						</svg>
						<span class="font-medium">Add New Country</span>
					</div>
					<p class="text-sm text-gray-500 mt-1">Create a new country entry</p>
				</a>

				<a href="/bulk-import" class="block p-4 border border-gray-200 rounded-lg hover:border-purple-500 hover:shadow-md transition">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-purple-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10"></path>
						</svg>
						<span class="font-medium">Bulk Import</span>
					</div>
					<p class="text-sm text-gray-500 mt-1">Import multiple countries from JSON</p>
				</a>

				<button on:click={loadStats} class="block p-4 border border-gray-200 rounded-lg hover:border-gray-500 hover:shadow-md transition text-left">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-gray-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
						</svg>
						<span class="font-medium">Refresh Stats</span>
					</div>
					<p class="text-sm text-gray-500 mt-1">Update dashboard statistics</p>
				</button>
			</div>
		</div>

		<!-- Status Messages -->
		{#if stats.error}
			<div class="mt-6 bg-red-50 border border-red-200 rounded-md p-4">
				<div class="flex">
					<svg class="h-5 w-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
					</svg>
					<div class="ml-3">
						<h3 class="text-sm font-medium text-red-800">Connection Error</h3>
						<p class="text-sm text-red-700 mt-1">{stats.error}</p>
						<p class="text-sm text-red-600 mt-1">Make sure the API Gateway and Country Service are running at {API_BASE_URL}</p>
					</div>
				</div>
			</div>
		{/if}
	</div>
</div>