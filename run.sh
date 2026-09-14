#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
case "${1:-}" in
  '') main=br.com.patternsshop.Main ;;
  --test) main=br.com.patternsshop.CheckoutTest ;;
  *) echo 'Uso: sh run.sh [--test]' >&2; exit 2 ;;
esac
mkdir -p build/classes
find src/main/java -name '*.java' > build/sources.txt
if [ "${1:-}" = '--test' ]; then
  find src/test/java -name '*.java' >> build/sources.txt
fi
javac --release 17 -encoding UTF-8 -d build/classes @build/sources.txt
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/classes "$main"
