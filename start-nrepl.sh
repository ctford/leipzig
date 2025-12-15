#!/bin/bash
# Start NREPL server for development and MCP integration

PORT="${NREPL_PORT:-7888}"

echo "Starting NREPL server on port $PORT..."
lein repl :headless :host 127.0.0.1 :port "$PORT"
