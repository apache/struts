#!/bin/sh
#
# Phase 3 - move the release assemblies from the closed Nexus staging repository
# into https://dist.apache.org/repos/dist/dev/struts/$VERSION so they can be tested
# and voted on.
#
# Usage:  VERSION=7.3.0 ./stage-assemblies.sh
#
# Requires: the staging repository must already be CLOSED in Nexus (an open repo
# serves nothing under the staging *group* URL this fetches from), and your ASF
# svn credentials for dist.apache.org.
#
# Ported from the release manager's local toolbox
# (~/Projects/Apache/minatour/bin/update-struts2-assemblies.sh). Behaviour is
# unchanged except:
#   - set -eu, so a failed step stops the run instead of committing a partial set
#   - $VERSION is required up front rather than producing an empty directory
#   - the md5/sha1 cleanup no longer fails when there is nothing to remove
#   - the staged file list is printed before the commit

set -eu

if [ -z "${VERSION:-}" ]; then
    echo "VERSION is not set. Usage: VERSION=7.3.0 $0" >&2
    exit 1
fi

STAGING_URL="https://repository.apache.org/content/groups/staging/org/apache/struts/struts2-assembly/$VERSION"
DIST_DEV_URL="https://dist.apache.org/repos/dist/dev/struts/"

if [ -e "$VERSION" ]; then
    echo "Directory $VERSION already exists here - remove it or run elsewhere." >&2
    exit 1
fi

echo "Creating working dir $VERSION"
mkdir "$VERSION"
cd "$VERSION"

echo "Getting distro $VERSION from the staging repository"
wget -erobots=off -nv -l 1 --accept=zip,md5,sha1,asc -r --no-check-certificate -nd -nH "$STAGING_URL"

if ! ls ./*.zip >/dev/null 2>&1; then
    echo "No assemblies downloaded. Is the staging repository closed?" >&2
    exit 1
fi

# struts2-assembly-7.3.0-all.zip -> struts-7.3.0-all.zip
echo "Renaming files"
for f in *2-assembly*; do
    [ -e "$f" ] || continue
    mv "$f" "$(echo "$f" | sed s/2-assembly//g)"
done

echo "Removing unneeded files"
rm -f struts-"$VERSION"*.pom*
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

cd ..

echo "Publishing artifacts for test"
svn --no-auth-cache co --depth empty "$DIST_DEV_URL" struts-dev
mv "$VERSION" struts-dev/
cd struts-dev
svn add --force ./
svn --no-auth-cache commit -m "Updates test release $VERSION"

cd ..
rm -rf struts-dev

echo "Done - verify https://dist.apache.org/repos/dist/dev/struts/$VERSION/"
