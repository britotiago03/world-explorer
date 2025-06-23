import fp from 'fastify-plugin'
import fastifyHttpProxy from '@fastify/http-proxy'

export default fp(async (fastify) => {
    // Country service routes
    fastify.register(fastifyHttpProxy, {
        upstream: 'http://country-service:3020',
        prefix: '/api/countries',
        rewritePrefix: '/api/countries',
        http2: false,
    })

    // Note: Event service routes are handled in events.ts for custom SSE logic
    // Regular event proxy endpoints can be added here if needed separately
})