#!/usr/bin/env bash

# Run with ./package.sh 0.0.2
# requires zip, curl, jq and yarn

set -e

version="$1"

# create release directories
rm -rf ./build
mkdir -p build/pf2e-kingmaker-tools/

# create archive
node ./scripts/update-module-json.mjs "$version"
cp module.json token-map.json README.md LICENSE CHANGELOG.md OpenGameLicense.md build/pf2e-kingmaker-tools/
cp -r img/ packs/ docs/ templates/ styles/ dist/ build/pf2e-kingmaker-tools/

cd build
zip -r release.zip pf2e-kingmaker-tools
cd -
