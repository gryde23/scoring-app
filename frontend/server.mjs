import http from "node:http";
import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const port = Number(process.env.PORT || 5175);

const proxyTargets = [
  { prefix: "/api/auth", target: "http://localhost:8080" },
  { prefix: "/api/scoring", target: "http://localhost:8081" },
  { prefix: "/api/applications", target: "http://localhost:8081" }
];

const contentTypes = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".svg": "image/svg+xml",
  ".png": "image/png",
  ".ico": "image/x-icon"
};

function findProxyTarget(url) {
  return proxyTargets.find(({ prefix }) => url.startsWith(prefix));
}

async function proxyRequest(req, res, target) {
  const upstreamUrl = new URL(req.url, target.target);
  const headers = { ...req.headers, host: upstreamUrl.host };

  try {
    const upstreamResponse = await fetch(upstreamUrl, {
      method: req.method,
      headers,
      body: ["GET", "HEAD"].includes(req.method || "GET") ? undefined : req,
      duplex: "half"
    });

    const responseHeaders = Object.fromEntries(upstreamResponse.headers.entries());
    delete responseHeaders["content-encoding"];
    delete responseHeaders["content-length"];

    res.writeHead(upstreamResponse.status, responseHeaders);
    if (upstreamResponse.body) {
      for await (const chunk of upstreamResponse.body) {
        res.write(chunk);
      }
    }
    res.end();
  } catch (error) {
    res.writeHead(502, { "content-type": "application/json; charset=utf-8" });
    res.end(JSON.stringify({
      message: "Backend service is unavailable",
      details: error instanceof Error ? error.message : String(error)
    }));
  }
}

async function serveStatic(req, res) {
  const requestUrl = new URL(req.url || "/", `http://localhost:${port}`);
  const cleanPath = decodeURIComponent(requestUrl.pathname);
  const requestedPath = cleanPath === "/" ? "/index.html" : cleanPath;
  const filePath = path.normalize(path.join(__dirname, requestedPath));

  if (!filePath.startsWith(__dirname)) {
    res.writeHead(403);
    res.end("Forbidden");
    return;
  }

  try {
    const file = await fs.readFile(filePath);
    const extension = path.extname(filePath);
    res.writeHead(200, {
      "content-type": contentTypes[extension] || "application/octet-stream"
    });
    res.end(file);
  } catch {
    const fallback = await fs.readFile(path.join(__dirname, "index.html"));
    res.writeHead(200, { "content-type": contentTypes[".html"] });
    res.end(fallback);
  }
}

const server = http.createServer(async (req, res) => {
  const target = findProxyTarget(req.url || "");
  if (target) {
    await proxyRequest(req, res, target);
    return;
  }

  await serveStatic(req, res);
});

server.listen(port, () => {
  console.log(`Frontend is running at http://localhost:${port}`);
});
