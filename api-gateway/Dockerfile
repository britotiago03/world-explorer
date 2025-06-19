# Use official Node.js image with support for pnpm
FROM node:20-alpine

# Set working directory
WORKDIR /app

# Install pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy package files and install dependencies
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

# Copy the rest of the source code
COPY . .

# Build the TypeScript project
RUN pnpm build

# Expose the port
EXPOSE 3011

# Start the application
CMD ["pnpm", "start"]
