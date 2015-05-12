package info.pudgestats.fetcher

trait StringSerializable[T] {
  def serialize: String
  def deserialize(s: String): T
}

