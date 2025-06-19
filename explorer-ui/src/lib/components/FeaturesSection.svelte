<!-- src/lib/components/FeaturesSection.svelte -->
<script>
  import FeatureCard from './FeatureCard.svelte';

  const features = [
    {
      icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9v-9m0-9v9" /></svg>',
      title: 'Cultural Insights',
      description: 'Learn about local customs, etiquette, and language to ensure respectful and meaningful interactions with local communities.'
    },
    {
      icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>',
      title: 'Regional Breakdown',
      description: 'Discover the unique character of each region within a country, from bustling cities to tranquil countryside and coastal gems.'
    },
    {
      icon: '<svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>',
      title: 'Best Time to Visit',
      description: 'Get month-by-month insights on weather, crowds, and events to plan your trip during the perfect season.'
    }
  ];
</script>

<section class="py-16 bg-gray-50">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
        Everything You Need for Smart Travel Planning
      </h2>
    </div>

    <div class="grid md:grid-cols-3 gap-8">
      {#each features as feature}
        <FeatureCard
          icon={feature.icon}
          title={feature.title}
          description={feature.description}
        />
      {/each}
    </div>
  </div>
</section>