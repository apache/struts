#!/bin/sh
#
# Phase 5 - promote a release that passed its vote, moving the assemblies from
# dist/dev to dist/release. This is the point at which the artifacts start
# replicating to the mirrors.
#
# Usage:  VERSION=7.3.0 ./promote-dist.sh
#
# Run it only after the vote has passed. Wait 24 hours after this before
# announcing anything - the announcement links a download page that the mirrors
# have to have caught up with first.

set -eu

if [ -z "${VERSION:-}" ]; then
    echo "VERSION is not set. Usage: VERSION=7.3.0 $0" >&2
    exit 1
fi

# Not cosmetic. svn resolves a "." path element instead of rejecting it, so
# VERSION="." would move the whole of dist/dev/struts into dist/release in one
# irreversible server-side commit. ".." is rejected by svn; "." is not.
case "$VERSION" in
    [0-9]*.[0-9]*.[0-9]*) ;;
    *)
        echo "VERSION must look like 7.3.0 (got '$VERSION')" >&2
        exit 1
        ;;
esac

svn mv "https://dist.apache.org/repos/dist/dev/struts/$VERSION/" \
       "https://dist.apache.org/repos/dist/release/struts/" \
       -m "Release Struts $VERSION"

echo "Done - verify https://dist.apache.org/repos/dist/release/struts/$VERSION/"
echo "Now release the staging repository in Nexus, then wait 24 hours before announcing."
