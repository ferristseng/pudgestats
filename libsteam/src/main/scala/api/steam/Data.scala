package api.steam.data

import api.common.data._

case class SteamUser(
    val steamId: String,
    val name: String,
    val profileUrl: String,
    val avatar: String,
    val avatarMedium: String,
    val avatarFull: String)
  extends SteamApiData {
  
  override def toString = s"<SteamUser id='$steamId' name='$name)'>"
}
