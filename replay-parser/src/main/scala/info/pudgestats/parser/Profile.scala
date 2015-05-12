package info.pudgestats.parser

import skadistats.clarity.parser.{Profile => ClarityProfile}

import com.dota2.proto.DotaUsermessages

object Profile {
  val PARTICLES = (new ClarityProfile).dependsOn(ClarityProfile.USERMESSAGE_CONTAINER)
                                      .dependsOn(ClarityProfile.ENTITIES)
                                      .append(
                                        classOf[DotaUsermessages.CDOTAUserMsg_ParticleManager]
                                      )
}
