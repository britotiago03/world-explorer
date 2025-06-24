// api-gateway/src/plugins/cors.ts
import fp from 'fastify-plugin'
import fastifyCors from '@fastify/cors'

export default fp(async (fastify) => {
    await fastify.register(fastifyCors, {
        origin: true, // Allow all origins - change to specific origins in production
        credentials: true,
        methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', 'PATCH'],
        allowedHeaders: [
            'Content-Type',
            'Authorization',
            'Cache-Control',
            'Accept',
            'Origin',
            'X-Requested-With'
        ],
        exposedHeaders: [
            'Content-Type',
            'Cache-Control',
            'Connection'
        ],
        maxAge: 86400 // 24 hours
    })

    // Add a global preHandler to ensure CORS headers are always set
    fastify.addHook('preHandler', async (request, reply) => {
        // Ensure CORS headers are set for all responses, especially SSE
        if (request.headers.origin) {
            reply.header('Access-Control-Allow-Origin', request.headers.origin)
        } else {
            reply.header('Access-Control-Allow-Origin', '*')
        }

        reply.header('Access-Control-Allow-Credentials', 'true')

        // Handle preflight requests
        if (request.method === 'OPTIONS') {
            reply.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS, PATCH')
            reply.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Cache-Control, Accept, Origin, X-Requested-With')
            reply.header('Access-Control-Max-Age', '86400')
            reply.code(204)
            return reply.send()
        }
    })
})