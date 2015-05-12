package api.common.json

import scala.collection.mutable.MutableList

import com.google.gson.stream.{JsonReader, JsonWriter, JsonToken}
import com.google.gson.{TypeAdapter, JsonParseException}

import api.common.data._

//
// Generic TypeAdapter for a Steam data JSON Object
// Takes a type of [T <: SteamApiData], and creates an object of
// that type.
//
abstract class SteamGenericTypeAdapter[T <: SteamApiDataObject] 
  extends TypeAdapter[T] 
{ 
  def handleName(name: String, reader: JsonReader)
  def create(): T
  def reset() = { }

  override def read(reader: JsonReader): T = {
    if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
      reader.beginObject()
      while (!reader.peek().equals(JsonToken.END_OBJECT)) {
        if (reader.peek().equals(JsonToken.NAME)) {
          this.handleName(reader.nextName(), reader)
        } else { reader.skipValue() }
      }
      reader.endObject()
    }
    val parsed = this.create(); this.reset()
    parsed
  }

  override def write(writer: JsonWriter, value: T) = 
    throw new NotImplementedError()
}

// 
// Deserializes an array of objects corresponding to 
// a name in a JSON object.
// Example:
//    {
//      'matches': [ .. ]
//    }
//    
//    Use `new Dota2ApiInnerCollectionTypeAdapter(
//           "matches", new SteamApiDataCollectionTypeAdapter(
//              new MatchTypeAdapter()))`
//
class SteamApiInnerCollectionTypeAdapter[T <: SteamApiData](
    _name: String,
    _adapter: SteamApiDataCollectionTypeAdapter[T])
  extends SteamGenericTypeAdapter[SteamApiDataSeq[T]] 
{
  val name = _name
  val adapter = _adapter
  var list = new SteamApiDataSeq[T]()

  def handleName(name: String, reader: JsonReader) = name match {
    case this._name => this.adapter.read(reader) match {
      case l: SteamApiDataSeq[T]  => this.list = l
    }
    case _ => { reader.skipValue() }
  }

  def create(): SteamApiDataSeq[T] = this.list
}

//
// Deserializes a JSON array of objects all of the same type,
// using a specified TypeAdapter
//
class SteamApiDataCollectionTypeAdapter[T <: SteamApiData](
    _adapter: TypeAdapter[T]) 
  extends TypeAdapter[SteamApiDataSeq[T]]   
{
  val adapter = _adapter

  override def read(reader: JsonReader): SteamApiDataSeq[T] = {
    var list = new SteamApiDataSeq[T]()

    if (reader.peek().equals(JsonToken.BEGIN_ARRAY)) {
      reader.beginArray()
      while(!reader.peek().equals(JsonToken.END_ARRAY)) {
        if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
          list += this.adapter.read(reader)
        } else { reader.skipValue() }
      }
      reader.endArray()
    }

    list
  }

  // Not complete! Do not use!
  override def write(writer: JsonWriter, value: SteamApiDataSeq[T]) = 
    throw new NotImplementedError() 
}
