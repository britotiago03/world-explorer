<!-- src/lib/components/FeaturedCountries.svelte -->
<script>
  import CountryCard from './CountryCard.svelte';

  const featuredCountries = [
    {
      country: 'Portugal',
      description: 'Discover stunning beaches, rich history, and warm hospitality in this European gem.',
      gradientFrom: 'orange-400',
      gradientTo: 'red-500',
      href: '/countries/portugal'
    },
    {
      country: 'Japan',
      description: 'Experience the perfect blend of ancient traditions and cutting-edge modernity.',
      gradientFrom: 'pink-400',
      gradientTo: 'purple-500',
      href: '/countries/japan'
    }
  ];
</script>

<section class="py-16 bg-white">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
        Featured Countries
      </h2>
    </div>

    <div class="grid md:grid-cols-2 gap-8 mb-8">
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

    <!-- View All Countries Button -->
    <div class="text-center">
      <a
        href="/countries"
        class="inline-block bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg text-lg font-semibold transition duration-300 shadow-lg"
      >
        View All Countries
      </a>
    </div>
  </div>
</section>