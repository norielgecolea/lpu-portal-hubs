import { createReadStream, existsSync, statSync } from 'node:fs';
import { createServer } from 'node:http';
import { extname, join, normalize } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('./dist/lpu-hubs-portal/browser/', import.meta.url));
const port = Number(process.env.PORT || 4200);
const host = process.env.HOST || '0.0.0.0';

const types = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.webp': 'image/webp',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.json': 'application/json',
  '.txt': 'text/plain; charset=utf-8',
  '.apk': 'application/vnd.android.package-archive',
  '.map': 'application/json',
};

createServer((req, res) => {
  const url = new URL(req.url || '/', 'http://localhost');
  let pathname = decodeURIComponent(url.pathname);
  if (pathname.includes('\0')) {
    res.writeHead(400);
    res.end();
    return;
  }
  let file = normalize(join(root, pathname));
  if (!file.startsWith(root)) {
    res.writeHead(400);
    res.end();
    return;
  }
  if (existsSync(file) && statSync(file).isDirectory()) {
    file = join(file, 'index.html');
  }
  if (!existsSync(file) || !statSync(file).isFile()) {
    file = join(root, 'index.html');
  }
  const ext = extname(file);
  const cache = ext === '.html' ? 'no-store' : 'public, max-age=3600';
  res.writeHead(200, {
    'Content-Type': types[ext] || 'application/octet-stream',
    'Cache-Control': cache,
    'Content-Disposition': ext === '.apk' ? 'attachment; filename="LIT_LPU_V5.apk"' : 'inline',
  });
  createReadStream(file).pipe(res);
}).listen(port, host, () => {
  console.log(`Hubs portal listening on ${host}:${port}`);
});
