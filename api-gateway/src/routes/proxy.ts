import fp from 'fastify-plugin'
import fastifyHttpProxy from '@fastify/http-proxy'

export default fp(async (fastify) => {
    // Map routes to services
    const routes = {
        '/order': 'http://localhost:3010',
        '/payment': 'http://localhost:3011',
        '/project': 'http://localhost:3012',
        '/contract': 'http://localhost:3013',
        '/monitor': 'http://localhost:3014',
    }

    for (const [prefix, target] of Object.entries(routes)) {
        fastify.register(fastifyHttpProxy, {
            upstream: target,
            prefix,
            rewritePrefix: prefix,
            http2: false,
        })
    }
})
