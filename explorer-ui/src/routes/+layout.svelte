<!-- src/routes/+layout.svelte -->
<script lang="ts">
    import '../app.css';
    import Navigation from '$lib/components/Navigation.svelte';
    import { page } from '$app/stores';

    // Determine current page for navigation highlighting
    $: currentPage = getCurrentPage($page.url.pathname);

    function getCurrentPage(pathname: string): string {
        if (pathname === '/') return 'home';
        if (pathname.startsWith('/countries')) return 'countries';
        if (pathname.startsWith('/about')) return 'about';
        return '';
    }
</script>

<Navigation {currentPage} />

<!-- Main Content -->
<main>
    <slot />
</main>