<!-- src/lib/components/CountryCard.svelte -->
<script>
  export let country;
  export let description;
  export let gradientFrom = "orange-400";
  export let gradientTo = "red-500";
  export let href = "#";
</script>

<div class="relative rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition duration-300 group">
  <a {href} class="block cursor-pointer">
    <div class="h-80 bg-gradient-to-br from-{gradientFrom} to-{gradientTo} relative">
      <!-- This would be your country image -->
      <div class="absolute inset-0 bg-black opacity-20 group-hover:opacity-30 transition duration-300"></div>
      <div class="absolute bottom-0 left-0 right-0 p-6 text-white">
        <h3 class="text-2xl font-bold mb-2">{country}</h3>
        <p class="text-gray-200 mb-4">
          {description}
        </p>
        <span class="inline-flex items-center text-white font-semibold pointer-events-none">
          Explore {country}
          <svg class="ml-2 w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </span>
      </div>
    </div>
  </a>
</div>