package info.pudgestats.core.replay

import org.scalatest.FunSuite

class PlayerItemEventSepc 
  extends FunSuite
{
  val itemEvtJsonString = 
    "{\"name\":\"town_portal_scroll\",\"timestamp\":10.1234}"

  test("PlayerItemEvent should serialize properly") {
    val evt = new PlayerItemEvent("town_portal_scroll", 10.1234f, 0)
    assert(evt.toJsonString == itemEvtJsonString)
  }
  
  test("PlayerItemEvent should deserialize properly") {
    val evt = (new PlayerItemEvent).fromJsonString(itemEvtJsonString)
    assert(evt.name == "town_portal_scroll")
    assert(evt.timestamp == 10.1234f)
    assert(evt.entityHandle == 0) // Default Value
  }
}