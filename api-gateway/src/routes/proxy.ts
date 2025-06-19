import fp from 'fastify-plugin'
import fastifyHttpProxy from '@fastify/http-proxy'

export default fp(async (fastify) => {
    // Map routes to services - use container names for Docker networking
    const routes = {
        '/api/countries': 'http://country-service:3020',
    }

    for (const [prefix, target] of Object.entries(routes)) {
        fastify.register(fastifyHttpProxy, {
            upstream: target,
            prefix,
            rewritePrefix: '/api/countries', // Rewrite to match country service endpoint
            http2: false,
        })
    }
})