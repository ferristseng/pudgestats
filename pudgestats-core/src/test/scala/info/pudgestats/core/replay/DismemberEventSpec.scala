package info.pudgestats.core.replay

import org.scalatest.FunSuite

class DismemberEventSpec 
  extends FunSuite
{
  val dismEvtJsonString = 
    "{\"target\":\"dota_unknown\",\"illusion\":false,\"damage\":-1,\"timestamp\":765.334}"

  test("DismemberEvent should serialize properly") {
    val evt = new DismemberEvent("dota_unknown", false, -1, 765.334f)
    assert(evt.toJsonString == dismEvtJsonString)
  }
  
  test("DismemberEvent should deserialize properly") {
    val evt = (new DismemberEvent).fromJsonString(dismEvtJsonString)
    assert(evt.target == "dota_unknown")
    assert(!evt.illusion)
    assert(evt.damage == -1)
    assert(evt.timestamp == 765.334f)
  }
}