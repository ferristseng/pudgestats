package info.pudgestats.core.replay

import info.pudgestats.core.json._

import com.google.gson.annotations.Expose

class Player(
    _hero: String,
    _name: String,
    _steamId: Long,
    _gameTeam: Int) 
  extends JsonSerializable
  with JsonDeserializable[Player]
{
  def this() = this("", "", 0.toLong, 2)

  @Expose val hero      = _hero
  @Expose val name      = _name
  @Expose val steamId   = _steamId
  @Expose val gameTeam  = _gameTeam

  override def toString = 
    s"<Player hero=$hero name=$name gameTeam=$gameTeam>"
}
