import fp from 'fastify-plugin'
import { FastifyInstance, FastifyRequest, FastifyReply } from 'fastify'

export default fp(async (fastify: FastifyInstance) => {
    // Enhanced country events endpoint that aggregates country list and event stream
    fastify.get('/api/countries/events', async (request: FastifyRequest, reply: FastifyReply) => {
        // Set up SSE headers with better CORS support
        reply.type('text/event-stream')
        reply.header('Cache-Control', 'no-cache')
        reply.header('Connection', 'keep-alive')
        reply.header('Access-Control-Allow-Origin', '*')
        reply.header('Access-Control-Allow-Headers', 'Cache-Control, Content-Type')
        reply.header('Access-Control-Allow-Methods', 'GET, OPTIONS')
        reply.header('X-Accel-Buffering', 'no') // Disable nginx buffering

        const eventServiceUrl = `${process.env.EVENT_SERVICE_URL || 'http://event-service:3021'}/api/events/subscribe/source/country-service`

        let response: Response | null = null
        let reader: ReadableStreamDefaultReader<Uint8Array> | null = null

        try {
            fastify.log.info(`Connecting to event service at: ${eventServiceUrl}`)

            response = await fetch(eventServiceUrl, {
                headers: {
                    'Accept': 'text/event-stream',
                    'Cache-Control': 'no-cache'
                }
            })

            if (!response.ok) {
                fastify.log.error(`Event service returned status ${response.status}: ${response.statusText}`)
                return reply.code(502).send({
                    error: 'Event service unavailable',
                    details: `Status: ${response.status}`
                })
            }

            if (!response.body) {
                fastify.log.error('No response body from event service')
                return reply.code(502).send({ error: 'No response body from event service' })
            }

            reader = response.body.getReader()
            const decoder = new TextDecoder()

            // Handle client disconnect
            const cleanup = () => {
                if (reader) {
                    reader.cancel().catch((err) => {
                        fastify.log.warn('Error cancelling reader:', err)
                    })
                }
                if (response && response.body) {
                    response.body.cancel().catch((err) => {
                        fastify.log.warn('Error cancelling response body:', err)
                    })
                }
            }

            request.raw.on('close', cleanup)
            request.raw.on('error', cleanup)

            // Send initial connection message
            reply.raw.write('data: {"type":"connection","status":"connected"}\n\n')

            // Keep connection alive with periodic heartbeat
            const heartbeatInterval = setInterval(() => {
                if (!reply.sent) {
                    reply.raw.write('data: {"type":"heartbeat","timestamp":' + Date.now() + '}\n\n')
                } else {
                    clearInterval(heartbeatInterval)
                }
            }, 30000) // Every 30 seconds

            // Stream data from event service
            const pump = async (): Promise<void> => {
                try {
                    if (!reader) return

                    const { done, value } = await reader.read()

                    if (done) {
                        fastify.log.info('Event stream ended')
                        clearInterval(heartbeatInterval)
                        reply.raw.end()
                        return
                    }

                    const chunk = decoder.decode(value, { stream: true })

                    // Forward the chunk to the client
                    if (chunk.trim()) {
                        reply.raw.write(chunk)
                    }

                    // Continue reading
                    return pump()
                } catch (error) {
                    fastify.log.error('Error in stream pump:', error)
                    clearInterval(heartbeatInterval)
                    cleanup()
                    if (!reply.sent) {
                        reply.raw.end()
                    }
                }
            }

            await pump()

        } catch (error) {
            fastify.log.error('Error setting up event stream:', error)

            // Clean up resources
            if (reader) {
                try {
                    await reader.cancel()
                } catch (cleanupError) {
                    fastify.log.warn('Error during reader cleanup:', cleanupError)
                }
            }

            if (!reply.sent) {
                reply.code(500).send({
                    error: 'Failed to establish event stream',
                    details: error instanceof Error ? error.message : 'Unknown error'
                })
            }
        }
    })

    // OPTIONS handler for CORS preflight
    fastify.options('/api/countries/events', async (_request: FastifyRequest, reply: FastifyReply) => {
        reply.header('Access-Control-Allow-Origin', '*')
        reply.header('Access-Control-Allow-Headers', 'Cache-Control, Content-Type')
        reply.header('Access-Control-Allow-Methods', 'GET, OPTIONS')
        reply.code(204).send()
    })

    // Health check for event connectivity
    fastify.get('/api/countries/events/health', async (_request: FastifyRequest, reply: FastifyReply) => {
        try {
            const eventServiceUrl = `${process.env.EVENT_SERVICE_URL || 'http://event-service:3021'}/api/events/health`

            const response = await fetch(eventServiceUrl, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            })

            if (response.ok) {
                const result = await response.text()
                return reply.send({
                    status: 'healthy',
                    eventService: result,
                    timestamp: new Date().toISOString()
                })
            } else {
                return reply.code(502).send({
                    status: 'unhealthy',
                    eventService: `HTTP ${response.status}`,
                    timestamp: new Date().toISOString()
                })
            }
        } catch (error) {
            fastify.log.error('Health check failed:', error)
            return reply.code(503).send({
                status: 'unhealthy',
                error: error instanceof Error ? error.message : 'Unknown error',
                timestamp: new Date().toISOString()
            })
        }
    })
})