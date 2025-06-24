// api-gateway/src/routes/events.ts
import fp from 'fastify-plugin'
import { FastifyInstance, FastifyRequest, FastifyReply } from 'fastify'

export default fp(async (fastify: FastifyInstance) => {
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
                    timestamp: new Date().toISOString(),
                    apiGateway: 'running'
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

    // Main SSE endpoint - Enhanced country events endpoint
    fastify.get('/api/countries/events', async (request: FastifyRequest, reply: FastifyReply) => {
        fastify.log.info('New SSE connection request from:', request.headers.origin || 'unknown')

        // Set up SSE headers immediately using reply.raw
        reply.raw.writeHead(200, {
            'Content-Type': 'text/event-stream',
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
            'Access-Control-Allow-Origin': request.headers.origin || '*',
            'Access-Control-Allow-Credentials': 'true',
            'Access-Control-Allow-Headers': 'Cache-Control, Content-Type, Accept',
            'Access-Control-Allow-Methods': 'GET, OPTIONS',
            'X-Accel-Buffering': 'no'
        })

        const eventServiceUrl = `${process.env.EVENT_SERVICE_URL || 'http://event-service:3021'}/api/events/subscribe/source/country-service`

        let eventResponse: Response | null = null
        let reader: ReadableStreamDefaultReader<Uint8Array> | null = null
        let heartbeatInterval: NodeJS.Timeout | null = null

        // Cleanup function
        const cleanup = () => {
            fastify.log.info('Cleaning up SSE connection')

            if (heartbeatInterval) {
                clearInterval(heartbeatInterval)
                heartbeatInterval = null
            }

            if (reader) {
                reader.cancel().catch((err) => {
                    fastify.log.warn('Error cancelling reader:', err)
                })
                reader = null
            }

            if (eventResponse && eventResponse.body) {
                eventResponse.body.cancel().catch((err) => {
                    fastify.log.warn('Error cancelling response body:', err)
                })
            }
        }

        // Handle client disconnect
        request.raw.on('close', () => {
            fastify.log.info('Client disconnected from SSE')
            cleanup()
        })

        request.raw.on('error', (error) => {
            fastify.log.error('Client connection error:', error)
            cleanup()
        })

        try {
            // Send initial connection message
            reply.raw.write('data: {"type":"connection","status":"connected","timestamp":' + Date.now() + '}\n\n')

            fastify.log.info(`Connecting to event service at: ${eventServiceUrl}`)

            eventResponse = await fetch(eventServiceUrl, {
                headers: {
                    'Accept': 'text/event-stream',
                    'Cache-Control': 'no-cache'
                }
            })

            if (!eventResponse.ok) {
                fastify.log.error(`Event service returned status ${eventResponse.status}: ${eventResponse.statusText}`)
                reply.raw.write(`data: {"type":"error","message":"Event service unavailable","status":${eventResponse.status}}\n\n`)
                reply.raw.end()
                return
            }

            if (!eventResponse.body) {
                fastify.log.error('No response body from event service')
                reply.raw.write('data: {"type":"error","message":"No response body from event service"}\n\n')
                reply.raw.end()
                return
            }

            reader = eventResponse.body.getReader()
            const decoder = new TextDecoder()

            // Keep connection alive with periodic heartbeat
            heartbeatInterval = setInterval(() => {
                try {
                    if (!reply.raw.destroyed) {
                        const heartbeatData = `data: {"type":"heartbeat","timestamp":${Date.now()}}\n\n`
                        reply.raw.write(heartbeatData)
                        fastify.log.debug('Sent heartbeat')
                    } else {
                        cleanup()
                    }
                } catch (error) {
                    fastify.log.error('Heartbeat error:', error)
                    cleanup()
                }
            }, 30000) // Every 30 seconds

            // Stream data from event service
            const pump = async (): Promise<void> => {
                try {
                    if (!reader) return

                    const { done, value } = await reader.read()

                    if (done) {
                        fastify.log.info('Event stream ended')
                        cleanup()
                        reply.raw.end()
                        return
                    }

                    const chunk = decoder.decode(value, { stream: true })

                    // Forward the chunk to the client if it's not empty
                    if (chunk.trim()) {
                        fastify.log.debug('Forwarding event chunk:', chunk.substring(0, 100))
                        reply.raw.write(chunk)
                    }

                    // Continue reading
                    return pump()
                } catch (error) {
                    fastify.log.error('Error in stream pump:', error)
                    cleanup()
                    if (!reply.raw.destroyed) {
                        reply.raw.write(`data: {"type":"error","message":"Stream error"}\n\n`)
                        reply.raw.end()
                    }
                }
            }

            await pump()

        } catch (error) {
            fastify.log.error('Error setting up event stream:', error)
            cleanup()

            if (!reply.raw.destroyed) {
                reply.raw.write(`data: {"type":"error","message":"Failed to establish event stream","details":"${error instanceof Error ? error.message : 'Unknown error'}"}\n\n`)
                reply.raw.end()
            }
        }
    })

    // OPTIONS handler for CORS preflight
    fastify.options('/api/countries/events', async (request: FastifyRequest, reply: FastifyReply) => {
        reply.header('Access-Control-Allow-Origin', request.headers.origin || '*')
        reply.header('Access-Control-Allow-Headers', 'Cache-Control, Content-Type, Accept, Origin')
        reply.header('Access-Control-Allow-Methods', 'GET, OPTIONS')
        reply.header('Access-Control-Max-Age', '86400')
        reply.code(204).send()
    })
})