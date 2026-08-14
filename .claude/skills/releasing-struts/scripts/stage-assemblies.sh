#!/bin/sh
#
# Phase 3 - move the release assemblies from the closed Nexus staging repository
# into https://dist.apache.org/repos/dist/dev/struts/$VERSION so they can be tested
# and voted on.
#
# Usage:  cd "$(mktemp -d)" && VERSION=7.3.0 /path/to/stage-assemblies.sh
#
# Run it from a scratch directory, not from a repository checkout: it creates
# ./$VERSION and a temporary svn working copy in the current directory.
#
# Requires: the staging repository must already be CLOSED in Nexus (an open repo
# serves nothing under the staging *group* URL this fetches from), and your ASF
# svn credentials for dist.apache.org.

set -eu

if [ -z "${VERSION:-}" ]; then
    echo "VERSION is not set. Usage: VERSION=7.3.0 $0" >&2
    exit 1
fi

# Not cosmetic: a VERSION of "." resolves server-side to the parent directory,
# which would publish the whole staging tree.
case "$VERSION" in
    [0-9]*.[0-9]*.[0-9]*) ;;
    *)
        echo "VERSION must look like 7.3.0 (got '$VERSION')" >&2
        exit 1
        ;;
esac

STAGING_URL="https://repository.apache.org/content/groups/staging/org/apache/struts/struts2-assembly/$VERSION"
DIST_DEV_URL="https://dist.apache.org/repos/dist/dev/struts/"

if [ -e "$VERSION" ]; then
    echo "Directory $VERSION already exists here - remove it or run elsewhere." >&2
    exit 1
fi
if [ -e struts-dev ]; then
    echo "Directory struts-dev already exists here - remove it or run elsewhere." >&2
    exit 1
fi

# Unconditional, as in the original: a half-built working copy left behind can be
# picked up and committed by a later run for a different version.
cleanup() {
    rm -rf "$START_DIR/struts-dev"
}
START_DIR=$(pwd)
trap cleanup EXIT

echo "Creating working dir $VERSION"
mkdir "$VERSION"
cd "$VERSION"

echo "Getting distro $VERSION from the staging repository"
if ! wget -erobots=off -nv -l 1 --accept=zip,md5,sha1,asc -r --no-check-certificate -nd -nH "$STAGING_URL"; then
    echo "Download failed. Is the staging repository closed in Nexus?" >&2
    exit 1
fi

if ! ls ./*.zip >/dev/null 2>&1; then
    echo "No assemblies downloaded. Is the staging repository closed in Nexus?" >&2
    exit 1
fi

# struts2-assembly-7.3.0-all.zip -> struts-7.3.0-all.zip, and the same for the
# .asc/.md5/.sha1 beside each zip. The .pom files keep their name and are removed
# below - narrowing this glob without widening that one republishes them.
echo "Renaming files"
for f in *2-assembly*.zip*; do
    [ -e "$f" ] || continue
    mv "$f" "$(echo "$f" | sed s/2-assembly//g)"
done

echo "Removing unneeded files"
rm -f struts2-assembly-*.pom*
rm -f ./*.md5 ./*.sha1

# The ASF publishes sha256/sha512; Nexus only carries the legacy hashes.
echo "Generating SHA signatures"
for f in *.zip; do
    [ -f "$f" ] || continue
    shasum -a 256 "$f" > "$f.sha256"
    shasum -a 512 "$f" > "$f.sha512"
done

echo "Staging the following files:"
ls -1

cd "$START_DIR"

echo "Publishing artifacts for test"
svn --no-auth-cache co --depth empty "$DIST_DEV_URL" struts-dev
mv "$VERSION" struts-dev/
cd struts-dev
svn add --force ./
svn --no-auth-cache commit -m "Updates test release $VERSION"

cd "$START_DIR"

echo "Done - verify https://dist.apache.org/repos/dist/dev/struts/$VERSION/"
