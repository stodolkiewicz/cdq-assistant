# Builds a runnable image of semdin/mcp-weather (https://github.com/semdin/mcp-weather),
# an MCP server exposing weather tools via the STDIO transport (task.md's third knowledge
# source). Not our code — cloned at build time so no manual git clone/npm install is needed
# on the host, only Docker.
FROM node:20-alpine

RUN apk add --no-cache git

WORKDIR /app
RUN git clone --depth 1 https://github.com/semdin/mcp-weather.git .
RUN npm install

CMD ["npx", "tsx", "src/index.ts"]
