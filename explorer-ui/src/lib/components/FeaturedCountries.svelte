<!-- src/lib/components/FeaturedCountries.svelte -->
<script>
  import { onMount } from 'svelte'
  import CountryCard from './CountryCard.svelte'
  import { availableCountries, countriesLoading, countriesError, countryStore } from '$lib/stores/countryStore.ts'

  // Color gradients for featured countries
  const gradients = [
    { from: 'orange-400', to: 'red-500' },
    { from: 'pink-400', to: 'purple-500' },
    { from: 'blue-400', to: 'indigo-500' },
    { from: 'green-400', to: 'teal-500' },
    { from: 'yellow-400', to: 'orange-500' },
    { from: 'purple-400', to: 'pink-500' }
  ]

  // Get featured countries (first 4 available countries)
  $: featuredCountries = $availableCountries.slice(0, 4).map((country, index) => ({
    country: country.name,
    description: getCountryDescription(country),
    gradientFrom: gradients[index % gradients.length].from,
    gradientTo: gradients[index % gradients.length].to,
    href: `/countries/${country.name.toLowerCase().replace(/\s+/g, '-')}`
  }))

  function getCountryDescription(country) {
    // Generate dynamic descriptions based on country data
    const descriptions = {
      'Portugal': 'Discover stunning beaches, rich history, and warm hospitality in this European gem.',
      'Japan': 'Experience the perfect blend of ancient traditions and cutting-edge modernity.',
      'Spain': 'Explore vibrant culture, incredible cuisine, and passionate people.',
      'France': 'Immerse yourself in art, cuisine, and timeless elegance.',
      'Italy': 'Journey through history, art, and culinary excellence.',
      'Germany': 'Discover efficiency, innovation, and rich cultural heritage.',
      'Brazil': 'Experience vibrant festivals, stunning nature, and warm hospitality.',
      'Canada': 'Explore vast wilderness, friendly people, and multicultural cities.',
      'Australia': 'Discover unique wildlife, stunning landscapes, and laid-back culture.',
      'Norway': 'Experience breathtaking fjords, northern lights, and Nordic charm.'
    }

    return descriptions[country.name] ||
           `Discover the unique culture, stunning landscapes, and rich heritage of ${country.name}.`
  }

  onMount(async () => {
    // Load countries if not already loaded
    if ($availableCountries.length === 0 && !$countriesLoading) {
      await countryStore.loadCountries()
    }
  })
</script>

<section class="py-16 bg-white">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
        Featured Countries
      </h2>
    </div>

    {#if $countriesLoading}
      <div class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Loading featured countries...</p>
      </div>
    {:else if $countriesError}
      <div class="text-center py-12">
        <div class="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md mx-auto">
          <svg class="h-8 w-8 text-red-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
          </svg>
          <h3 class="text-lg font-medium text-red-800 mb-2">Unable to load countries</h3>
          <p class="text-red-600 mb-4">{$countriesError}</p>
          <button
            on:click={() => countryStore.loadCountries()}
            class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded transition"
          >
            Try Again
          </button>
        </div>
      </div>
    {:else if featuredCountries.length === 0}
      <div class="text-center py-12">
        <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
        <h3 class="mt-4 text-lg font-medium text-gray-900">No countries available</h3>
        <p class="mt-2 text-gray-600">Countries will appear here once they are added to the system.</p>
      </div>
    {:else}
      <div class="grid md:grid-cols-2 lg:grid-cols-2 gap-8 mb-8">
        {#each featuredCountries as countryData}
          <CountryCard
            country={countryData.country}
            description={countryData.description}
            gradientFrom={countryData.gradientFrom}
            gradientTo={countryData.gradientTo}
            href={countryData.href}
          />
        {/each}
      </div>
    {/if}

    <!-- View All Countries Button -->
    <div class="text-center">
      <a
        href="/countries"
        class="inline-block bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg text-lg font-semibold transition duration-300 shadow-lg"
      >
        View All Countries ({$availableCountries.length})
      </a>
    </div>
  </div>
</section>