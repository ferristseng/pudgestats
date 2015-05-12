package info.pudgestats.fetcher

import java.net.URL
import java.io.{FileOutputStream, File}
import java.nio.channels.Channels

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

object URLHelper {
  /** Builds the URL for a replay */
  def createReplayEndpoint(
    id: Int, 
    cluster: Int, 
    salt: Int)
  : String = {
    s"http://replay$cluster.valve.net/570/${id}_${salt}.dem.bz2?v=1" 
  }
}

class Downloader(_url: String, output: String) {
  val url = new URL(_url)
  val file = new File(output)

  def start = {
    this.file.createNewFile

    val rbc = Channels.newChannel(
      new BZip2CompressorInputStream(url.openStream))
    val stream = new FileOutputStream(this.file)

    stream.getChannel.transferFrom(rbc, 0, Long.MaxValue)
  }
}
