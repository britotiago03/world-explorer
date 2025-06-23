// plugins/cors.ts
import fp from 'fastify-plugin'
import fastifyCors from '@fastify/cors'

export default fp(async (fastify) => {
    fastify.register(fastifyCors, {
        origin: '*',
        credentials: true,
        methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS', 'PATCH'],
        allowedHeaders: ['Content-Type', 'Authorization', 'Cache-Control'],
    })
})