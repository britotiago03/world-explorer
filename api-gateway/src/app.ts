import Fastify from 'fastify';
import rootRoutes from './routes/root';

const app = Fastify({ logger: true });

app.register(rootRoutes);

app.listen({ port: 3000, host: '0.0.0.0' }, err => {
    if (err) {
        app.log.error(err);
        process.exit(1);
    }
});
