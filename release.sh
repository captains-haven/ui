#! /usr/bin/env bash

set -e

rm -rf public/js/*

npx shadow-cljs release app

cp public/index-template.html public/index.html

REV=$(git rev-parse HEAD)
VERSION=$(cat package.json | jq -r .version)
LAST_UPDATE=$(date -u)
NODE_VERSION=$(node --version)
NPM_VERSION=$(npm --version)
SHADOW_CLJS_VERSION=$(cat package-lock.json | jq -r '.dependencies."shadow-cljs".version')

echo $REV
echo $VERSION
echo $LAST_UPDATE
echo $NODE_VERSION
echo $NPM_VERSION
echo $SHADOW_CLJS_VERSION

sleep 5

sed -i "s/{rev}/$REV/g" public/index.html
sed -i "s/{version}/$VERSION/g" public/index.html
sed -i "s/{last_update}/$LAST_UPDATE/g" public/index.html
sed -i "s/{node_version}/$NODE_VERSION/g" public/index.html
sed -i "s/{npm_version}/$NPM_VERSION/g" public/index.html
sed -i "s/{shadow_cljs_version}/$SHADOW_CLJS_VERSION/g" public/index.html

# upload
scp -r public/* user@136.243.78.134:/home/user/portal

ssh -t user@136.243.78.134 'sudo cp -rT portal/ /websites/portal/ && sudo chown -R caddy:caddy /websites/'

rm public/index.html

