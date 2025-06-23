<!-- admin-ui/src/routes/countries/+page.svelte -->
<script>
	import { onMount } from 'svelte';
	import { API_BASE_URL, buildApiUrl } from '$lib/config/environment.js';

	let countries = [];
	let loading = true;
	let error = null;
	let searchQuery = '';
	let deleteConfirm = { show: false, country: null };

	onMount(async () => {
		await loadCountries();
	});

	async function loadCountries() {
		try {
			loading = true;
			error = null;

			const response = await fetch(buildApiUrl('/api/countries'));
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}

			countries = await response.json();
		} catch (err) {
			console.error('Failed to load countries:', err);
			error = 'Failed to load countries. Make sure the API Gateway and Country Service are running.';
		} finally {
			loading = false;
		}
	}

	async function deleteCountry(id) {
		try {
			const response = await fetch(buildApiUrl(`/api/countries/${id}`), {
				method: 'DELETE'
			});

			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}

			// Remove from local array
			countries = countries.filter(c => c.id !== id);
			deleteConfirm = { show: false, country: null };
		} catch (err) {
			console.error('Failed to delete country:', err);
			error = 'Failed to delete country';
		}
	}

	function showDeleteConfirm(country) {
		deleteConfirm = { show: true, country };
	}

	function hideDeleteConfirm() {
		deleteConfirm = { show: false, country: null };
	}

	$: filteredCountries = countries.filter(country =>
		country.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
		country.capital?.toLowerCase().includes(searchQuery.toLowerCase()) ||
		country.officialName?.toLowerCase().includes(searchQuery.toLowerCase())
	);
</script>

<svelte:head>
	<title>Manage Countries - World Explorer Admin</title>
</svelte:head>

<div class="py-8">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Page Header -->
		<div class="flex justify-between items-center mb-8">
			<div>
				<h1 class="text-3xl font-bold text-gray-900">Countries Management</h1>
				<p class="mt-2 text-gray-600">
					{#if loading}
						Loading countries...
					{:else}
						{countries.length} countries total
					{/if}
				</p>
			</div>
			<a href="/countries/new" class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition cursor-pointer">
				Add New Country
			</a>
		</div>

		<!-- Search Bar -->
		<div class="mb-6">
			<div class="max-w-md">
				<label for="search" class="block text-sm font-medium text-gray-700 mb-2">Search Countries</label>
				<input
					type="text"
					id="search"
					bind:value={searchQuery}
					placeholder="Search by name, capital, or official name..."
					class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent cursor-text"
				/>
			</div>
		</div>

		<!-- Loading State -->
		{#if loading}
			<div class="text-center py-12">
				<div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
				<p class="mt-4 text-gray-600">Loading countries...</p>
			</div>

		<!-- Error State -->
		{:else if error}
			<div class="bg-red-50 border border-red-200 rounded-lg p-6">
				<div class="flex items-center">
					<svg class="h-8 w-8 text-red-400 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
					</svg>
					<div>
						<h3 class="text-lg font-medium text-red-800">Error Loading Countries</h3>
						<p class="text-red-600 mt-1">{error}</p>
						<button on:click={loadCountries} class="mt-3 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded text-sm transition cursor-pointer">
							Try Again
						</button>
					</div>
				</div>
			</div>

		<!-- Empty State -->
		{:else if countries.length === 0}
			<div class="text-center py-12">
				<svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
				</svg>
				<h3 class="mt-4 text-lg font-medium text-gray-900">No countries found</h3>
				<p class="mt-2 text-gray-600">Get started by adding your first country.</p>
				<a href="/countries/new" class="mt-4 inline-block bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition cursor-pointer">
					Add First Country
				</a>
			</div>

		<!-- Countries Table -->
		{:else}
			<div class="bg-white rounded-lg shadow overflow-hidden">
				<table class="min-w-full divide-y divide-gray-200">
					<thead class="bg-gray-50">
						<tr>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Country</th>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Capital</th>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Population</th>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Currency</th>
							<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
						</tr>
					</thead>
					<tbody class="bg-white divide-y divide-gray-200">
						{#each filteredCountries as country (country.id)}
							<tr class="hover:bg-gray-50">
								<td class="px-6 py-4 whitespace-nowrap">
									<div class="flex items-center">
										{#if country.flagUrl}
											<img src={country.flagUrl} alt="{country.name} flag" class="h-6 w-8 mr-3 rounded shadow-sm" />
										{/if}
										<div>
											<div class="text-sm font-medium text-gray-900">{country.name}</div>
											<div class="text-sm text-gray-500">{country.officialName || ''}</div>
										</div>
									</div>
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{country.capital || 'N/A'}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{country.population ? (country.population / 1000000).toFixed(1) + 'M' : 'N/A'}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
									{country.currency || 'N/A'}
								</td>
								<td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
									<div class="flex space-x-2">
										<a href="/countries/{country.id}/edit" class="text-blue-600 hover:text-blue-800 transition cursor-pointer">
											Edit
										</a>
										<button on:click={() => showDeleteConfirm(country)} class="text-red-600 hover:text-red-800 transition cursor-pointer">
											Delete
										</button>
									</div>
								</td>
							</tr>
						{/each}
					</tbody>
				</table>

				{#if filteredCountries.length === 0 && searchQuery}
					<div class="text-center py-8">
						<p class="text-gray-500">No countries match your search for "{searchQuery}"</p>
						<button on:click={() => searchQuery = ''} class="mt-2 text-blue-600 hover:text-blue-800 transition cursor-pointer">
							Clear Search
						</button>
					</div>
				{/if}
			</div>
		{/if}
	</div>
</div>

<!-- Delete Confirmation Modal -->
{#if deleteConfirm.show}
	<div class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
		<div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
			<div class="flex items-center mb-4">
				<svg class="h-8 w-8 text-red-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
				</svg>
				<h3 class="text-lg font-medium text-gray-900">Delete Country</h3>
			</div>
			<p class="text-gray-600 mb-6">
				Are you sure you want to delete <strong>{deleteConfirm.country?.name}</strong>? This action cannot be undone.
			</p>
			<div class="flex justify-end space-x-3">
				<button on:click={hideDeleteConfirm} class="px-4 py-2 text-gray-700 bg-gray-200 hover:bg-gray-300 rounded transition cursor-pointer">
					Cancel
				</button>
				<button on:click={() => deleteCountry(deleteConfirm.country.id)} class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded transition cursor-pointer">
					Delete
				</button>
			</div>
		</div>
	</div>
{/if}