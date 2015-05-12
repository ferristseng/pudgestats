package info.pudgestats.core.replay

import org.scalatest.FunSuite

class RunePickupEventSepc 
  extends FunSuite
{
  val runeEvtJsonString = 
    "{\"rune\":\"modifier_haste_rune\",\"timestamp\":9.517}"

  test("RuneEvent should serialize properly") {
    val evt = new RuneEvent("modifier_haste_rune", 9.517f)
    assert(evt.toJsonString == runeEvtJsonString)
  }
  
  test("RuneEvent should deserialize properly") {
    val evt = (new RuneEvent).fromJsonString(runeEvtJsonString)
    assert(evt.rune == "modifier_haste_rune")
    assert(evt.timestamp == 9.517f)
  }
}