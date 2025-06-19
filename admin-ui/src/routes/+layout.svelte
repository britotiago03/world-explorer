<!-- admin-ui/src/routes/+layout.svelte -->
<script lang="ts">
	import '../app.css';

	let { children } = $props();
</script>

<!-- Admin Header -->
<header class="bg-slate-800 text-white shadow-lg">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<div class="flex justify-between items-center h-16">
			<!-- Logo -->
			<div class="flex items-center">
				<h1 class="text-xl font-bold">World Explorer Admin</h1>
			</div>

			<!-- Navigation -->
			<nav class="flex space-x-8">
				<a href="/" class="text-gray-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition">
					Dashboard
				</a>
				<a href="/countries" class="text-gray-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition">
					Countries
				</a>
				<a href="/bulk-import" class="text-gray-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition">
					Bulk Import
				</a>
			</nav>
		</div>
	</div>
</header>

<!-- Main Content -->
<main class="min-h-screen bg-gray-50">
	{@render children()}
</main>

<!-- Footer -->
<footer class="bg-slate-800 text-gray-400 py-6">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
		<p>&copy; 2025 World Explorer Admin. All rights reserved.</p>
	</div>
</footer>

<style>
	:global(body) {
		margin: 0;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
	}
</style>