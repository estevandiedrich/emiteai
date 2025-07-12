# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files first for better caching
COPY package*.json ./
RUN npm ci --production=false --no-audit --no-fund --prefer-offline

# Copy source code and build
COPY . .
RUN npm run build && npm cache clean --force

# Production stage
FROM nginx:1.25-alpine
COPY --from=build /app/build /usr/share/nginx/html

# Add custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
