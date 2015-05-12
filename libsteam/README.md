# libsteam

A *partial* port of SteamKit2 in Scala.

Provides functionality to connect to Steam, and spoof a Dota client, but not much else. In other words, the port is incomplete!

In addition, includes a wrapper to a limited number of endpoints in the Steam API and Dota Web API.

## Setup

Building `.java` files requires `protoc`. Run `make` to build `.java` files.

## Updating `.proto`

Updating `.proto` files requires a small change (if updating using SteamKit2's repository).

`option java_package = "com.steam.proto"` or `option java_package = "com.dota.proto"` needs to be prepended
to the `proto` files in order for them to be properly seen. 

The `java_package` depends on where the `.java` file is outputted.

## Usage

TODO
