package info.pudgestats.core.replay

import org.scalatest.FunSuite

class HookEventSpec 
  extends FunSuite
{
  val hookEvtJsonString = 
    "{\"target\":\"dota_unknown\",\"hit\":true,\"illusion\":false,\"timestamp\":45.1917}"

  test("HookEvent should serialize properly") {
    val evt = new HookEvent("dota_unknown", true, false, 45.1917f)
    assert(evt.toJsonString == hookEvtJsonString)
  }
  
  test("HookEvent should deserialize properly") {
    val evt = (new HookEvent).fromJsonString(hookEvtJsonString)
    assert(evt.target == "dota_unknown")
    assert(evt.hit)
    assert(!evt.illusion)
    assert(evt.timestamp == 45.1917f)
  }
}