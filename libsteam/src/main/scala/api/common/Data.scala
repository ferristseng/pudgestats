package api.common.data

import scala.collection.mutable.MutableList

//
// Differents ways to represent the 
// data returned by the Dota2Api. Essentially
// either a Unit (SteamApiData) or Sequence of SteamApiData objects
//
sealed trait SteamApiDataObject

case class SteamApiDataSeq[T <: SteamApiData]() 
  extends MutableList[T] with SteamApiDataObject

abstract class SteamApiData 
  extends SteamApiDataObject
