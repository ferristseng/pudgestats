package info.pudgestats.parser

import skadistats.clarity.model.{StringTable, Entity}
import skadistats.clarity.`match`.EntityCollection

trait StringTableApplicable {
  def applyTable(t: StringTable)(i: Int) = t.getNameByIndex(i)
}

/** Parsed Message
  *
  * High level messages that occur in the replay. These are STATIC, 
  * not dynamic like the data types found in the replay. 
  * This MIGHT not work with older replays...and will have to 
  * updated in the future if the replay format. 
  */
sealed trait ParsedMessage

object CombatMessageType extends Enumeration {
  type CombatMessageType    = Value
  val Unknown               = Value(-1)
  val Damage                = Value(0)
  val Healing               = Value(1)
  val BuffDebuffApplied     = Value(2)
  val BuffDebuffRemoved     = Value(3)
  val Death                 = Value(4)
  val UsesSkill             = Value(5)
  val UsesItem              = Value(6)
  val Location              = Value(7)
  val Gold                  = Value(8)
  val GameState             = Value(9)
  val XP                    = Value(10)
  val Purchase              = Value(11)
  val Buyback               = Value(12)
  
  def fromInt(i: Int)       = i match {
    case 0  | 1  | 2  | 3  | 4 |
         5  | 6  | 7  | 8  | 9 | 
         10 | 11 | 12 => CombatMessageType(i)
    case _            => CombatMessageType(-1)
  }
}

case class CombatLogMessage(
    val kind: CombatMessageType.CombatMessageType,
    val source: Int,
    val target: Int,
    val attacker: Int,
    val inflictor: Int,
    val isAttackerIllusion: Boolean,
    val isTargetIllusion: Boolean,
    val value: Int,
    val health: Int,
    val timestamp: Float,
    val targetSource: Int)(
    val stringTable: StringTable) 
  extends ParsedMessage 
  with StringTableApplicable
{
  private def $ = this.applyTable(stringTable)_

  lazy val sourceName        = this $ source
  lazy val targetName        = this $ target
  lazy val attackerName      = this $ attacker
  lazy val inflictorName     = this $ inflictor
  lazy val targetSourceName  = this $ targetSource

  override def toString = 
    s"<CombatLogMessage kind=$kind source=$sourceName target=$targetName " +  
    s"attacker=$attackerName inflictor=$inflictorName targetSource=$targetSourceName " + 
    s"value=$value health=$health timestamp=$timestamp>"
}
  
case class PlayerMessage(
    val playerEntity: Entity,
    val timestamp: Float,
    private val entities: EntityCollection)
  extends ParsedMessage
{
  def inventory = Const.ItemPropertyNames.map {
    case name => this.entities.getByHandle(this.playerEntity.getProperty[Int](name))
  }.filter(ent => ent != null)

  def abilities = Const.AbilityPropertyNames.map {
    case name => this.entities.getByHandle(this.playerEntity.getProperty[Int](name))
  }.filter(ent => ent != null)
}

case class UnknownMessage(
    val msg: com.google.protobuf.GeneratedMessage) 
  extends ParsedMessage

case class RawMessage(
    val msg: com.google.protobuf.GeneratedMessage)
  extends ParsedMessage
