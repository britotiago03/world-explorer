<!-- src/lib/components/Navigation.svelte -->
<script>
  import { onMount, onDestroy } from 'svelte'
  import { availableCountries, countriesLoading, eventConnectionStatus, countryStore } from '$lib/stores/countryStore.ts'

  export let currentPage = 'home'

  let showCountriesDropdown = false
  let dropdownTimeout

  const navItems = [
    { name: 'Home', href: '/', key: 'home' },
    { name: 'Countries', href: '/countries', key: 'countries', hasDropdown: true },
    { name: 'About', href: '/about', key: 'about' }
  ]

  // Reactive statement for countries - will update when store changes
  $: countries = $availableCountries.slice(0, 10).map(country => ({
    name: country.name,
    href: `/countries/${country.name.toLowerCase().replace(/\s+/g, '-')}`
  }))

  // Add "More countries..." link if there are more than 10
  $: displayCountries = $availableCountries.length > 10
    ? [...countries, { name: 'View all countries...', href: '/countries', disabled: false }]
    : countries

  // Determine what to show in dropdown
  $: dropdownContent = getDropdownContent($countriesLoading, $availableCountries, displayCountries)

  function getDropdownContent(loading, availableCountries, displayCountries) {
    if (loading) {
      return {
        type: 'loading',
        message: 'Loading countries...'
      }
    }

    if (availableCountries.length === 0) {
      return {
        type: 'empty',
        message: 'No countries available at the moment'
      }
    }

    return {
      type: 'countries',
      countries: displayCountries
    }
  }

  onMount(async () => {
    // Load countries and subscribe to updates
    await countryStore.loadCountries()
    countryStore.subscribeToUpdates()
  })

  onDestroy(() => {
    // Clean up event source connection
    countryStore.disconnect()
  })

  function showDropdown() {
    clearTimeout(dropdownTimeout)
    showCountriesDropdown = true
  }

  function hideDropdown() {
    dropdownTimeout = setTimeout(() => {
      showCountriesDropdown = false
    }, 150)
  }

  function keepDropdown() {
    clearTimeout(dropdownTimeout)
  }

  // Keyboard handling for accessibility
  function handleKeydown(event) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      showCountriesDropdown = !showCountriesDropdown
    } else if (event.key === 'Escape') {
      showCountriesDropdown = false
    }
  }

  // Debug connection status
  $: if ($eventConnectionStatus) {
    console.log('Event connection status:', $eventConnectionStatus)
  }
</script>

<header class="bg-white shadow-sm border-b">
  <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="flex justify-between items-center h-16">
      <!-- Logo -->
      <div class="flex-shrink-0">
        <a href="/" class="text-xl font-bold text-blue-600">
          World Explorer
          <!-- Connection status indicator (only visible in dev) -->
          {#if $eventConnectionStatus === 'connected'}
            <span class="ml-2 text-xs text-green-600 hidden md:inline">●</span>
          {:else if $eventConnectionStatus === 'connecting'}
            <span class="ml-2 text-xs text-yellow-600 hidden md:inline">●</span>
          {:else if $eventConnectionStatus === 'error'}
            <span class="ml-2 text-xs text-red-600 hidden md:inline">●</span>
          {/if}
        </a>
      </div>

      <!-- Navigation Links -->
      <div class="hidden md:block">
        <div class="ml-10 flex items-baseline space-x-8">
          {#each navItems as item}
            <div class="relative">
              {#if item.hasDropdown}
                <div
                  on:mouseenter={showDropdown}
                  on:mouseleave={hideDropdown}
                  on:keydown={handleKeydown}
                  class="relative"
                  role="button"
                  tabindex="0"
                >
                  <a
                    href={item.href}
                    class="text-gray-500 hover:text-gray-700 px-3 py-2 text-sm font-medium flex items-center"
                    class:text-blue-600={currentPage === item.key}
                    class:border-b-2={currentPage === item.key}
                    class:border-blue-600={currentPage === item.key}
                  >
                    {item.name}
                    {#if $availableCountries.length > 0}
                      <span class="ml-1 text-xs bg-blue-100 text-blue-600 px-2 py-1 rounded-full">
                        {$availableCountries.length}
                      </span>
                    {/if}
                    <svg class="ml-1 h-4 w-4 transition-transform" class:rotate-180={showCountriesDropdown} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                    </svg>
                  </a>

                  <!-- Dropdown Menu -->
                  {#if showCountriesDropdown}
                    <div
                      class="absolute top-full left-0 mt-1 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50"
                      on:mouseenter={keepDropdown}
                      on:mouseleave={hideDropdown}
                      role="menu"
                      tabindex="-1"
                    >
                      <div class="py-1">
                        {#if dropdownContent.type === 'loading'}
                          <div class="flex items-center px-4 py-2 text-sm text-gray-400">
                            <svg class="animate-spin h-4 w-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                            </svg>
                            {dropdownContent.message}
                          </div>
                        {:else if dropdownContent.type === 'empty'}
                          <div class="px-4 py-2 text-sm text-gray-400">
                            {dropdownContent.message}
                          </div>
                          <div class="border-t border-gray-100 mt-1 pt-1">
                            <a
                              href="/countries"
                              class="block px-4 py-2 text-sm text-blue-600 hover:bg-gray-100 transition-colors"
                            >
                              View all countries
                            </a>
                          </div>
                        {:else}
                          {#each dropdownContent.countries as country}
                            <a
                              href={country.href}
                              class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                            >
                              {country.name}
                            </a>
                          {/each}
                        {/if}
                      </div>
                    </div>
                  {/if}
                </div>
              {:else}
                <a
                  href={item.href}
                  class="px-3 py-2 text-sm font-medium"
                  class:text-blue-600={currentPage === item.key}
                  class:border-b-2={currentPage === item.key}
                  class:border-blue-600={currentPage === item.key}
                  class:text-gray-500={currentPage !== item.key}
                  class:hover:text-gray-700={currentPage !== item.key}
                >
                  {item.name}
                </a>
              {/if}
            </div>
          {/each}
        </div>
      </div>

      <!-- Mobile menu button -->
      <div class="md:hidden">
        <button
          type="button"
          aria-label="Open mobile menu"
          class="text-gray-500 hover:text-gray-700 focus:outline-none focus:text-gray-700"
        >
          <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
      </div>
    </div>
  </nav>
</header>