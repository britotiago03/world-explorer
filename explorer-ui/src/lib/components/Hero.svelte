<!-- src/lib/components/Hero.svelte -->
<script>
  import { onMount } from 'svelte'
  import { availableCountries, countriesLoading, countryStore } from '$lib/stores/countryStore.ts'

  export let title = "World Explorer"
  export let subtitle = "Discover everything you need to know about a country before you travel - from cultural tips and regional insights to the best times to visit."
  export let primaryButtonText = "Explore Countries"
  export let secondaryButtonText = "Featured Destinations"

  // Remove unused props or make them constants if they're for external reference
  // export let featuredCountry = null
  // export let featuredCountryDescription = null

  let mounted = false
  let currentSlide = 0

  // Generate slides from available countries
  $: slides = generateSlides($availableCountries, $countriesLoading)

  function generateSlides(countries, loading) {
    const gradients = [
      { from: "orange-400", to: "red-500" },
      { from: "pink-400", to: "purple-500" },
      { from: "blue-400", to: "indigo-500" },
      { from: "green-400", to: "teal-500" },
      { from: "yellow-400", to: "orange-500" },
      { from: "purple-400", to: "pink-500" }
    ]

    if (loading) {
      return [
        {
          country: "World",
          title: "Loading amazing destinations...",
          description: "Getting ready to show you the world.",
          gradient: "from-blue-400 to-indigo-500"
        }
      ]
    }

    if (countries.length === 0) {
      // Default slides when no countries are loaded
      return [
        {
          country: "World",
          title: "Explore the world with us",
          description: "Comprehensive travel guides will appear here as countries are added to our database.",
          gradient: "from-blue-400 to-indigo-500"
        }
      ]
    }

    // Take first 6 countries or all if less than 6
    const featuredCountries = countries.slice(0, 6)

    return featuredCountries.map((country, index) => ({
      country: country.name,
      title: `Explore the beauty of ${country.name}`,
      description: generateCountryDescription(country),
      gradient: `from-${gradients[index % gradients.length].from} to-${gradients[index % gradients.length].to}`
    }))
  }

  function generateCountryDescription(country) {
    const descriptions = {
      'Portugal': 'From stunning beaches to historic cities and vibrant culture.',
      'Japan': 'Ancient traditions meet cutting-edge modernity.',
      'Spain': 'Rich culture, incredible cuisine, and passionate people.',
      'France': 'Art, cuisine, and timeless elegance await.',
      'Italy': 'History, art, and culinary excellence.',
      'Germany': 'Innovation, efficiency, and rich heritage.',
      'Brazil': 'Vibrant festivals, stunning nature, and warm people.',
      'Canada': 'Vast wilderness and multicultural cities.',
      'Australia': 'Unique wildlife and stunning landscapes.',
      'Norway': 'Breathtaking fjords and northern lights.'
    }

    return descriptions[country.name] ||
           `${country.capital ? `Capital: ${country.capital}. ` : ''}Discover unique culture and stunning landscapes.`
  }

  onMount(async () => {
    mounted = true

    // Load countries if not already loaded
    if ($availableCountries.length === 0 && !$countriesLoading) {
      await countryStore.loadCountries()
    }

    // Auto-advance slides
    const interval = setInterval(() => {
      if (slides.length > 1) {
        currentSlide = (currentSlide + 1) % slides.length
      }
    }, 5000)

    return () => clearInterval(interval)
  })

  function handleSecondaryClick() {
    // Default: scroll to featured countries section (for "Featured Destinations")
    const featuredSection = document.querySelector('[data-section="featured-countries"]')
    if (featuredSection) {
      featuredSection.scrollIntoView({ behavior: 'smooth' })
    }
  }

  function nextSlide() {
    currentSlide = (currentSlide + 1) % slides.length
  }

  function prevSlide() {
    currentSlide = currentSlide === 0 ? slides.length - 1 : currentSlide - 1
  }

  function goToSlide(index) {
    currentSlide = index
  }

  $: currentSlideData = slides[currentSlide] || slides[0]

  // Get counter text based on state
  $: counterText = getCounterText($availableCountries, $countriesLoading)

  function getCounterText(countries, loading) {
    if (loading) {
      return "Loading destinations..."
    }

    if (countries.length === 0) {
      return "Ready to explore the world"
    }

    return `${countries.length} ${countries.length === 1 ? 'country' : 'countries'} available to explore`
  }
</script>

