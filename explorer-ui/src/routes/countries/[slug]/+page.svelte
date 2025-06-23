<!-- src/routes/countries/[slug]/+page.svelte -->
<script>
  import { onMount } from 'svelte'
  import { page } from '$app/stores'
  import { goto } from '$app/navigation'
  import Hero from '$lib/components/Hero.svelte'
  import FeatureCard from '$lib/components/FeatureCard.svelte'
  import Footer from '$lib/components/Footer.svelte'
  import { countryStore, availableCountries } from '$lib/stores/countryStore.ts'

  let country = null
  let loading = true
  let error = null

  // Get the slug from the URL
  $: slug = $page.params.slug

  // Convert slug back to country name
  $: countryName = slug ? slug.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase()) : ''

  // Watch for changes in available countries or slug
  $: if ($availableCountries.length > 0 && countryName) {
    findCountry()
  }

  function findCountry() {
    loading = true
    error = null

    // Find country by name (case-insensitive)
    const foundCountry = $availableCountries.find(c =>
      c.name.toLowerCase() === countryName.toLowerCase()
    )

    if (foundCountry) {
      country = foundCountry
      loading = false
    } else {
      // Country not found, redirect to countries page after a short delay
      error = `Country "${countryName}" not found`
      loading = false
      setTimeout(() => {
        goto('/countries')
      }, 3000)
    }
  }

  // Generate features based on country data
  $: countryFeatures = country ? generateCountryFeatures(country) : []

  function generateCountryFeatures(countryData) {
    const features = []

    // Geographic features
    if (countryData.area || countryData.capital) {
      features.push({
        icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>',
        title: 'Geography',
        description: `${countryData.capital ? `Capital: ${countryData.capital}. ` : ''}${countryData.area ? `Area: ${countryData.area.toLocaleString()} km². ` : ''}Explore the diverse landscapes and regions.`
      })
    }

    // Population and culture
    if (countryData.population || countryData.officialLanguages?.length) {
      features.push({
        icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" /></svg>',
        title: 'People & Culture',
        description: `${countryData.population ? `Population: ${(countryData.population / 1000000).toFixed(1)}M people. ` : ''}${countryData.officialLanguages?.length ? `Languages: ${countryData.officialLanguages.join(', ')}. ` : ''}Rich cultural heritage and traditions.`
      })
    }

    // Practical information
    if (countryData.currency || countryData.timezone) {
      features.push({
        icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>',
        title: 'Travel Info',
        description: `${countryData.currency ? `Currency: ${countryData.currency}. ` : ''}${countryData.timezone ? `Timezone: ${countryData.timezone}. ` : ''}Essential information for your visit.`
      })
    }

    // If we don't have enough features, add some generic ones
    while (features.length < 3) {
      if (features.length === 0) {
        features.push({
          icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>',
          title: 'Explore',
          description: `Discover the unique attractions, landmarks, and experiences that make ${countryData.name} special.`
        })
      } else if (features.length === 1) {
        features.push({
          icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2-2v12a2 2 0 002 2z" /></svg>',
          title: 'Best Time to Visit',
          description: `Plan your trip to ${countryData.name} with insights on weather, seasons, and local events.`
        })
      } else {
        features.push({
          icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>',
          title: 'Local Experiences',
          description: `Immerse yourself in authentic local experiences, cuisine, and traditions in ${countryData.name}.`
        })
      }
    }

    return features
  }

  onMount(async () => {
    // Load countries if not already loaded
    if ($availableCountries.length === 0) {
      await countryStore.loadCountries()
    } else {
      findCountry()
    }
  })
</script>

