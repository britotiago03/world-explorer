// api-gateway/src/index.ts
import Fastify from 'fastify'
import cors from './plugins/cors'
import proxyRoutes from './routes/proxy'
import eventRoutes from './routes/events'

const app = Fastify({
    logger: {
        level: 'info'
    }
})

app.register(cors)
app.register(proxyRoutes)
app.register(eventRoutes)

// Get port from environment variable, fallback to 3011 to match Docker
const port = parseInt(process.env.PORT || '3011', 10)

app.listen({ port, host: '0.0.0.0' }, (err, address) => {
    if (err) {
        app.log.error(err)
        process.exit(1)
    }
    console.log(`🚀 API Gateway running on ${address}`)
    console.log(`🔗 Event Service URL: ${process.env.EVENT_SERVICE_URL || 'http://event-service:3021'}`)
    console.log(`🔗 Country Service URL: http://country-service:3020`)
})