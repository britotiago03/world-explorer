<!-- src/routes/countries/+page.svelte -->
<script>
  import { onMount } from 'svelte'
  import Hero from '$lib/components/Hero.svelte'
  import Footer from '$lib/components/Footer.svelte'
  import CountryCard from '$lib/components/CountryCard.svelte'
  import { availableCountries, countriesLoading, countriesError, countryStore } from '$lib/stores/countryStore.ts'

  let searchQuery = ''

  // Color gradients for countries
  const gradients = [
    { from: 'orange-400', to: 'red-500' },
    { from: 'pink-400', to: 'purple-500' },
    { from: 'blue-400', to: 'indigo-500' },
    { from: 'green-400', to: 'teal-500' },
    { from: 'yellow-400', to: 'orange-500' },
    { from: 'purple-400', to: 'pink-500' },
    { from: 'indigo-400', to: 'blue-500' },
    { from: 'red-400', to: 'pink-500' },
    { from: 'teal-400', to: 'green-500' },
    { from: 'gray-400', to: 'gray-600' }
  ]

  // Filter countries based on search query
  $: filteredCountries = $availableCountries.filter(country =>
    country.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    country.capital?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    country.officialName?.toLowerCase().includes(searchQuery.toLowerCase())
  )

  // Map countries to display format
  $: displayCountries = filteredCountries.map((country, index) => ({
    country: country.name,
    description: getCountryDescription(country),
    gradientFrom: gradients[index % gradients.length].from,
    gradientTo: gradients[index % gradients.length].to,
    href: `/countries/${country.name.toLowerCase().replace(/\s+/g, '-')}`,
    capital: country.capital,
    population: country.population
  }))

  function getCountryDescription(country) {
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
      'Norway': 'Experience breathtaking fjords, northern lights, and Nordic charm.',
      'Netherlands': 'Explore windmills, tulips, and progressive culture.',
      'Sweden': 'Discover pristine nature, innovative design, and quality of life.',
      'United Kingdom': 'Journey through royal history, diverse landscapes, and cultural heritage.',
      'United States': 'Experience diverse landscapes, vibrant cities, and the American dream.',
      'Mexico': 'Discover ancient civilizations, vibrant culture, and delicious cuisine.',
      'Argentina': 'Explore tango, wine country, and stunning natural beauty.',
      'Chile': 'Experience diverse geography from deserts to glaciers.',
      'India': 'Immerse yourself in ancient traditions, spices, and spiritual heritage.',
      'China': 'Discover ancient history, modern innovation, and diverse landscapes.',
      'South Korea': 'Experience cutting-edge technology and rich cultural traditions.',
      'Thailand': 'Explore tropical paradise, ancient temples, and incredible cuisine.',
      'Indonesia': 'Discover thousands of islands, diverse cultures, and natural beauty.',
      'Morocco': 'Experience exotic markets, desert landscapes, and rich history.',
      'Egypt': 'Journey through ancient civilizations and timeless monuments.',
      'South Africa': 'Discover diverse wildlife, stunning landscapes, and rich heritage.'
    }

    return descriptions[country.name] ||
           `Discover the unique culture, stunning landscapes, and rich heritage of ${country.name}.`
  }

  function formatPopulation(population) {
    if (!population) return 'Unknown'
    if (population >= 1000000) {
      return `${(population / 1000000).toFixed(1)}M`
    } else if (population >= 1000) {
      return `${(population / 1000).toFixed(1)}K`
    }
    return population.toString()
  }

  onMount(async () => {
    // Load countries if not already loaded
    if ($availableCountries.length === 0 && !$countriesLoading) {
      await countryStore.loadCountries()
    }
  })
</script>

<svelte:head>
  <title>All Countries - World Explorer</title>
  <meta name="description" content="Explore comprehensive travel guides for countries around the world. Find cultural insights, regional information, and travel tips." />
</svelte:head>

<Hero
  title="Explore All Countries"
  subtitle="Comprehensive travel guides for destinations around the world. Discover cultural insights, regional highlights, and the best times to visit."
  primaryButtonText="Featured Destinations"
  secondaryButtonText="About Us"
/>

<section class="py-16 bg-gray-50">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <!-- Search and Statistics -->
    <div class="mb-8">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div class="flex-1 max-w-md">
          <label for="search" class="block text-sm font-medium text-gray-700 mb-2">
            Search Countries
          </label>
          <input
            type="text"
            id="search"
            bind:value={searchQuery}
            placeholder="Search by name, capital, or official name..."
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div class="text-center md:text-right">
          <p class="text-lg font-semibold text-gray-900">
            {#if searchQuery}
              {filteredCountries.length} of {$availableCountries.length} countries
            {:else}
              {$availableCountries.length} countries available
            {/if}
          </p>
          <p class="text-sm text-gray-600">Updated in real-time</p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    {#if $countriesLoading}
      <div class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Loading countries...</p>
      </div>

    <!-- Error State -->
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

    <!-- Empty State -->
    {:else if $availableCountries.length === 0}
      <div class="text-center py-12">
        <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
        <h3 class="mt-4 text-lg font-medium text-gray-900">No countries available</h3>
        <p class="mt-2 text-gray-600">Countries will appear here once they are added to the system.</p>
      </div>

    <!-- No Search Results -->
    {:else if filteredCountries.length === 0 && searchQuery}
      <div class="text-center py-12">
        <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
        </svg>
        <h3 class="mt-4 text-lg font-medium text-gray-900">No countries found</h3>
        <p class="mt-2 text-gray-600">No countries match your search for "{searchQuery}"</p>
        <button
          on:click={() => searchQuery = ''}
          class="mt-4 text-blue-600 hover:text-blue-800 transition"
        >
          Clear search
        </button>
      </div>

    <!-- Countries Grid -->
    {:else}
      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
        {#each displayCountries as countryData}
          <div class="relative">
            <CountryCard
              country={countryData.country}
              description={countryData.description}
              gradientFrom={countryData.gradientFrom}
              gradientTo={countryData.gradientTo}
              href={countryData.href}
            />

            <!-- Country Info Overlay -->
            <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/60 to-transparent p-4 rounded-b-xl">
              <div class="text-white text-sm">
                {#if countryData.capital}
                  <p><strong>Capital:</strong> {countryData.capital}</p>
                {/if}
                {#if countryData.population}
                  <p><strong>Population:</strong> {formatPopulation(countryData.population)}</p>
                {/if}
              </div>
            </div>
          </div>
        {/each}
      </div>
    {/if}
  </div>
</section>

<!-- Real-time Update Indicator -->
<div class="fixed bottom-4 right-4 z-50">
  <div class="bg-green-500 text-white px-3 py-1 rounded-full text-sm flex items-center shadow-lg">
    <div class="w-2 h-2 bg-green-300 rounded-full mr-2 animate-pulse"></div>
    Live Updates
  </div>
</div>

<Footer />