<!-- admin-ui/src/routes/bulk-import/+page.svelte -->
<script>
	import { onMount } from 'svelte';
	import { buildApiUrl } from '$lib/config/environment.js';

	let importing = false;
	let error = null;
	let success = null;
	let importResults = null;
	let jsonData = '';

	const sampleData = [
		{
			"name": "Portugal",
			"officialName": "Portuguese Republic",
			"isoCode": "PT",
			"capital": "Lisbon",
			"demonym": "Portuguese",
			"area": 92090,
			"waterPercent": 0.5,
			"population": 10300000,
			"populationDensity": 111.8,
			"callingCode": "+351",
			"internetTld": ".pt",
			"dateFormat": "dd/mm/yyyy",
			"timezone": "WET (UTC+0)",
			"summerTimezone": "WEST (UTC+1)",
			"currency": "Euro (EUR)",
			"flagUrl": "https://flagcdn.com/w320/pt.png",
			"coatOfArmsUrl": "https://mainfacts.com/media/images/coats_of_arms/pt.png",
			"nativeNames": ["Portugal"],
			"officialLanguages": ["Portuguese"],
			"recognizedLanguages": ["Mirandese"]
		},
		{
			"name": "Japan",
			"officialName": "Japan",
			"isoCode": "JP",
			"capital": "Tokyo",
			"demonym": "Japanese",
			"area": 377975,
			"waterPercent": 0.6,
			"population": 125800000,
			"populationDensity": 332.9,
			"callingCode": "+81",
			"internetTld": ".jp",
			"dateFormat": "yyyy/mm/dd",
			"timezone": "JST (UTC+9)",
			"summerTimezone": "JST (UTC+9)",
			"currency": "Japanese Yen (JPY)",
			"flagUrl": "https://flagcdn.com/w320/jp.png",
			"coatOfArmsUrl": "https://mainfacts.com/media/images/coats_of_arms/jp.png",
			"nativeNames": ["日本", "Nippon"],
			"officialLanguages": ["Japanese"],
			"recognizedLanguages": []
		},
		{
			"name": "Spain",
			"officialName": "Kingdom of Spain",
			"isoCode": "ES",
			"capital": "Madrid",
			"demonym": "Spanish",
			"area": 505992,
			"waterPercent": 1.0,
			"population": 47400000,
			"populationDensity": 93.7,
			"callingCode": "+34",
			"internetTld": ".es",
			"dateFormat": "dd/mm/yyyy",
			"timezone": "CET (UTC+1)",
			"summerTimezone": "CEST (UTC+2)",
			"currency": "Euro (EUR)",
			"flagUrl": "https://flagcdn.com/w320/es.png",
			"coatOfArmsUrl": "https://mainfacts.com/media/images/coats_of_arms/es.png",
			"nativeNames": ["España"],
			"officialLanguages": ["Spanish"],
			"recognizedLanguages": ["Catalan", "Galician", "Basque"]
		}
	];

	function loadSampleData() {
		jsonData = JSON.stringify(sampleData, null, 2);
	}

	async function handleImport() {
		try {
			importing = true;
			error = null;
			success = null;
			importResults = null;

			// Parse JSON data
			let countries;
			try {
				countries = JSON.parse(jsonData);
			} catch (parseError) {
				throw new Error('Invalid JSON format. Please check your data.');
			}

			// Ensure it's an array
			if (!Array.isArray(countries)) {
				countries = [countries];
			}

			// Import each country
			const results = {
				successful: [],
				failed: [],
				total: countries.length
			};

			for (const country of countries) {
				try {
					const response = await fetch(buildApiUrl(`/api/countries/${id}`), {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json'
						},
						body: JSON.stringify(country)
					});

					if (response.ok) {
						const createdCountry = await response.json();
						results.successful.push({
							name: country.name,
							id: createdCountry.id
						});
					} else {
						const errorData = await response.text();
						results.failed.push({
							name: country.name,
							error: `HTTP ${response.status}: ${errorData}`
						});
					}
				} catch (countryError) {
					results.failed.push({
						name: country.name || 'Unknown',
						error: countryError.message
					});
				}
			}

			importResults = results;

			if (results.failed.length === 0) {
				success = `Successfully imported ${results.successful.length} countries!`;
			} else if (results.successful.length === 0) {
				error = `Failed to import all ${results.failed.length} countries.`;
			} else {
				success = `Imported ${results.successful.length} countries successfully. ${results.failed.length} failed.`;
			}

		} catch (err) {
			console.error('Import failed:', err);
			error = `Import failed: ${err.message}`;
		} finally {
			importing = false;
		}
	}

	function clearData() {
		jsonData = '';
		error = null;
		success = null;
		importResults = null;
	}
