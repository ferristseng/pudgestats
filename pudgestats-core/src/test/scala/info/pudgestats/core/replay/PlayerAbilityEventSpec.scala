package info.pudgestats.core.replay

import org.scalatest.FunSuite

class PlayerAbiliyEventSepc 
  extends FunSuite
{
  val abilityEvtJsonString = 
    "{\"name\":\"attribute_bonus\",\"level\":8,\"timestamp\":0.0}"

  test("AbilityEvent should serialize properly") {
    val evt = new PlayerAbilityEvent("attribute_bonus", 8, 0.0f, 0)
    assert(evt.toJsonString == abilityEvtJsonString)
  }
  
  test("AbilityEvent should deserialize properly") {
    val evt = (new PlayerAbilityEvent).fromJsonString(abilityEvtJsonString)
    assert(evt.name == "attribute_bonus")
    assert(evt.level == 8)
    assert(evt.timestamp == 0.0)
    assert(evt.entityHandle == 0) // Default Value
  }
}