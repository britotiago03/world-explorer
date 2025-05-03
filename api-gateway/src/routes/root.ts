import { FastifyInstance } from 'fastify';

export default async function rootRoutes(fastify: FastifyInstance) {
    fastify.get('/', async (_req, _res) => {
        return { hello: 'world' };
    });
}
