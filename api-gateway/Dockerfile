# Dockerfile
FROM node:20-alpine

# Enable pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

WORKDIR /app

ENV NODE_ENV=development

# Install deps
COPY pnpm-lock.yaml package.json ./
RUN pnpm install

# Copy source files
COPY . .

# Build TS files
RUN pnpm build

# Start the compiled app
CMD ["node", "dist/app.js"]
