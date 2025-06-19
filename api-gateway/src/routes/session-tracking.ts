import fp from 'fastify-plugin'
import { FastifyInstance } from 'fastify'

export default fp(async (fastify: FastifyInstance) => {
    fastify.post('/session', async (request, reply) => {
        try {
            const sessionId = request.headers['x-session-id']
            const userAgent = request.headers['user-agent']
            const ip = request.ip

            await fastify.inject({
                method: 'POST',
                url: 'http://localhost:3014/api/monitor/session',
                headers: {
                    'X-Session-Id': String(sessionId),
                    'User-Agent': String(userAgent),
                    'X-Forwarded-For': ip,
                },
            })

            reply.status(200).send({ success: true })
        } catch (error) {
            console.error('Session logging failed', error)
            reply.status(500).send({ success: false })
        }
    })
})
