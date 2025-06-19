<!-- src/lib/components/CTASection.svelte -->
<script>
  export let title = "Start Planning Your Next Adventure";
  export let subtitle = "Explore our detailed country guides and make your travel experience more enriching and authentic.";
  export let buttonText = "Browse Countries";
  export let buttonHref = "/countries";
</script>

<section class="py-16 bg-blue-600">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
    <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">
      {title}
    </h2>
    <p class="text-xl text-blue-100 mb-8 max-w-3xl mx-auto">
      {subtitle}
    </p>
    <a href={buttonHref} class="inline-block bg-white text-blue-600 hover:bg-gray-100 px-8 py-3 rounded-lg text-lg font-semibold transition duration-300 shadow-lg">
      {buttonText}
    </a>
  </div>
</section>