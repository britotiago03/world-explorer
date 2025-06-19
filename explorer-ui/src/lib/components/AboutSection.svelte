<!-- src/lib/components/AboutSection.svelte -->
<script>
  const features = [
    {
      icon: '<svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>',
      label: 'Researched Facts'
    },
    {
      icon: '<svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>',
      label: 'Updated Regularly'
    },
    {
      icon: '<svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>',
      label: 'Traveler Focused'
    }
  ];
</script>

<section class="py-16 bg-gray-50">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
    <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-8">
      About World Explorer
    </h2>
    <p class="text-lg text-gray-600 max-w-4xl mx-auto mb-12 leading-relaxed">
      Our mission is to provide travelers with comprehensive, well-organized information about countries around the world. We help you understand everything from cultural nuances to regional highlights before you travel.
    </p>

    <!-- Feature Icons -->
    <div class="grid md:grid-cols-3 gap-8 max-w-4xl mx-auto">
      {#each features as feature}
        <div class="flex flex-col items-center">
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mb-4">
            {@html feature.icon}
          </div>
          <span class="text-blue-600 font-semibold">{feature.label}</span>
        </div>
      {/each}
    </div>
  </div>
</section>