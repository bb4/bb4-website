#!/usr/bin/env python3
"""Inject Cloudflare Web Analytics beacon into HTML files before deploy."""

from __future__ import annotations

import os
import re
import sys
from pathlib import Path

BEACON_MARKER = "static.cloudflareinsights.com/beacon.min.js"
GTAG_BLOCK = re.compile(
    r"(<!-- Google tag \(gtag\.js\) -->.*?</script>\s*<script>.*?</script>\s*)",
    re.DOTALL | re.IGNORECASE,
)
HEAD_TAG = re.compile(r"(<head[^>]*>\s*)", re.IGNORECASE)


def beacon_snippet(token: str) -> str:
    return (
        "<!-- Cloudflare Web Analytics -->\n"
        "<script defer src='https://static.cloudflareinsights.com/beacon.min.js' "
        f"data-cf-beacon='{{\"token\": \"{token}\"}}'></script>\n"
    )


def inject(content: str, token: str) -> tuple[str, bool]:
    if BEACON_MARKER in content:
        return content, False

    snippet = beacon_snippet(token)
    match = GTAG_BLOCK.search(content)
    if match:
        insert_at = match.end()
        return content[:insert_at] + snippet + content[insert_at:], True

    match = HEAD_TAG.search(content)
    if match:
        insert_at = match.end()
        return content[:insert_at] + snippet + content[insert_at:], True

    return content, False


def main() -> int:
    token = os.environ.get("CF_WEB_ANALYTICS_TOKEN", "").strip()
    if not token:
        print("CF_WEB_ANALYTICS_TOKEN is not set.", file=sys.stderr)
        return 1

    root = Path(sys.argv[1] if len(sys.argv) > 1 else "source/html/barrybecker4")
    if not root.is_dir():
        print(f"Directory not found: {root}", file=sys.stderr)
        return 1

    injected = 0
    skipped = 0
    for path in sorted(root.rglob("*.html")):
        content = path.read_text(encoding="utf-8")
        updated, changed = inject(content, token)
        if changed:
            path.write_text(updated, encoding="utf-8")
            injected += 1
        else:
            skipped += 1

    print(f"Cloudflare Web Analytics: injected {injected} file(s), skipped {skipped} file(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
