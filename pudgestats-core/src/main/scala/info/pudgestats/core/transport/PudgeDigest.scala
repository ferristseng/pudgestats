package info.pudgestats.core.transport

import info.pudgestats.core.json._
import info.pudgestats.core.replay._

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose

import scala.annotation.meta._

import java.util.ArrayList

/** The primary way that the fetcher and parser 
  * communicate is through this Digest. 
  *
  * Essentially, it is a compiled report of all 
  * the pudge related statistics that are captured 
  * during the parsing stage.
  *
  * Scala annotations can't be placed on traits, unless 
  * they have default impl. (it seems)
  */
abstract class PudgeJsonDigest[T <: PudgeJsonDigest[T]] 
  extends JsonSerializable 
  with JsonDeserializable[T] 
  with Digest
{
  /** These have to be vars, so they can be assigned in a subclass 
    * and because the Expose annotation does not work on non-implemented 
    * members. 
    */
  @Expose var matchTimestamp    = 0l
  @Expose var matchId           = 0
  @Expose var matchWinner       = 0
  @Expose var matchMode         = 0
  @Expose var matchDuration     = 0.0f 
  @Expose var parseTimestamp    = 0l

  @Expose var selfRotDamage     = 0
  @Expose var creepRotDamage    = 0
  @Expose var neutralRotDamage  = 0
  @Expose var heroRotDamage     = 0

  @Expose val players           = new ArrayList[Player] 
  @Expose val hookEvents        = new ArrayList[HookEvent] 
  @Expose val killEvents        = new ArrayList[KillEvent] 
  @Expose val dismEvents        = new ArrayList[DismemberEvent]
  @Expose val itemEvents        = new ArrayList[PlayerItemEvent]
  @Expose val abilityEvents     = new ArrayList[PlayerAbilityEvent]
  @Expose val deathEvents       = new ArrayList[DeathEvent]
  @Expose val runeEvents        = new ArrayList[RuneEvent]

  def toBytes: Array[Byte] = this.toJsonString.getBytes
}
