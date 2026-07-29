# ---- Build stage ----
FROM node:24-alpine3.24 AS build
 
WORKDIR /app
 
# Disable Angular CLI's interactive analytics prompt (no TTY in Docker builds,
# so this would otherwise hang indefinitely)
ENV NG_CLI_ANALYTICS=false
 
# Install dependencies first (better layer caching)
COPY package*.json ./
RUN npm ci
 
# Copy the rest of the source and build (produces both browser + server bundles)
COPY . .
RUN npm run build
 
# ---- Development stage (live reload, runs the SSR dev server) ----
FROM node:24-alpine3.24 AS dev
 
WORKDIR /app
 
ENV NG_CLI_ANALYTICS=false
 
COPY package*.json ./
RUN npm ci
 
COPY . .
 
EXPOSE 4200
 
# `ng serve` with SSR configured in angular.json will render on the server
# and hydrate on the client automatically. --host 0.0.0.0 makes it reachable
# from outside the container.
CMD ["npx", "ng", "serve", "--host", "0.0.0.0", "--port", "4200", "--poll", "2000"]
 
# ---- Production stage (runs the built Node SSR server) ----
FROM node:24-alpine3.24 AS production
 
WORKDIR /app
 
# Only need the compiled output + production deps to run the server
COPY --from=build ./dist ./dist
COPY --from=build package*.json ./
RUN npm ci --omit=dev
 
EXPOSE 4200
 
# Angular's SSR build output includes a server.mjs entry point
# (dist/<project-name>/server/server.mjs) that boots an Express server.
# Angular CLI wires "npm run serve:ssr:<project-name>" to this automatically.
# NOTE: exec-form CMD can't expand wildcards, so shell form is used here.
ENV PORT=4200
CMD ["sh", "-c", "node dist/*/server/server.mjs"]