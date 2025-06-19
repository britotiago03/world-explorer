<!-- src/routes/countries/portugal/+page.svelte -->

<script>
  import Hero from '$lib/components/Hero.svelte';
  import FeatureCard from '$lib/components/FeatureCard.svelte';
  import Footer from '$lib/components/Footer.svelte';

  const portugalFeatures = [
    {
      icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>',
      title: 'Diverse Regions',
      description: 'From the historic charm of Porto and Lisbon to the stunning beaches of the Algarve and the wine country of Douro Valley.'
    },
    {
      icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" /></svg>',
      title: 'Perfect Climate',
      description: 'Mediterranean climate with mild winters and warm summers. Over 300 days of sunshine per year make it ideal for year-round visits.'
    },
    {
      icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>',
      title: 'Rich Culture',
      description: 'Experience Fado music, incredible cuisine featuring fresh seafood, historic architecture, and the warm hospitality of Portuguese people.'
    }
  ];
</script>

<svelte:head>
  <title>Portugal Travel Guide - Travel Country Explorer</title>
  <meta name="description" content="Comprehensive travel guide to Portugal. Discover the best regions, cultural insights, and optimal travel times for your Portuguese adventure." />
</svelte:head>

<Hero
  title="Discover Portugal"
  subtitle="From stunning Atlantic coastlines to historic cities and world-renowned wine regions, Portugal offers an authentic European experience with warmth and charm."
  primaryButtonText="Best Time to Visit"
  secondaryButtonText="Regional Guide"
  featuredCountry="Portugal"
  featuredCountryDescription="A perfect blend of history, culture, and natural beauty."
/>

<section class="py-16 bg-gray-50">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
        Why Visit Portugal?
      </h2>
    </div>

    <div class="grid md:grid-cols-3 gap-8">
      {#each portugalFeatures as feature}
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
          <li><strong>Capital:</strong> Lisbon</li>
          <li><strong>Language:</strong> Portuguese</li>
          <li><strong>Currency:</strong> Euro (EUR)</li>
          <li><strong>Population:</strong> ~10.3 million</li>
          <li><strong>Time Zone:</strong> WET (UTC+0)</li>
        </ul>
      </div>

      <div class="bg-gray-50 rounded-xl p-6">
        <h3 class="text-xl font-bold text-gray-900 mb-4">Cultural Tips</h3>
        <ul class="space-y-2 text-gray-600">
          <li>• Lunch is typically served 12:30-2:30 PM</li>
          <li>• Dinner starts around 7:30-8:00 PM</li>
          <li>• Tipping 10% is appreciated but not mandatory</li>
          <li>• Portuguese people are generally warm and welcoming</li>
          <li>• Learn basic Portuguese phrases - locals appreciate the effort</li>
        </ul>
      </div>
    </div>
  </div>
</section>

<section class="py-16 bg-blue-600">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
    <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">
      Ready to Explore Portugal?
    </h2>
    <p class="text-xl text-blue-100 mb-8 max-w-3xl mx-auto">
      Start planning your Portuguese adventure with our detailed regional guides and seasonal recommendations.
    </p>
    <div class="flex flex-col sm:flex-row gap-4 justify-center">
      <button class="bg-white text-blue-600 hover:bg-gray-100 px-8 py-3 rounded-lg text-lg font-semibold transition duration-300 shadow-lg">
        Regional Breakdown
      </button>
      <button class="border-2 border-white text-white hover:bg-white hover:text-blue-600 px-8 py-3 rounded-lg text-lg font-semibold transition duration-300">
        Best Time to Visit
      </button>
    </div>
  </div>
</section>

<Footer />