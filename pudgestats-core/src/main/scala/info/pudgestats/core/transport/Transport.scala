package info.pudgestats.core.transport

/** Sends a array of bytes across some 
  * transport layer.
  */
trait Transport {
  def send(data: Digest)
  def close
}
