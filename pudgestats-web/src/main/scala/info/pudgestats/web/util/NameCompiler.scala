package info.pudgestats.web.util

import com.dota.data.{Hero, Building, Creep, Neutral, DotaEntity}
import com.dota.helper.NameHelper

object NameCompiler {
  def compileUnitEntityName(name: String) = 
    if (NameHelper.isHero(name)) {
      Hero.fromString(name).get
    } else if (NameHelper.isBuilding(name)) {
      Building.fromString(name).get
    } else if (NameHelper.isCreep(name)) {
      Creep.fromString(name).get
    } else if (NameHelper.isNeutral(name)) {
      Neutral.fromString(name).get
    } else {
      new DotaEntity("Unknown", "dota_unknown")
    }
}
