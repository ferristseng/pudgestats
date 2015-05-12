package info.pudgestats.core.json

import com.google.gson.GsonBuilder

/** Mixin to allow support for Gson */
protected trait RequiresGson {
  protected def gsonBase = 
    (new GsonBuilder)
      .excludeFieldsWithoutExposeAnnotation
  
  protected lazy val gsonDef    = gsonBase.create
  protected lazy val gsonPretty = gsonBase.setPrettyPrinting.create
}

/** Mixin to allow for serializing of the object.
  * Requires explicit Expose annotation of fields.
  */
trait JsonSerializable extends RequiresGson {
  def toJsonString = this.gsonDef.toJson(this)
  def toJsonElement = this.gsonDef.toJsonTree(this)
  def toPrettyJsonString = this.gsonPretty.toJson(this)
}

/** Mixin to allow for deserializing of the object.
  * Also requires explicit Expose annotatinos of fields
  */
trait JsonDeserializable[T] extends RequiresGson {
  def fromJsonString(s: String): T = this.gsonDef.fromJson(s, this.getClass)
}
