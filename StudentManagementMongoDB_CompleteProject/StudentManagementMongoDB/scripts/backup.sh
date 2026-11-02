#!/usr/bin/env bash
set -euo pipefail
# Set MONGODB_URI before running this script.
: "${MONGODB_URI:?Please set MONGODB_URI}"
mkdir -p backups
STAMP=$(date +%Y%m%d_%H%M%S)
mongodump --uri="$MONGODB_URI" --db=studentdb --out="backups/backup_$STAMP"
echo "Backup saved in backups/backup_$STAMP"
