package info.pudgestats.core.replay

import org.scalatest.FunSuite

class KillEventSepc 
  extends FunSuite
{
  val killEvtJsonString = "{\"target\":\"dota_unknown\",\"timestamp\":0.0}"

  test("KillEvent should serialize properly") {
    val evt = new KillEvent("dota_unknown", 0.0f)
    assert(evt.toJsonString == killEvtJsonString)
  }
  
  test("KillEvent should deserialize properly") {
    val evt = (new KillEvent).fromJsonString(killEvtJsonString)
    assert(evt.timestamp == 0.0)
    assert(evt.target == "dota_unknown")
  }
}