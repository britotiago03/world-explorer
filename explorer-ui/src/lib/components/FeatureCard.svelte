<!-- src/lib/components/FeatureCard.svelte -->
<script>
  export let icon;
  export let title;
  export let description;
</script>

<div class="bg-white rounded-xl p-8 shadow-lg hover:shadow-xl transition duration-300">
  <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-6">
    {@html icon}
  </div>
  <h3 class="text-xl font-bold text-gray-900 mb-4">{title}</h3>
  <p class="text-gray-600 leading-relaxed">
    {description}
  </p>
</div>