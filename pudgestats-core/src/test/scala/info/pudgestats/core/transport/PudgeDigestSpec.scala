package info.pudgestats.core.transport

import org.scalatest.FunSuite

import scala.io.Source

class PudgeDigestSpec 
  extends FunSuite
{
  private class PudgeJsonDigestConcrete 
    extends PudgeJsonDigest[PudgeJsonDigestConcrete]

  test("PudgeDigest should deserialize properly") {
    val file = Source.fromURL(getClass.getResource("/digest1.json"))
    val json = file.toBuffer.mkString
    val digest = (new PudgeJsonDigestConcrete).fromJsonString(json)

    assert(digest.selfRotDamage == 8863)
    assert(digest.creepRotDamage == 6946)
    assert(digest.neutralRotDamage == 0)
    assert(digest.heroRotDamage == 8050)
    assert(digest.players.size == 10)
    assert(digest.hookEvents.size > 0)
    assert(digest.killEvents.size > 0)
    assert(digest.itemEvents.size > 0)
    assert(digest.abilityEvents.size > 0)
    assert(digest.deathEvents.size > 0)
    assert(digest.runeEvents.size > 0)
    assert(digest.dismEvents.size > 0)
  }
}