<svelte:head>
  {#if country}
    <title>{country.name} Travel Guide - World Explorer</title>
    <meta name="description" content="Comprehensive travel guide to {country.name}. Discover culture, geography, and travel information for your visit to {country.name}." />
  {:else}
    <title>Country Guide - World Explorer</title>
  {/if}
</svelte:head>

{#if loading}
  <div class="min-h-screen flex items-center justify-center">
    <div class="text-center">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
      <p class="text-gray-600">Loading country information...</p>
    </div>
  </div>
{:else if error}
  <div class="min-h-screen flex items-center justify-center">
    <div class="text-center max-w-md">
      <svg class="mx-auto h-16 w-16 text-red-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
      </svg>
      <h1 class="text-2xl font-bold text-gray-900 mb-2">Country Not Found</h1>
      <p class="text-gray-600 mb-4">{error}</p>
      <p class="text-sm text-gray-500">Redirecting to countries page...</p>
    </div>
  </div>
{:else if country}
  <Hero
    title="Discover {country.name}"
    subtitle="{country.officialName ? `Official name: ${country.officialName}. ` : ''}Explore comprehensive travel information, cultural insights, and essential details for your visit to {country.name}."
    primaryButtonText="View All Countries"
    secondaryButtonText="Contact Us"
    featuredCountry={country.name}
    featuredCountryDescription="Comprehensive travel guide and cultural insights."
  />

  <section class="py-16 bg-gray-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-12">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
          About {country.name}
        </h2>
      </div>

      <div class="grid md:grid-cols-3 gap-8">
        {#each countryFeatures as feature}
          <FeatureCard
            icon={feature.icon}
            title={feature.title}
            description={feature.description}
          />
        {/each}
      </div>
    </div>
  </section>

  <section class="py-16 bg-white">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-12">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
          Essential Information
        </h2>
      </div>

      <div class="grid md:grid-cols-2 gap-8 mb-12">
        <div class="bg-gray-50 rounded-xl p-6">
          <h3 class="text-xl font-bold text-gray-900 mb-4">Quick Facts</h3>
          <ul class="space-y-2 text-gray-600">
            {#if country.capital}
              <li><strong>Capital:</strong> {country.capital}</li>
            {/if}
            {#if country.officialLanguages?.length}
              <li><strong>Languages:</strong> {country.officialLanguages.join(', ')}</li>
            {/if}
            {#if country.currency}
              <li><strong>Currency:</strong> {country.currency}</li>
            {/if}
            {#if country.population}
              <li><strong>Population:</strong> ~{(country.population / 1000000).toFixed(1)} million</li>
            {/if}
            {#if country.timezone}
              <li><strong>Time Zone:</strong> {country.timezone}</li>
            {/if}
            {#if country.callingCode}
              <li><strong>Calling Code:</strong> {country.callingCode}</li>
            {/if}
          </ul>
        </div>

        <div class="bg-gray-50 rounded-xl p-6">
          <h3 class="text-xl font-bold text-gray-900 mb-4">Geography</h3>
          <ul class="space-y-2 text-gray-600">
            {#if country.area}
              <li><strong>Area:</strong> {country.area.toLocaleString()} km²</li>
            {/if}
            {#if country.populationDensity}
              <li><strong>Population Density:</strong> {country.populationDensity.toFixed(1)} people/km²</li>
            {/if}
            {#if country.waterPercent}
              <li><strong>Water Coverage:</strong> {country.waterPercent}%</li>
            {/if}
            {#if country.demonym}
              <li><strong>Demonym:</strong> {country.demonym}</li>
            {/if}
            {#if country.isoCode}
              <li><strong>ISO Code:</strong> {country.isoCode}</li>
            {/if}
            {#if country.internetTld}
              <li><strong>Internet TLD:</strong> {country.internetTld}</li>
            {/if}
          </ul>
        </div>
      </div>
    </div>
  </section>

  <section class="py-16 bg-blue-600">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
      <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">
        Plan Your Visit to {country.name}
      </h2>
      <p class="text-xl text-blue-100 mb-8 max-w-3xl mx-auto">
        Ready to explore {country.name}? Discover more countries and start planning your next adventure.
      </p>
      <div class="flex flex-col sm:flex-row gap-4 justify-center">
        <a href="/countries" class="bg-white text-blue-600 hover:bg-gray-100 px-8 py-3 rounded-lg text-lg font-semibold transition duration-300 shadow-lg">
          Explore More Countries
        </a>
        <a href="/contact" class="border-2 border-white text-white hover:bg-white hover:text-blue-600 px-8 py-3 rounded-lg text-lg font-semibold transition duration-300">
          Get Travel Advice
        </a>
      </div>
    </div>
  </section>

  <Footer />
{/if}