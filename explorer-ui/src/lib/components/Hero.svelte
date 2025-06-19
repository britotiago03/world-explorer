<!-- src/lib/components/Hero.svelte -->
<script>
  import { onMount } from 'svelte';

  export let title = "World Explorer";
  export let subtitle = "Discover everything you need to know about a country before you travel - from cultural tips and regional insights to the best times to visit.";
  export let primaryButtonText = "Explore Countries";
  export let secondaryButtonText = "Featured Destinations";
  export let featuredCountry = "Portugal";
  export let featuredCountryDescription = "From stunning beaches to historic cities and vibrant culture.";

  let mounted = false;
  let currentSlide = 0;

  const slides = [
    {
      country: "Portugal",
      title: "Explore the beauty of Portugal",
      description: "From stunning beaches to historic cities and vibrant culture.",
      gradient: "from-orange-400 to-red-500"
    },
    {
      country: "Japan",
      title: "Discover the magic of Japan",
      description: "Ancient traditions meet cutting-edge modernity.",
      gradient: "from-pink-400 to-purple-500"
    },
    {
      country: "Spain",
      title: "Experience vibrant Spain",
      description: "Rich culture, incredible cuisine, and passionate people.",
      gradient: "from-yellow-400 to-orange-500"
    }
  ];

  onMount(() => {
    mounted = true;

    // Auto-advance slides
    const interval = setInterval(() => {
      currentSlide = (currentSlide + 1) % slides.length;
    }, 5000);

    return () => clearInterval(interval);
  });

  function handleSecondaryClick() {
    // Default: scroll to featured countries section (for "Featured Destinations")
    const featuredSection = document.querySelector('[data-section="featured-countries"]');
    if (featuredSection) {
      featuredSection.scrollIntoView({ behavior: 'smooth' });
    }
  }

  function nextSlide() {
    currentSlide = (currentSlide + 1) % slides.length;
  }

  function prevSlide() {
    currentSlide = currentSlide === 0 ? slides.length - 1 : currentSlide - 1;
  }

  function goToSlide(index) {
    currentSlide = index;
  }

  $: currentSlideData = slides[currentSlide];
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

  <!-- Navigation Arrows -->
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

  <!-- Featured Country Info - Removed content, kept for future use -->
  <div class="absolute bottom-4 left-0 right-0 px-8">
    <!-- This space is now available for other content if needed -->
  </div>
</section>