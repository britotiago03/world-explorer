<!-- src/lib/components/Footer.svelte -->
<script>
  import { onMount } from 'svelte'
  import { availableCountries, countryStore } from '$lib/stores/countryStore.ts'

  // Dynamic countries list for footer (first 5)
  $: countries = $availableCountries.slice(0, 5).map(country => ({
    name: country.name,
    href: `/countries/${country.name.toLowerCase().replace(/\s+/g, '-')}`
  }))

  // Add "View all" link if there are more countries
  $: footerCountries = $availableCountries.length > 5
    ? [...countries, { name: `View all ${$availableCountries.length} countries`, href: '/countries' }]
    : countries

  const quickLinks = [
    { name: 'Home', href: '/' },
    { name: 'About', href: '/about' },
    { name: 'Contact', href: '/contact' }
  ]

  const currentYear = new Date().getFullYear()

  onMount(async () => {
    // Load countries if not already loaded
    if ($availableCountries.length === 0) {
      await countryStore.loadCountries()
    }
  })
</script>

<footer class="bg-gray-900 text-white py-12">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="grid md:grid-cols-3 gap-8">
      <!-- Brand -->
      <div>
        <h3 class="text-xl font-bold mb-4">World Explorer</h3>
        <p class="text-gray-400 leading-relaxed">
          Comprehensive guides to help travelers understand countries deeply before visiting—from regional insights and culture to climate, attractions, and the best times to visit.
        </p>
        <div class="mt-4 flex items-center text-sm text-gray-500">
          <div class="w-2 h-2 bg-green-400 rounded-full mr-2 animate-pulse"></div>
          Real-time updates
        </div>
        <p class="text-gray-500 mt-4">© {currentYear} World Explorer. All rights reserved.</p>
      </div>

      <!-- Dynamic Countries -->
      <div>
        <h4 class="text-lg font-semibold mb-4">
          Countries
          {#if $availableCountries.length > 0}
            <span class="text-sm text-gray-400">({$availableCountries.length} available)</span>
          {/if}
        </h4>
        <ul class="space-y-2 text-gray-400">
          {#if footerCountries.length === 0}
            <li class="text-gray-500">Loading countries...</li>
          {:else}
            {#each footerCountries as country}
              <li>
                <a href={country.href} class="hover:text-white transition">
                  {country.name}
                </a>
              </li>
            {/each}
          {/if}
        </ul>
      </div>

      <!-- Quick Links -->
      <div>
        <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
        <ul class="space-y-2 text-gray-400">
          {#each quickLinks as link}
            <li>
              <a href={link.href} class="hover:text-white transition">
                {link.name}
              </a>
            </li>
          {/each}
        </ul>

        <!-- Admin Link (for development) -->
        <div class="mt-6 pt-4 border-t border-gray-700">
          <p class="text-sm text-gray-500 mb-2">Admin</p>
          <a href="http://localhost:3002" target="_blank" class="text-gray-400 hover:text-white transition text-sm">
            Admin Dashboard
          </a>
        </div>
      </div>
    </div>
  </div>
</footer>