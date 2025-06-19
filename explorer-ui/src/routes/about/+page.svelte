<!-- src/routes/about/+page.svelte -->

<script>
  import Hero from '$lib/components/Hero.svelte';
  import AboutSection from '$lib/components/AboutSection.svelte';
  import CTASection from '$lib/components/CTASection.svelte';
  import Footer from '$lib/components/Footer.svelte';
</script>

<svelte:head>
  <title>About - World Explorer</title>
  <meta name="description" content="Learn about World Explorer's mission to provide comprehensive travel guides and country information." />
</svelte:head>

<Hero
  title="About World Explorer"
  subtitle="Our mission is to provide travelers with comprehensive, well-organized information about countries around the world."
  primaryButtonText="Browse Countries"
  secondaryButtonText="Contact Us"
  featuredCountry="the World"
  featuredCountryDescription="Comprehensive guides for every destination."
/>

<AboutSection />

<section class="py-16 bg-white">
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
        Our Story
      </h2>
      <div class="prose prose-lg mx-auto text-gray-600">
        <p class="mb-6">
          World Explorer was born from a simple idea: travelers deserve accurate, comprehensive information about their destinations before they arrive. Too often, we've seen travelers struggle with cultural misunderstandings, poor timing, or missed opportunities because they lacked essential knowledge about their destination.
        </p>
        <p class="mb-6">
          Our team of travel researchers, cultural experts, and local contributors work tirelessly to create guides that go beyond surface-level tourism information. We dig deep into the cultural nuances, regional differences, and seasonal considerations that can make or break a travel experience.
        </p>
        <p>
          Whether you're planning a weekend getaway or a month-long adventure, our guides provide the insights you need to travel with confidence and respect for local communities.
        </p>
      </div>
    </div>
  </div>
</section>

<CTASection
  title="Ready to Explore?"
  subtitle="Start planning your next adventure with our comprehensive country guides."
  buttonText="Browse All Countries"
/>

<Footer />