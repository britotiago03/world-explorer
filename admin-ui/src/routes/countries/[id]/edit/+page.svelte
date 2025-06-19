<!-- admin-ui/src/routes/countries/[id]/edit/+page.svelte -->
<script>
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { page } from '$app/stores';
	import { buildApiUrl } from '$lib/config/environment.js';

	let loading = true;
	let saving = false;
	let error = null;
	let success = false;

	let formData = {
		id: null,
		name: '',
		officialName: '',
		isoCode: '',
		capital: '',
		demonym: '',
		area: '',
		waterPercent: '',
		population: '',
		populationDensity: '',
		callingCode: '',
		internetTld: '',
		dateFormat: '',
		timezone: '',
		summerTimezone: '',
		currency: '',
		flagUrl: '',
		coatOfArmsUrl: '',
		nativeNames: [''],
		officialLanguages: [''],
		recognizedLanguages: ['']
	};

	$: countryId = $page.params.id;

	onMount(async () => {
		if (countryId) {
			await loadCountry(countryId);
		}
	});

	async function loadCountry(id) {
		try {
			loading = true;
			error = null;

			const response = await fetch(buildApiUrl(`/api/countries/${id}`));
			if (!response.ok) {
				if (response.status === 404) {
					throw new Error('Country not found');
				}
				throw new Error(`HTTP error! status: ${response.status}`);
			}

			const country = await response.json();

			// Populate form data
			formData = {
				id: country.id,
				name: country.name || '',
				officialName: country.officialName || '',
				isoCode: country.isoCode || '',
				capital: country.capital || '',
				demonym: country.demonym || '',
				area: country.area ? country.area.toString() : '',
				waterPercent: country.waterPercent ? country.waterPercent.toString() : '',
				population: country.population ? country.population.toString() : '',
				populationDensity: country.populationDensity ? country.populationDensity.toString() : '',
				callingCode: country.callingCode || '',
				internetTld: country.internetTld || '',
				dateFormat: country.dateFormat || '',
				timezone: country.timezone || '',
				summerTimezone: country.summerTimezone || '',
				currency: country.currency || '',
				flagUrl: country.flagUrl || '',
				coatOfArmsUrl: country.coatOfArmsUrl || '',
				nativeNames: country.nativeNames && country.nativeNames.length > 0 ? country.nativeNames : [''],
				officialLanguages: country.officialLanguages && country.officialLanguages.length > 0 ? country.officialLanguages : [''],
				recognizedLanguages: country.recognizedLanguages && country.recognizedLanguages.length > 0 ? country.recognizedLanguages : ['']
			};

		} catch (err) {
			console.error('Failed to load country:', err);
			error = `Failed to load country: ${err.message}`;
		} finally {
			loading = false;
		}
	}

	async function handleSubmit() {
		try {
			saving = true;
			error = null;

			// Clean up empty arrays
			const cleanedData = {
				...formData,
				area: formData.area ? parseFloat(formData.area) : null,
				waterPercent: formData.waterPercent ? parseFloat(formData.waterPercent) : null,
				population: formData.population ? parseInt(formData.population) : null,
				populationDensity: formData.populationDensity ? parseFloat(formData.populationDensity) : null,
				nativeNames: formData.nativeNames.filter(name => name.trim() !== ''),
				officialLanguages: formData.officialLanguages.filter(lang => lang.trim() !== ''),
				recognizedLanguages: formData.recognizedLanguages.filter(lang => lang.trim() !== '')
			};

			const response = await fetch(buildApiUrl(`/api/countries/${countryId}`), {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(cleanedData)
			});

			if (!response.ok) {
				const errorData = await response.text();
				throw new Error(`HTTP error! status: ${response.status}, message: ${errorData}`);
			}

			success = true;
			setTimeout(() => {
				goto('/countries');
			}, 2000);

		} catch (err) {
			console.error('Failed to update country:', err);
			error = `Failed to update country: ${err.message}`;
		} finally {
			saving = false;
		}
	}

	function addArrayField(fieldName) {
		formData[fieldName] = [...formData[fieldName], ''];
	}

	function removeArrayField(fieldName, index) {
		formData[fieldName] = formData[fieldName].filter((_, i) => i !== index);
	}

	function updateArrayField(fieldName, index, value) {
		formData[fieldName][index] = value;
	}
