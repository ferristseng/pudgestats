package com.steam.const

import scala.util.Random
import com.google.common.net.HostAndPort

object ServerList {
  private var randGen = new Random()
  val servers = List(
    // These are only servers in Seattle, 
    // but more are available
    "72.165.61.174:27017",
    "72.165.61.174:27018",
    "72.165.61.175:27017",
    "72.165.61.175:27018",
    "72.165.61.176:27017",
    "72.165.61.176:27018",
    "72.165.61.185:27017",
    "72.165.61.185:27018",
    "72.165.61.187:27017",
    "72.165.61.187:27018",
    "72.165.61.188:27017",
    "72.165.61.188:27018",
    // Inteliquent Luxembourg
    "146.66.152.12:27017",
    "146.66.152.12:27018",
    "146.66.152.12:27019",
    "146.66.152.13:27017",
    "146.66.152.13:27018",
    "146.66.152.13:27019",
    "146.66.152.14:27017",
    "146.66.152.14:27018",
    "146.66.152.14:27019",
    "146.66.152.15:27017",
    "146.66.152.15:27018",
    "146.66.152.15:27019"
  )
  def randomServer = 
    HostAndPort.fromString(servers(randGen.nextInt(servers.size)))
}