<section class="relative h-screen bg-gradient-to-br from-indigo-900 via-blue-800 to-purple-900 flex items-center justify-center text-white overflow-hidden">
  <!-- Animated Background Gradient -->
  <div class="absolute inset-0 bg-gradient-to-br {currentSlideData.gradient} opacity-20 transition-all duration-1000"></div>

  <!-- Background Image Overlay -->
  <div class="absolute inset-0 bg-black opacity-40"></div>

  <!-- Floating Particles -->
  <div class="absolute inset-0 overflow-hidden">
    {#each Array(20) as _, i}
      <div
        class="absolute w-2 h-2 bg-white rounded-full opacity-20 animate-pulse"
        style="
          left: {Math.random() * 100}%;
          top: {Math.random() * 100}%;
          animation-delay: {Math.random() * 3}s;
          animation-duration: {2 + Math.random() * 2}s;
        "
      ></div>
    {/each}
  </div>

  <!-- Hero Content -->
  <div class="relative z-10 max-w-4xl mx-auto text-center px-4 -mt-20">
    <h1 class="text-5xl md:text-6xl font-bold mb-8 transform transition-all duration-1000"
        class:translate-y-0={mounted}
        class:opacity-100={mounted}
        class:translate-y-10={!mounted}
        class:opacity-0={!mounted}>
      {title}
    </h1>

    <p class="text-xl md:text-2xl mb-10 text-gray-200 leading-relaxed transform transition-all duration-1000 delay-300"
       class:translate-y-0={mounted}
       class:opacity-100={mounted}
       class:translate-y-10={!mounted}
       class:opacity-0={!mounted}>
      {subtitle}
    </p>

    <!-- Dynamic Countries Counter -->
    <div class="mb-6 transform transition-all duration-1000 delay-400"
         class:translate-y-0={mounted}
         class:opacity-100={mounted}
         class:translate-y-10={!mounted}
         class:opacity-0={!mounted}>
      <p class="text-lg text-blue-200 flex items-center justify-center">
        {#if $countriesLoading}
          <svg class="animate-spin h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
          </svg>
        {:else if $availableCountries.length > 0}
          <span class="font-semibold text-2xl text-white mr-1">{$availableCountries.length}</span>
        {/if}
        {counterText}
      </p>
    </div>

    <!-- CTA Buttons -->
    <div class="flex flex-col sm:flex-row gap-4 justify-center mb-12 transform transition-all duration-1000 delay-500"
         class:translate-y-0={mounted}
         class:opacity-100={mounted}
         class:translate-y-10={!mounted}
         class:opacity-0={!mounted}>
      <a
        href="/countries"
        class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg text-lg font-semibold transition-all duration-300 shadow-lg text-center transform hover:scale-105 hover:shadow-xl"
      >
        {primaryButtonText}
      </a>

      {#if secondaryButtonText === 'Contact Us'}
        <a
          href="/contact"
          class="border-2 border-white text-white hover:bg-white hover:text-blue-900 px-8 py-3 rounded-lg text-lg font-semibold transition-all duration-300 cursor-pointer transform hover:scale-105 text-center"
        >
          {secondaryButtonText}
        </a>
      {:else if secondaryButtonText === 'About Us'}
        <a
          href="/about"
          class="border-2 border-white text-white hover:bg-white hover:text-blue-900 px-8 py-3 rounded-lg text-lg font-semibold transition-all duration-300 cursor-pointer transform hover:scale-105 text-center"
        >
          {secondaryButtonText}
        </a>
      {:else}
        <button
          on:click={handleSecondaryClick}
          class="border-2 border-white text-white hover:bg-white hover:text-blue-900 px-8 py-3 rounded-lg text-lg font-semibold transition-all duration-300 cursor-pointer transform hover:scale-105"
        >
          {secondaryButtonText}
        </button>
      {/if}
    </div>
  </div>

  <!-- Navigation Arrows (only show if multiple slides) -->
  {#if slides.length > 1}
    <button
      on:click={prevSlide}
      aria-label="Previous slide"
      class="absolute left-8 top-1/2 transform -translate-y-1/2 w-12 h-12 bg-black bg-opacity-30 hover:bg-opacity-50 rounded-full flex items-center justify-center text-white transition-all duration-300 hover:scale-110 cursor-pointer"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
    </button>
    <button
      on:click={nextSlide}
      aria-label="Next slide"
      class="absolute right-8 top-1/2 transform -translate-y-1/2 w-12 h-12 bg-black bg-opacity-30 hover:bg-opacity-50 rounded-full flex items-center justify-center text-white transition-all duration-300 hover:scale-110 cursor-pointer"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
      </svg>
    </button>

    <!-- Slide Indicators -->
    <div class="absolute bottom-20 left-1/2 transform -translate-x-1/2 flex space-x-2">
      {#each slides as _, index}
        <button
          on:click={() => goToSlide(index)}
          class="w-3 h-3 rounded-full transition-all duration-300 cursor-pointer"
          class:bg-white={currentSlide === index}
          class:bg-gray-400={currentSlide !== index}
          class:scale-125={currentSlide === index}
          aria-label="Go to slide {index + 1}"
        ></button>
      {/each}
    </div>
  {/if}

  <!-- Current Slide Info -->
  <div class="absolute bottom-4 left-4 right-4 text-center">
    <div class="bg-black bg-opacity-30 rounded-lg p-4 max-w-md mx-auto">
      <h3 class="text-lg font-semibold mb-1">{currentSlideData.title}</h3>
      <p class="text-sm text-gray-200">{currentSlideData.description}</p>
    </div>
  </div>
</section>