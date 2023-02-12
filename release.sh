#! /usr/bin/env bash

set -e

rm -rf public/js/*

npx shadow-cljs release app

cp public/index-template.html public/index.html

REV=$(git rev-parse HEAD)

sed -i "s/{rev}/$REV/g" public/index.html

# upload
scp -r public/* user@136.243.78.134:/home/user/portal

ssh -t user@136.243.78.134 'sudo cp -rT portal/ /websites/portal/ && sudo chown -R caddy:caddy /websites/'

rm public/index.html

