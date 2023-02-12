#! /usr/bin/env bash
# From https://gist.github.com/april/ee2e104b1435f3113e67663d8875bbef
# attempting to be the most robust solution for outputting git log as JSON,
# using only `git` and the standard shell functions, without requiring
# additional software.

# - uses traditional JSON camelCase
# - includes every major field that git log can output, including the body
# - proper sections for author, committer, and signature
# - multiple date formats (one for reading, ISO for parsing)
# - should properly handle (most? all?) body values, even those that contain
#   quotation marks and escaped characters
# - outputs as minimized JSON, can be piped to `jq` for pretty printing
# - can run against the current directory as `git-log-json` or against a file
#   or folder with `git-log-json foo`
# - easily piped into `jq`, e.g. this will get all the commit subjects:
#   $ git-log-json foo | jq -r '.[] | .subject'

# credit to @nsisodiya, @varemenos, @overengineer, and others for the
# original working code:
# https://gist.github.com/varemenos/e95c2e098e657c7688fd

git-log-json() {
  IFS='' read -r -d '' FORMAT << 'EOF'
  {
  ^^^^author^^^^: { ^^^^name^^^^: ^^^^%aN^^^^,
    ^^^^email^^^^: ^^^^%aE^^^^,
    ^^^^date^^^^: ^^^^%aD^^^^,
    ^^^^dateISO8601^^^^: ^^^^%aI^^^^},
  ^^^^body^^^^: ^^^^%b^^^^,
  ^^^^commitHash^^^^: ^^^^%H^^^^,
  ^^^^commitHashAbbreviated^^^^: ^^^^%h^^^^,
  ^^^^committer^^^^: {
    ^^^^name^^^^: ^^^^%cN^^^^,
    ^^^^email^^^^: ^^^^%cE^^^^,
    ^^^^date^^^^: ^^^^%cD^^^^,
    ^^^^dateISO8601^^^^: ^^^^%cI^^^^},
  ^^^^encoding^^^^: ^^^^%e^^^^,
  ^^^^notes^^^^: ^^^^%N^^^^,
  ^^^^parent^^^^: ^^^^%P^^^^,
  ^^^^parentAbbreviated^^^^: ^^^^%p^^^^,
  ^^^^refs^^^^: ^^^^%D^^^^,
  ^^^^signature^^^^: {
    ^^^^key^^^^: ^^^^%GK^^^^,
  ^^^^signer^^^^: ^^^^%GS^^^^,
  ^^^^verificationFlag^^^^: ^^^^%G?^^^^},
  ^^^^subject^^^^: ^^^^%s^^^^,
  ^^^^subjectSanitized^^^^: ^^^^%f^^^^,
  ^^^^tree^^^^: ^^^^%T^^^^,
  ^^^^treeAbbreviated^^^^: ^^^^%t^^^^
  },
EOF
  FORMAT=$(echo $FORMAT|tr -d '\r\n ')

  git log --pretty=format:$FORMAT $1 | \
  sed -e ':a' -e 'N' -e '$!ba' -e s'/\^^^^},\n{\^^^^/^^^^},{^^^^/g' \
      -e 's/\\/\\\\/g' -e 's/"/\\"/g' -e 's/\^^^^/"/g' -e '$ s/,$//' | \
  sed -e ':a' -e 'N' -e '$!ba' -e 's/\r//g' -e 's/\n/\\n/g' -e 's/\t/\\t/g' | \
  awk 'BEGIN { ORS=""; printf("[") } { print($0) } END { printf("]\n") }'
}

git-log-json
