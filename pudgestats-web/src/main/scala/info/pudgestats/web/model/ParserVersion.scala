package info.pudgestats.web.model

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}

import info.pudgestats.web.util.{ExtendedTable, HasDuplicate}

/** Info regarding the parser used */
class ParserVersion(
    val id: Int,
    val versionNum: String,
    val versionName: String) 
  extends KeyedEntity[Int]
  with HasDuplicate[ParserVersion]
{
  def this() = this(0, "0", "DEFAULT")
  def this(num: String, name: String) = this(0, num, name)

  def dbEquiv(other: ParserVersion) = 
    this.versionNum === other.versionNum and 
    this.versionName === other.versionName
  
  override def toString = 
    s"<Parser id=$id versionNum=$versionNum versionName=$versionName>"
}

object ParserVersionsStore extends Schema {
  val parserVersions = new ExtendedTable[Int, ParserVersion](table[ParserVersion])

  on(parserVersions)(v => declare(
    columns(v.versionNum, v.versionName) are unique
  ))
}
