#!/usr/bin/env bash
set -euo pipefail
: "${MONGODB_URI:?Please set MONGODB_URI}"
if [ -z "${1:-}" ]; then
  echo "Usage: ./scripts/restore.sh backups/backup_YYYYMMDD_HHMMSS"
  exit 1
fi
mongorestore --uri="$MONGODB_URI" --drop "$1/studentdb"
echo "Restore completed from $1"
