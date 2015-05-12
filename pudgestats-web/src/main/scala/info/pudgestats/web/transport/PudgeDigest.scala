package info.pudgestats.web.transport

import scala.collection.JavaConversions._

import com.google.gson.annotations.{Expose, SerializedName}

import info.pudgestats.core.replay.HookEvent
import info.pudgestats.core.transport.{
  PudgeJsonDigest => PudgeJsonDigestAbstract}
import info.pudgestats.web.util.HookEventsExtensionJava

/** Pudge Digest that server side knows how to consume
  * 
  * Ideally this should be synced up closely with the parser, but
  * doesn't have to be
  */
class PudgeJsonDigest 
  extends PudgeJsonDigestAbstract[PudgeJsonDigest]
  with HookEventsExtensionJava
{
  @Expose
  @SerializedName("version")
  val parserVersion = 0.0f

  @Expose
  val versionName = "DEFAULT" 
}
