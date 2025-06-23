import Fastify from 'fastify'
import cors from './plugins/cors'
import proxyRoutes from './routes/proxy'
import eventRoutes from './routes/events'

const app = Fastify()

app.register(cors)
app.register(proxyRoutes)
app.register(eventRoutes)

// Get port from environment variable, fallback to 3011 to match Docker
const port = parseInt(process.env.PORT || '3011', 10)

app.listen({ port, host: '0.0.0.0' }, err => {
    if (err) {
        app.log.error(err)
        process.exit(1)
    }
    console.log(`🚀 API Gateway running on http://localhost:${port}`)
})