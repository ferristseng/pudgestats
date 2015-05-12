package info.pudgestats.core.transport

/** A consumer. Consumes what is sent in transport */
trait Consumer {
  def receive: Option[Array[Byte]]
  def ack
  def close
}
