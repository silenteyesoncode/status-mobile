# This file controls the pinned version of nixpkgs we use for our Nix environment
# as well as which versions of package we use, including their overrides.
{ config ? { } }:

let
  inherit (import <nixpkgs> { }) fetchFromGitHub;

  # For testing local version of nixpkgs
  #nixpkgsSrc = (import <nixpkgs> { }).lib.cleanSource "/home/jakubgs/work/nixpkgs";

  # We follow the master branch of official nixpkgs.
  nixpkgsSrc = fetchFromGitHub {
    name = "nixpkgs-source";
    # FIXME: Fork used to get Cocoapods 1.12.0 and apksigner macOS build.
    owner = "status-im";
    repo = "nixpkgs";
    rev = "d0c06fa3d3982a91aa01bd63ed84020cbde3d3ab";
    sha256 = "sha256-8blvuUHnuf0hFr/PpBxVohJp5CaGXIXhgJlFN/cv7us=";
    # To get the compressed Nix sha256, use:
    # nix-prefetch-url --unpack https://github.com/${ORG}/nixpkgs/archive/${REV}.tar.gz
  };

  # Status specific configuration defaults
  defaultConfig = import ./config.nix;
  inherit config;
  mergedConfig = defaultConfig // config;

  # Override some packages and utilities
  pkgsOverlay = import ./overlay.nix;

  # Override system for local Apple Silicon builds (see PR-16237)
  ci-build = if (builtins.hasAttr "status-im" mergedConfig) && (builtins.hasAttr "ci-build" mergedConfig.status-im)
  then
    builtins.getAttr "ci-build" mergedConfig.status-im
  else
    "false";

  system = if !ci-build && builtins.currentSystem == "aarch64-darwin"
  then
    "x86_64-darwin"
  else
    builtins.currentSystem;
in
  # import nixpkgs with a config override
  (import nixpkgsSrc) {
    config = mergedConfig;
    overlays = [ pkgsOverlay ];
    inherit system;
  }