</script>

<svelte:head>
	<title>Bulk Import - World Explorer Admin</title>
</svelte:head>

<div class="py-8">
	<div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Page Header -->
		<div class="mb-8">
			<nav class="flex mb-4" aria-label="Breadcrumb">
				<ol class="flex items-center space-x-4">
					<li><a href="/" class="text-gray-500 hover:text-gray-700 transition">Dashboard</a></li>
					<li>
						<svg class="h-5 w-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
							<path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
						</svg>
					</li>
					<li><span class="text-gray-900 font-medium">Bulk Import</span></li>
				</ol>
			</nav>
			<h1 class="text-3xl font-bold text-gray-900">Bulk Import Countries</h1>
			<p class="mt-2 text-gray-600">Import multiple countries from JSON data</p>
		</div>

		<div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
			<!-- Import Form -->
			<div class="bg-white rounded-lg shadow p-6">
				<h2 class="text-xl font-semibold text-gray-900 mb-4">Import Data</h2>

				<!-- Instructions -->
				<div class="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
					<div class="flex items-start">
						<svg class="h-5 w-5 text-blue-600 mt-0.5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
						</svg>
						<div>
							<h3 class="text-sm font-medium text-blue-800">Import Instructions</h3>
							<div class="mt-2 text-sm text-blue-700">
								<ul class="list-disc list-inside space-y-1">
									<li>Paste JSON data in the textarea below</li>
									<li>Data should be an array of country objects</li>
									<li>Only "name" field is required for each country</li>
									<li>Use the sample data button to see the expected format</li>
									<li>Existing countries with the same name may cause errors</li>
								</ul>
							</div>
						</div>
					</div>
				</div>

				<!-- Action Buttons -->
				<div class="mb-4 flex space-x-3">
					<button
						on:click={loadSampleData}
						class="px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded text-sm transition"
					>
						Load Sample Data
					</button>
					<button
						on:click={clearData}
						class="px-4 py-2 border border-gray-300 text-gray-700 hover:bg-gray-50 rounded text-sm transition"
					>
						Clear
					</button>
				</div>

				<!-- JSON Input -->
				<div class="mb-6">
					<label for="jsonData" class="block text-sm font-medium text-gray-700 mb-2">
						JSON Data
					</label>
					<textarea
						id="jsonData"
						bind:value={jsonData}
						rows="12"
						class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
						placeholder="Paste your JSON data here..."
					></textarea>
				</div>

				<!-- Success Message -->
				{#if success}
					<div class="mb-4 bg-green-50 border border-green-200 rounded-lg p-4">
						<div class="flex items-center">
							<svg class="h-5 w-5 text-green-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
							</svg>
							<p class="text-green-800 font-medium">{success}</p>
						</div>
					</div>
				{/if}

				<!-- Error Message -->
				{#if error}
					<div class="mb-4 bg-red-50 border border-red-200 rounded-lg p-4">
						<div class="flex items-center">
							<svg class="h-5 w-5 text-red-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
							</svg>
							<p class="text-red-800">{error}</p>
						</div>
					</div>
				{/if}

				<!-- Import Button -->
				<button
					on:click={handleImport}
					disabled={importing || !jsonData.trim()}
					class="w-full px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white rounded-lg font-medium transition flex items-center justify-center"
				>
					{#if importing}
						<svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
						</svg>
						Importing Countries...
					{:else}
						Import Countries
					{/if}
				</button>
			</div>

			<!-- Sample Data & Results -->
			<div class="space-y-8">
				<!-- Sample Data Format -->
				<div class="bg-white rounded-lg shadow p-6">
					<h2 class="text-xl font-semibold text-gray-900 mb-4">Expected JSON Format</h2>
					<div class="bg-gray-50 rounded-lg p-4 font-mono text-sm overflow-x-auto">
						<pre class="text-gray-800">{JSON.stringify([
	{
		"name": "Country Name",
		"officialName": "Official Country Name",
		"isoCode": "XX",
		"capital": "Capital City",
		"demonym": "Nationality",
		"area": 12345,
		"waterPercent": 1.5,
		"population": 1000000,
		"populationDensity": 80.5,
		"callingCode": "+123",
		"internetTld": ".xx",
		"dateFormat": "dd/mm/yyyy",
		"timezone": "UTC+0",
		"summerTimezone": "UTC+1",
		"currency": "Currency Name",
		"flagUrl": "https://example.com/flag.png",
		"coatOfArmsUrl": "https://example.com/coa.png",
		"nativeNames": ["Native Name 1"],
		"officialLanguages": ["Language 1"],
		"recognizedLanguages": ["Language 2"]
	}
], null, 2)}</pre>
					</div>
					<p class="mt-3 text-sm text-gray-600">
						<strong>Note:</strong> Only the "name" field is required. All other fields are optional.
					</p>
				</div>

				<!-- Import Results -->
				{#if importResults}
					<div class="bg-white rounded-lg shadow p-6">
						<h2 class="text-xl font-semibold text-gray-900 mb-4">Import Results</h2>

						<!-- Summary -->
						<div class="grid grid-cols-3 gap-4 mb-6">
							<div class="text-center p-4 bg-blue-50 rounded-lg">
								<div class="text-2xl font-bold text-blue-600">{importResults.total}</div>
								<div class="text-sm text-blue-800">Total</div>
							</div>
							<div class="text-center p-4 bg-green-50 rounded-lg">
								<div class="text-2xl font-bold text-green-600">{importResults.successful.length}</div>
								<div class="text-sm text-green-800">Successful</div>
							</div>
							<div class="text-center p-4 bg-red-50 rounded-lg">
								<div class="text-2xl font-bold text-red-600">{importResults.failed.length}</div>
								<div class="text-sm text-red-800">Failed</div>
							</div>
						</div>

						<!-- Successful Imports -->
						{#if importResults.successful.length > 0}
							<div class="mb-6">
								<h3 class="text-lg font-medium text-green-800 mb-3">✅ Successfully Imported</h3>
								<div class="space-y-2">
									{#each importResults.successful as success}
										<div class="flex items-center justify-between p-3 bg-green-50 border border-green-200 rounded">
											<span class="text-green-800 font-medium">{success.name}</span>
											<span class="text-green-600 text-sm">ID: {success.id}</span>
										</div>
									{/each}
								</div>
							</div>
						{/if}

						<!-- Failed Imports -->
						{#if importResults.failed.length > 0}
							<div>
								<h3 class="text-lg font-medium text-red-800 mb-3">❌ Failed Imports</h3>
								<div class="space-y-2">
									{#each importResults.failed as failure}
										<div class="p-3 bg-red-50 border border-red-200 rounded">
											<div class="font-medium text-red-800">{failure.name}</div>
											<div class="text-red-600 text-sm mt-1">{failure.error}</div>
										</div>
									{/each}
								</div>
							</div>
						{/if}

						<!-- Actions -->
						<div class="mt-6 flex space-x-3">
							<a href="/countries" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded transition">
								View All Countries
							</a>
							<button on:click={clearData} class="px-4 py-2 border border-gray-300 text-gray-700 hover:bg-gray-50 rounded transition">
								Start New Import
							</button>
						</div>
					</div>
				{/if}
			</div>
		</div>

		<!-- Quick Actions -->
		<div class="mt-8 bg-white rounded-lg shadow p-6">
			<h2 class="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
			<div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
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
						<span class="font-medium">Add Single Country</span>
					</div>
					<p class="text-sm text-gray-500 mt-1">Create one country manually</p>
				</a>

				<a href="/" class="block p-4 border border-gray-200 rounded-lg hover:border-gray-500 hover:shadow-md transition">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-gray-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2V7"></path>
						</svg>
						<span class="font-medium">Back to Dashboard</span>
					</div>
					<p class="text-sm text-gray-500 mt-1">Return to admin dashboard</p>
				</a>
			</div>
		</div>
	</div>
</div>