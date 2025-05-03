# Dockerfile
FROM node:20-alpine AS builder

WORKDIR /app

# Enable pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy and install dependencies
COPY pnpm-lock.yaml package.json ./
RUN pnpm install

# Copy the rest and build
COPY . .
RUN pnpm run build

# Serve with a lightweight static server
FROM nginx:alpine AS runner
COPY --from=builder /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
