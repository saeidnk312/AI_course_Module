#!/usr/bin/env bash
# Build, test and run the Roman numeral converter using only the JDK (17+).
#   ./build.sh          compile + run tests + build roman.jar
#   ./build.sh test     compile + run tests only
#   ./build.sh run      build, then start the interactive CLI
#   ./build.sh gui      build, then open the Swing window
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out/main out/test
javac -Xlint:all -Werror -d out/main $(find src/main/java -name '*.java')
javac -Xlint:all -Werror -cp out/main -d out/test src/test/java/roman/RomanNumeralConverterTest.java
java -cp out/main:out/test roman.RomanNumeralConverterTest

[[ "${1:-}" == "test" ]] && exit 0

jar --create --file roman.jar --main-class roman.RomanCli -C out/main .
echo "Built roman.jar"

case "${1:-}" in
  run) java -jar roman.jar ;;
  gui) java -jar roman.jar --gui ;;
esac
