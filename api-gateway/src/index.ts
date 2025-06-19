import Fastify from 'fastify'
import cors from './plugins/cors'
import proxyRoutes from './routes/proxy'
import sessionTracking from './routes/session-tracking'

const app = Fastify()

app.register(cors)
app.register(proxyRoutes)
app.register(sessionTracking)

// Get port from environment variable, fallback to 3000
const port = parseInt(process.env.PORT || '3000', 10)

app.listen({ port, host: '0.0.0.0' }, err => {
    if (err) {
        app.log.error(err)
        process.exit(1)
    }
    console.log(`🚀 API Gateway running on http://localhost:${port}`)
})