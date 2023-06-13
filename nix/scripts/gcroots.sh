#!/usr/bin/env bash

set -Ee

_NIX_GCROOTS="${_NIX_GCROOTS:-/nix/var/nix/gcroots/per-user/${USER}/status-mobile}"

GIT_ROOT=$(cd "${BASH_SOURCE%/*}" && git rev-parse --show-toplevel)
source "${GIT_ROOT}/nix/scripts/source.sh"
source "${GIT_ROOT}/scripts/colors.sh"

TARGET="${1}"
shift
if [[ -z "${TARGET}" ]]; then
    echo -e "${RED}No target specified for gcroots.sh!${RST}" >&2
    exit 1
fi

config=''
if [[ "${CI}" == "true" ]]; then
    config+="status-im.ci-build=true;"
else
    config+="status-im.ci-build=false;"
fi
if [[ -n "$config" ]]; then
    nixArgs+=("--arg config {$config}")
fi

# Creates a symlink to derivation in _NIX_GCROOTS directory.
# This prevents it from being removed by 'gc-collect-garbage'.
nix-instantiate --attr "${TARGET}" \
    --add-root "${_NIX_GCROOTS}/${TARGET}" \
    ${nixArgs[@]} \
    "${@}" "${GIT_ROOT}/default.nix" >/dev/null
