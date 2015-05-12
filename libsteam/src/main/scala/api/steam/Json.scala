package api.steam.json

import com.google.gson.stream.{JsonReader, JsonWriter, JsonToken}
import com.google.gson.{TypeAdapter, JsonParseException}

import api.steam.data._
import api.common.data._
import api.common.json._

//
// The TypeAdapter for a SteamUser
//
class SteamUserTypeAdapter
  extends SteamGenericTypeAdapter[SteamUser] {
  var steamId       = ""
  var name          = ""
  var profileUrl    = ""
  var avatar        = ""
  var avatarMedium  = ""
  var avatarFull    = ""

  def handleName(name: String, reader: JsonReader) = name match {
    case "steamid"        => { this.steamId = reader.nextString }
    case "personaname"    => { this.name = reader.nextString }
    case "profileurl"     => { this.profileUrl = reader.nextString }
    case "avatar"         => { this.avatar = reader.nextString }
    case "avatarmedium"   => { this.avatarMedium = reader.nextString }
    case "avatarfull"     => { this.avatarFull = reader.nextString }
    case _                => { reader.skipValue }
  }

  def create = 
    new SteamUser(this.steamId, this.name, 
      this.profileUrl, this.avatar,
      this.avatarMedium, this.avatarFull)
}
