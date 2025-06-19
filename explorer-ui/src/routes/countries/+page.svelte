<!-- src/routes/+page.svelte -->

<script>
  import Hero from '$lib/components/Hero.svelte';
  import FeaturesSection from '$lib/components/FeaturesSection.svelte';
  import FeaturedCountries from '$lib/components/FeaturedCountries.svelte';
  import AboutSection from '$lib/components/AboutSection.svelte';
  import CTASection from '$lib/components/CTASection.svelte';
  import Footer from '$lib/components/Footer.svelte';
</script>

<Hero />
<FeaturesSection />
<div data-section="featured-countries">
  <FeaturedCountries />
</div>
<AboutSection />
<CTASection />
<Footer />