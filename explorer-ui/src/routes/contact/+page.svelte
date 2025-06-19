<!-- src/routes/contact/+page.svelte -->

<script>
  import Hero from '$lib/components/Hero.svelte';
  import Footer from '$lib/components/Footer.svelte';

  let formData = {
    name: '',
    email: '',
    subject: '',
    message: ''
  };

  let isSubmitting = false;
  let submitMessage = '';

  function handleSubmit(event) {
    event.preventDefault();
    isSubmitting = true;

    // Simulate form submission
    setTimeout(() => {
      isSubmitting = false;
      submitMessage = 'Thank you for your message! We\'ll get back to you soon.';

      // Reset form
      formData = {
        name: '',
        email: '',
        subject: '',
        message: ''
      };

      // Clear message after 5 seconds
      setTimeout(() => {
        submitMessage = '';
      }, 5000);
    }, 1000);
  }
</script>

<svelte:head>
  <title>Contact Us - Travel Country Explorer</title>
  <meta name="description" content="Get in touch with Travel Country Explorer. We'd love to hear from you about our travel guides and country information." />
</svelte:head>

<Hero
  title="Contact Us"
  subtitle="Have questions, suggestions, or feedback? We'd love to hear from you and help make your travel planning even better."
  primaryButtonText="Browse Countries"
  secondaryButtonText="About Us"
/>

<section class="py-16 bg-gray-50">
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="bg-white rounded-xl shadow-lg p-8">
      <div class="text-center mb-8">
        <h2 class="text-3xl font-bold text-gray-900 mb-4">
          Get in Touch
        </h2>
        <p class="text-lg text-gray-600">
          Whether you have questions about a destination, suggestions for new countries to cover, or general feedback, we're here to help.
        </p>
      </div>

      {#if submitMessage}
        <div class="mb-6 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          {submitMessage}
        </div>
      {/if}

      <form on:submit={handleSubmit} class="space-y-6">
        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700 mb-2">
              Full Name *
            </label>
            <input
              type="text"
              id="name"
              bind:value={formData.name}
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
              placeholder="Your full name"
            />
          </div>

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
              Email Address *
            </label>
            <input
              type="email"
              id="email"
              bind:value={formData.email}
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
              placeholder="your.email@example.com"
            />
          </div>
        </div>

        <div>
          <label for="subject" class="block text-sm font-medium text-gray-700 mb-2">
            Subject *
          </label>
          <select
            id="subject"
            bind:value={formData.subject}
            required
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
          >
            <option value="">Select a subject</option>
            <option value="general">General Inquiry</option>
            <option value="destination">Destination Question</option>
            <option value="suggestion">Suggestion for New Country</option>
            <option value="feedback">Website Feedback</option>
            <option value="partnership">Partnership Opportunity</option>
            <option value="other">Other</option>
          </select>
        </div>

        <div>
          <label for="message" class="block text-sm font-medium text-gray-700 mb-2">
            Message *
          </label>
          <textarea
            id="message"
            bind:value={formData.message}
            required
            rows="6"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 resize-vertical"
            placeholder="Tell us how we can help you..."
          ></textarea>
        </div>

        <div class="text-center">
          <button
            type="submit"
            disabled={isSubmitting}
            class="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-8 py-3 rounded-lg text-lg font-semibold transition duration-300 shadow-lg"
          >
            {isSubmitting ? 'Sending...' : 'Send Message'}
          </button>
        </div>
      </form>
    </div>
  </div>
</section>

<!-- Contact Information -->
<section class="py-16 bg-white">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h2 class="text-3xl font-bold text-gray-900 mb-4">
        Other Ways to Reach Us
      </h2>
    </div>

    <div class="grid md:grid-cols-3 gap-8 text-center">
      <div class="p-6">
        <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">Email Us</h3>
        <p class="text-gray-600">hello@travelcountryexplorer.com</p>
      </div>

      <div class="p-6">
        <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">Response Time</h3>
        <p class="text-gray-600">We typically respond within 24-48 hours</p>
      </div>

      <div class="p-6">
        <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">Office</h3>
        <p class="text-gray-600">Remote team serving travelers worldwide</p>
      </div>
    </div>
  </div>
</section>

<Footer />