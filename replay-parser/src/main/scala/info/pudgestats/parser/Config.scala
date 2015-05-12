package info.pudgestats.parser

import com.typesafe.config.ConfigFactory

import java.io.File

import info.pudgestats.core.conf.{TypesafeConfigWrapper, RabbitConfig}

/** Configuration class 
  *
  * Parses a configuration file at a specific file path
  * throwing a ParseException if not found or if 
  * a mandatory config option is not specified.
  */
class Config(path: String)
  extends TypesafeConfigWrapper
  with RabbitConfig
{
  val conf        = ConfigFactory.parseFile(new File(path))
  val saveQueue   = conf.getString("save_queue")
  val parseQueue  = conf.getString("parse_queue")
}
