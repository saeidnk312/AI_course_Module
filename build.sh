#!/usr/bin/env bash
# Build, test and run the Roman numeral converter (JDK 17+, Maven for tests).
#   ./build.sh          compile + run tests + build roman.jar
#   ./build.sh test     compile + run tests only
#   ./build.sh run      build, then start the interactive CLI
#   ./build.sh gui      build, then open the Swing window
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out/main
javac -Xlint:all -Werror -d out/main $(find src/main/java -name '*.java')
mvn -q -B test          # JUnit 5 suite (downloads JUnit on first run)

[[ "${1:-}" == "test" ]] && exit 0

jar --create --file roman.jar --main-class roman.RomanCli -C out/main .
echo "Built roman.jar"

case "${1:-}" in
  run) java -jar roman.jar ;;
  gui) java -jar roman.jar --gui ;;
esac
