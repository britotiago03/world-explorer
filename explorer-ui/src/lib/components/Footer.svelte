<!-- src/lib/components/Footer.svelte -->
<script>
  const countries = [
    { name: 'Portugal', href: '/countries/portugal' },
    { name: 'Japan', href: '/countries/japan' },
    { name: 'More coming soon...', href: '#', disabled: true }
  ];

  const quickLinks = [
    { name: 'Home', href: '/' },
    { name: 'About', href: '/about' },
    { name: 'Explore Portugal', href: '/countries/portugal' }
  ];

  const currentYear = new Date().getFullYear();
</script>

<footer class="bg-gray-900 text-white py-12">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="grid md:grid-cols-3 gap-8">
      <!-- Brand -->
      <div>
        <h3 class="text-xl font-bold mb-4">Travel Country Explorer</h3>
        <p class="text-gray-400 leading-relaxed">
          Comprehensive guides to help travelers understand countries deeply before visiting—from regional insights and culture to climate, attractions, and the best times to visit.
        </p>
        <p class="text-gray-500 mt-4">© {currentYear} Travel Country Explorer. All rights reserved.</p>
      </div>

      <!-- Countries -->
      <div>
        <h4 class="text-lg font-semibold mb-4">Countries</h4>
        <ul class="space-y-2 text-gray-400">
          {#each countries as country}
            <li>
              {#if country.disabled}
                <span class="text-gray-500">{country.name}</span>
              {:else}
                <a href={country.href} class="hover:text-white transition">
                  {country.name}
                </a>
              {/if}
            </li>
          {/each}
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
      </div>
    </div>
  </div>
</footer>