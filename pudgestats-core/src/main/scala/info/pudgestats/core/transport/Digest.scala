package info.pudgestats.core.transport

/** A digest is sent to a third party as bytes, which 
  * can then be parsed / reconstructed.
  */
trait Digest {
  def toBytes: Array[Byte]
}