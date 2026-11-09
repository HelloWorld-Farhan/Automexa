#!/usr/bin/env python3
import sys
import re
import subprocess
from pathlib import Path

GRADLE_FILE = Path("app/build.gradle.kts")
README_FILE = Path("README.md")

def bump_version(bump_type="patch"):
    if not GRADLE_FILE.exists():
        print(f"Error: {GRADLE_FILE} not found. Run from project root.")
        sys.exit(1)

    content = GRADLE_FILE.read_text(encoding="utf-8")

    # Match versionCode and versionName
    code_match = re.search(r'versionCode\s*=\s*(\d+)', content)
    name_match = re.search(r'versionName\s*=\s*"([^"]+)"', content)

    if not code_match or not name_match:
        print("Error: Could not locate versionCode or versionName in build.gradle.kts")
        sys.exit(1)

    old_code = int(code_match.group(1))
    old_name = name_match.group(1)

    parts = [int(p) for p in old_name.split(".")]
    while len(parts) < 3:
        parts.append(0)

    major, minor, patch = parts[0], parts[1], parts[2]

    if bump_type == "major":
        major += 1
        minor = 0
        patch = 0
    elif bump_type == "minor":
        minor += 1
        patch = 0
    else:  # default patch
        patch += 1

    new_name = f"{major}.{minor}.{patch}"
    new_code = old_code + 1

    print(f"⚡ Bumping Automexa Version: v{old_name} (code {old_code}) ➔ v{new_name} (code {new_code})")

    # Update app/build.gradle.kts
    content = re.sub(r'versionCode\s*=\s*\d+', f'versionCode = {new_code}', content)
    content = re.sub(r'versionName\s*=\s*"[^"]+"', f'versionName = "{new_name}"', content)
    GRADLE_FILE.write_text(content, encoding="utf-8")

    # Update README.md badge
    if README_FILE.exists():
        readme = README_FILE.read_text(encoding="utf-8")
        readme = re.sub(r'Version-[0-9\.]+-blue', f'Version-{new_name}-blue', readme)
        README_FILE.write_text(readme, encoding="utf-8")

    print(f" Updated {GRADLE_FILE} and {README_FILE}")
    return new_name

if __name__ == "__main__":
    b_type = sys.argv[1] if len(sys.argv) > 1 else "patch"
    bump_version(b_type)

