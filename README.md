# pudgestats

_(Deprecated)_ - Unfortunately, there are just too many games being played to make this 
application feasible. At one point, even with extremely strict filters, I was downloading gigabytes worth of data each day. Also, this application technically break's the EULA for Steam. I mostly put the source code up for posterity, so enjoy!

## Projects

  * `libsteam` - Partial SteamKit2 port in Scala, and partial Steam / Dota 2 Web API wrapper
  * `pudgestats-core` - Utilities, traits, etc. shared among all pudgestats projects
  * `pudgestats-web` - pudgestats website -- written using Scalatra
  * `replay-parser` - pudgestats parser process. Uses skadistats for parsing
  * `replay-fetcher` - pudgestats replay fetcher. Depends on `libsteam`. Highly configurable (potentially usable outside of pudgestats).
  
## Setup

Setup should be pretty straight forward. Use `sbt` to build and install dependencies. 

`pudgestats-web`, `replay-parser`, `replay-fetcher` communicate 
across `rabbitmq`. 

`pudgestats-web` DB is backed by `postgresql`.
