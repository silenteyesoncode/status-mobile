# for passing build options, see nix/README.md
{ config ? { } , system ? builtins.currentSystem}:

let
  main = import ./nix { inherit config system; };
in {
  # this is where the --attr argument selects the shell or target
  inherit (main) pkgs targets shells;
  inherit (main.pkgs) config;
}
