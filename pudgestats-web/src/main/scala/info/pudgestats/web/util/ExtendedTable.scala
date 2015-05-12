package info.pudgestats.web.util

import java.sql.SQLException

import org.squeryl.{KeyedEntity, Table} 
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean

/** ExtendedTable needs entities that have some way of 
  * telling if a duplicate exists or not
  */
trait HasDuplicate[T] {
  def dbEquiv(other: T): LogicalBoolean
}

/** Squeryl Table with additional functionality.
  * 
  * Requires the HasDuplicate trait to be extended by the 
  * entity, to support some functionality
  */
class ExtendedTable[K, T <: HasDuplicate[T]](
    t: Table[T])(
    implicit manifestT: Manifest[T]) 
  extends Table[T](t.name) 
{
  /** Check if an equivalent exists */
  def equivExists(entity: T) = this.where(e => entity.dbEquiv(e)).headOption

  /** Does an insert, if the entity isn't in the 
    * database
    */
  def insertOrIgnore(entity: T) = 
    this.equivExists(entity) match {
      case Some(e) => e
      case None => this.insert(entity)
    }
}