</script>

<svelte:head>
	<title>Edit Country - World Explorer Admin</title>
</svelte:head>

<div class="py-8">
	<div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Loading State -->
		{#if loading}
			<div class="text-center py-12">
				<div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
				<p class="mt-4 text-gray-600">Loading country...</p>
			</div>
		{:else if error && !formData.id}
			<!-- Error loading country -->
			<div class="bg-red-50 border border-red-200 rounded-lg p-6">
				<div class="flex items-center">
					<svg class="h-8 w-8 text-red-400 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
					</svg>
					<div>
						<h3 class="text-lg font-medium text-red-800">Error Loading Country</h3>
						<p class="text-red-600 mt-1">{error}</p>
						<div class="mt-3 space-x-3">
							<button on:click={() => loadCountry(countryId)} class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded text-sm transition">
								Try Again
							</button>
							<a href="/countries" class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded text-sm transition">
								Back to Countries
							</a>
						</div>
					</div>
				</div>
			</div>
		{:else}
			<!-- Page Header -->
			<div class="mb-8">
				<nav class="flex mb-4" aria-label="Breadcrumb">
					<ol class="flex items-center space-x-4">
						<li><a href="/countries" class="text-gray-500 hover:text-gray-700 transition">Countries</a></li>
						<li>
							<svg class="h-5 w-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
								<path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
							</svg>
						</li>
						<li><span class="text-gray-900 font-medium">Edit {formData.name}</span></li>
					</ol>
				</nav>
				<h1 class="text-3xl font-bold text-gray-900">Edit Country</h1>
				<p class="mt-2 text-gray-600">Update the information for {formData.name}</p>
			</div>

			<!-- Success Message -->
			{#if success}
				<div class="mb-6 bg-green-50 border border-green-200 rounded-lg p-4">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-green-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
						</svg>
						<p class="text-green-800 font-medium">Country updated successfully! Redirecting...</p>
					</div>
				</div>
			{/if}

			<!-- Error Message -->
			{#if error && formData.id}
				<div class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
					<div class="flex items-center">
						<svg class="h-6 w-6 text-red-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
						</svg>
						<p class="text-red-800">{error}</p>
					</div>
				</div>
			{/if}

			<!-- Form -->
			<form on:submit|preventDefault={handleSubmit} class="bg-white rounded-lg shadow p-6">
				<!-- Basic Information -->
				<div class="mb-8">
					<h2 class="text-xl font-semibold text-gray-900 mb-4">Basic Information</h2>
					<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
						<div>
							<label for="name" class="block text-sm font-medium text-gray-700 mb-2">Country Name *</label>
							<input
								type="text"
								id="name"
								bind:value={formData.name}
								required
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., Portugal"
							/>
						</div>

						<div>
							<label for="officialName" class="block text-sm font-medium text-gray-700 mb-2">Official Name</label>
							<input
								type="text"
								id="officialName"
								bind:value={formData.officialName}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., Portuguese Republic"
							/>
						</div>

						<div>
							<label for="isoCode" class="block text-sm font-medium text-gray-700 mb-2">ISO Code</label>
							<input
								type="text"
								id="isoCode"
								bind:value={formData.isoCode}
								maxlength="3"
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., PT"
							/>
						</div>

						<div>
							<label for="capital" class="block text-sm font-medium text-gray-700 mb-2">Capital</label>
							<input
								type="text"
								id="capital"
								bind:value={formData.capital}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., Lisbon"
							/>
						</div>

						<div>
							<label for="demonym" class="block text-sm font-medium text-gray-700 mb-2">Demonym</label>
							<input
								type="text"
								id="demonym"
								bind:value={formData.demonym}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., Portuguese"
							/>
						</div>

						<div>
							<label for="currency" class="block text-sm font-medium text-gray-700 mb-2">Currency</label>
							<input
								type="text"
								id="currency"
								bind:value={formData.currency}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., Euro (EUR)"
							/>
						</div>
					</div>
				</div>

				<!-- Geographic Information -->
				<div class="mb-8">
					<h2 class="text-xl font-semibold text-gray-900 mb-4">Geographic Information</h2>
					<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
						<div>
							<label for="area" class="block text-sm font-medium text-gray-700 mb-2">Area (km²)</label>
							<input
								type="number"
								id="area"
								bind:value={formData.area}
								step="0.01"
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., 92090"
							/>
						</div>

						<div>
							<label for="waterPercent" class="block text-sm font-medium text-gray-700 mb-2">Water Percentage</label>
							<input
								type="number"
								id="waterPercent"
								bind:value={formData.waterPercent}
								step="0.01"
								min="0"
								max="100"
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., 0.5"
							/>
						</div>

						<div>
							<label for="population" class="block text-sm font-medium text-gray-700 mb-2">Population</label>
							<input
								type="number"
								id="population"
								bind:value={formData.population}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., 10300000"
							/>
						</div>

						<div>
							<label for="populationDensity" class="block text-sm font-medium text-gray-700 mb-2">Population Density (per km²)</label>
							<input
								type="number"
								id="populationDensity"
								bind:value={formData.populationDensity}
								step="0.01"
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., 111.8"
							/>
						</div>
					</div>
				</div>

				<!-- Contact Information -->
				<div class="mb-8">
					<h2 class="text-xl font-semibold text-gray-900 mb-4">Contact & Technical Information</h2>
					<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
						<div>
							<label for="callingCode" class="block text-sm font-medium text-gray-700 mb-2">Calling Code</label>
							<input
								type="text"
								id="callingCode"
								bind:value={formData.callingCode}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., +351"
							/>
						</div>

						<div>
							<label for="internetTld" class="block text-sm font-medium text-gray-700 mb-2">Internet TLD</label>
							<input
								type="text"
								id="internetTld"
								bind:value={formData.internetTld}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., .pt"
							/>
						</div>

						<div>
							<label for="dateFormat" class="block text-sm font-medium text-gray-700 mb-2">Date Format</label>
							<input
								type="text"
								id="dateFormat"
								bind:value={formData.dateFormat}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., dd/mm/yyyy"
							/>
						</div>

						<div>
							<label for="timezone" class="block text-sm font-medium text-gray-700 mb-2">Timezone</label>
							<input
								type="text"
								id="timezone"
								bind:value={formData.timezone}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., WET (UTC+0)"
							/>
						</div>

						<div class="md:col-span-2">
							<label for="summerTimezone" class="block text-sm font-medium text-gray-700 mb-2">Summer Timezone</label>
							<input
								type="text"
								id="summerTimezone"
								bind:value={formData.summerTimezone}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="e.g., WEST (UTC+1)"
							/>
						</div>
					</div>
				</div>

				<!-- Media URLs -->
				<div class="mb-8">
					<h2 class="text-xl font-semibold text-gray-900 mb-4">Media & Symbols</h2>
					<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
						<div>
							<label for="flagUrl" class="block text-sm font-medium text-gray-700 mb-2">Flag URL</label>
							<input
								type="url"
								id="flagUrl"
								bind:value={formData.flagUrl}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="https://example.com/flag.png"
							/>
							{#if formData.flagUrl}
								<img src={formData.flagUrl} alt="Flag preview" class="mt-2 h-8 w-12 rounded shadow-sm" on:error={() => {}} />
							{/if}
						</div>

						<div>
							<label for="coatOfArmsUrl" class="block text-sm font-medium text-gray-700 mb-2">Coat of Arms URL</label>
							<input
								type="url"
								id="coatOfArmsUrl"
								bind:value={formData.coatOfArmsUrl}
								class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								placeholder="https://example.com/coat-of-arms.png"
							/>
							{#if formData.coatOfArmsUrl}
								<img src={formData.coatOfArmsUrl} alt="Coat of arms preview" class="mt-2 h-12 w-12 rounded shadow-sm" on:error={() => {}} />
							{/if}
						</div>
					</div>
				</div>

				<!-- Languages -->
				<div class="mb-8">
					<h2 class="text-xl font-semibold text-gray-900 mb-4">Languages</h2>

					<!-- Native Names -->
					<div class="mb-6">
						<label class="block text-sm font-medium text-gray-700 mb-2">Native Names</label>
						{#each formData.nativeNames as name, index (index)}
							<div class="flex items-center mb-2">
								<input
									type="text"
									bind:value={name}
									on:input={(e) => updateArrayField('nativeNames', index, e.target.value)}
									class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
									placeholder="Native name"
								/>
								{#if formData.nativeNames.length > 1}
									<button type="button" on:click={() => removeArrayField('nativeNames', index)} class="ml-2 text-red-600 hover:text-red-800">
										<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
										</svg>
									</button>
								{/if}
							</div>
						{/each}
						<button type="button" on:click={() => addArrayField('nativeNames')} class="text-blue-600 hover:text-blue-800 text-sm">
							+ Add Native Name
						</button>
					</div>

					<!-- Official Languages -->
					<div class="mb-6">
						<label class="block text-sm font-medium text-gray-700 mb-2">Official Languages</label>
						{#each formData.officialLanguages as language, index (index)}
							<div class="flex items-center mb-2">
								<input
									type="text"
									bind:value={language}
									on:input={(e) => updateArrayField('officialLanguages', index, e.target.value)}
									class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
									placeholder="Official language"
								/>
								{#if formData.officialLanguages.length > 1}
									<button type="button" on:click={() => removeArrayField('officialLanguages', index)} class="ml-2 text-red-600 hover:text-red-800">
										<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
										</svg>
									</button>
								{/if}
							</div>
						{/each}
						<button type="button" on:click={() => addArrayField('officialLanguages')} class="text-blue-600 hover:text-blue-800 text-sm">
							+ Add Official Language
						</button>
					</div>

					<!-- Recognized Languages -->
					<div class="mb-6">
						<label class="block text-sm font-medium text-gray-700 mb-2">Recognized Languages</label>
						{#each formData.recognizedLanguages as language, index (index)}
							<div class="flex items-center mb-2">
								<input
									type="text"
									bind:value={language}
									on:input={(e) => updateArrayField('recognizedLanguages', index, e.target.value)}
									class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
									placeholder="Recognized language"
								/>
								{#if formData.recognizedLanguages.length > 1}
									<button type="button" on:click={() => removeArrayField('recognizedLanguages', index)} class="ml-2 text-red-600 hover:text-red-800">
										<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
										</svg>
									</button>
								{/if}
							</div>
						{/each}
						<button type="button" on:click={() => addArrayField('recognizedLanguages')} class="text-blue-600 hover:text-blue-800 text-sm">
							+ Add Recognized Language
						</button>
					</div>
				</div>

				<!-- Form Actions -->
				<div class="flex justify-end space-x-4 pt-6 border-t border-gray-200">
					<a href="/countries" class="px-6 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition">
						Cancel
					</a>
					<button
						type="submit"
						disabled={saving || !formData.name.trim()}
						class="px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white rounded-lg font-medium transition flex items-center"
					>
						{#if saving}
							<svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
							</svg>
							Updating...
						{:else}
							Update Country
						{/if}
					</button>
				</div>
			</form>
		{/if}
	</div>
</div>