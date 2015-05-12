package info.pudgestats.core.replay

import org.scalatest.FunSuite

class DeathEventSpec 
  extends FunSuite
{
  val deathEvtJsonString = 
    "{\"attacker\":\"dota_unknown\",\"illusion\":false,\"timestamp\":15.784}"

  test("DeathEvent should serialize properly") {
    val evt = new DeathEvent("dota_unknown", false, 15.784f)
    assert(evt.toJsonString == deathEvtJsonString)
  }
  
  test("DeathEvent should deserialize properly") {
    val evt = (new DeathEvent).fromJsonString(deathEvtJsonString)
    assert(evt.attacker == "dota_unknown")
    assert(evt.timestamp == 15.784f)
    assert(!evt.illusion)
  }
}