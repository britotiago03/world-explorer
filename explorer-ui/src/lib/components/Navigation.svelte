<!-- src/lib/components/Navigation.svelte -->
<script>
  export let currentPage = 'home';

  let showCountriesDropdown = false;
  let dropdownTimeout;

  const navItems = [
    { name: 'Home', href: '/', key: 'home' },
    { name: 'Countries', href: '/countries', key: 'countries', hasDropdown: true },
    { name: 'About', href: '/about', key: 'about' }
  ];

  const countries = [
    { name: 'Portugal', href: '/countries/portugal' },
    { name: 'Japan', href: '/countries/japan' },
    { name: 'More coming soon...', href: '#', disabled: true }
  ];

  function showDropdown() {
    clearTimeout(dropdownTimeout);
    showCountriesDropdown = true;
  }

  function hideDropdown() {
    dropdownTimeout = setTimeout(() => {
      showCountriesDropdown = false;
    }, 150); // Small delay to allow moving to dropdown
  }

  function keepDropdown() {
    clearTimeout(dropdownTimeout);
  }
</script>

<header class="bg-white shadow-sm border-b">
  <nav class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="flex justify-between items-center h-16">
      <!-- Logo -->
      <div class="flex-shrink-0">
        <a href="/" class="text-xl font-bold text-blue-600">
          World Explorer
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
                  class="relative"
                >
                  <a
                    href={item.href}
                    class="text-gray-500 hover:text-gray-700 px-3 py-2 text-sm font-medium flex items-center"
                    class:text-blue-600={currentPage === item.key}
                    class:border-b-2={currentPage === item.key}
                    class:border-blue-600={currentPage === item.key}
                  >
                    {item.name}
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
                    >
                      <div class="py-1">
                        {#each countries as country}
                          {#if country.disabled}
                            <span class="block px-4 py-2 text-sm text-gray-400">{country.name}</span>
                          {:else}
                            <a
                              href={country.href}
                              class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                            >
                              {country.name}
                            </a>
                          {/if}
                        {/each}
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