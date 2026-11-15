"""Local demo backend. Expose through a private HTTPS proxy for the Android client."""
import json
import os
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer


class Chat(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path != "/chat":
            return self.send_error(404)
        try:
            size = int(self.headers.get("Content-Length", 0))
            if not 0 < size <= 8192:
                return self.send_error(400)
            data = json.loads(self.rfile.read(size))
            question = str(data["question"])[:500]
            name = str(data["name"])[:20]
            mood = int(data["happiness"])
            payload = {"model": os.environ["PET_MODEL"], "messages": [
                {"role": "system", "content": f"너는 차량용 게임의 펫 {name}야. 행복도는 {mood}/100, 배고픔은 {data.get('hunger')}, 아픈 상태는 {bool(data.get('sick'))}야. 한국어로 짧고 다정하게 대답해."},
                {"role": "user", "content": question}]}
            req = urllib.request.Request(os.environ["PET_COMPLETIONS_URL"], json.dumps(payload).encode(),
                {"Authorization": "Bearer " + os.environ["PET_API_KEY"], "Content-Type": "application/json"})
            with urllib.request.urlopen(req, timeout=25) as response:
                reply = json.load(response)["choices"][0]["message"]["content"]
            body = json.dumps({"reply": reply}).encode()
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(body)
        except (KeyError, ValueError, OSError, IndexError, TypeError):
            self.send_error(502, "Chat unavailable")


if __name__ == "__main__":
    ThreadingHTTPServer(("127.0.0.1", 8765), Chat).serve_forever()

