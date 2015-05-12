package info.pudgestats.fetcher

import info.pudgestats.core.transport.RabbitMQQueueTransport

import com.typesafe.config.ConfigException

import org.slf4j.LoggerFactory

import org.apache.commons.cli.{Option, DefaultParser, Options, 
  HelpFormatter, ParseException}

import scala.collection.JavaConversions._

/** Replay Fetcher Program 
  *
  * A command line interface to start a program to
  * continually fetch new matches from the 
  * Dota 2 WebAPI, and download replays from a 
  * spoofed Steam Client.
  *
  * Usage:
  *   usage: replay-fetcher
  *    -c,--conf <arg>   configuration file to use
  *    -h,--help         display help and exit
  */
object ReplayFetcher extends App {
  /** Options available to the program */
  object ProgramOptions {
    val help      = Option.builder("h")
                          .longOpt("help")
                          .desc("display help and exit")
                          .build
    val confFile  = Option.builder("c")
                          .hasArg
                          .longOpt("conf")
                          .desc("configuration file to use")
                          .build
  }

  val logger = LoggerFactory.getLogger("info.pudgestats.fetcher")

  val opts    = new Options
  val parser  = new DefaultParser
  val helpf   = new HelpFormatter

  opts.addOption(ProgramOptions.help)
  opts.addOption(ProgramOptions.confFile)
    
  val cmd = parser.parse(opts, args)

  if (cmd.hasOption("h")) {
    helpf.printHelp("replay-fetcher", opts)
  } else {
    try {
      val conf = if (cmd.hasOption("c")) {
        new Config(cmd.getOptionValue("c")) 
      } else {
        throw new ParseException("Required a config file")
      }

      logger.info("=== BEGIN CONFIG ===")
      logger.info(s"Steam Users:      [${conf.steamUsers.size}]")
      logger.info(s"Steam Timeout:    ${conf.steamTimeout}")
      logger.info(s"Steam Retry:      ${conf.steamRetry}")
      logger.info(s"Steam Reconnect:  ${conf.steamReconnect}")
      logger.info(s"Steam API Key:    ${conf.steamApiKey}")
      logger.info(s"Batch Size:       ${conf.batchSize}")
      logger.info(s"Download Int.     ${conf.downloadInterval}")
      logger.info(s"Queue Name:       ${conf.parseQueue}")
      logger.info(s"Dota Regions:     ${conf.dotaRegions}")
      logger.info(s"Dota Modes:       ${conf.dotaModes}")
      logger.info(s"Dota Lobby:       ${conf.dotaLobbies}")
      logger.info(s"Dota Heroes:      ${conf.dotaHeroes}")
      logger.info(s"Dota Abandon Th.: ${conf.dotaAbandonThresh}")
      logger.info(s"Web API Res. #:   ${conf.webapiResultNum}")
      logger.info(s"Web API Interval: ${conf.webapiInterval}")
      logger.info(s"Replay Output:    ${conf.replayOutput}")
      logger.info(s"Replay Prefix:    ${conf.replayPrefix}")
      logger.info(s"RabbitMQ User:    ${conf.rabbitUser}")
      logger.info(s"RabbitMQ Host:    ${conf.rabbitHost}")
      logger.info(s"RabbitMQ Port:    ${conf.rabbitPort}")
      logger.info(s"RabbitMQ Timeout: ${conf.rabbitTimeout}")
      logger.info("==== END CONFIG ====")

      val filters = List(
        new PlayerMatchFilter(conf.dotaHeroes.toSet),
        new LobbyMatchFilter(conf.dotaLobbies.toSet),
        new RegionMatchFilter(conf.dotaRegions.toSet),
        new ModeMatchFilter(conf.dotaModes.toSet),
        new AbandonMatchFilter(conf.dotaAbandonThresh),
        new PlayerNumFilter(10)
      )
      val matchFinder = new FilteredMatchFinder(
        conf.steamApiKey,
        conf.webapiResultNum,
        filters)
      val accountScheduler = new BasicAccountScheduler(
        conf.steamUsers.map { 
          case (usr, pass, handle) => new SteamAccount(usr, pass, handle) 
        }.toList, conf.batchSize)
      val transport = new RabbitMQQueueTransport(
        conf.rabbitUser,
        conf.rabbitHost,
        conf.rabbitPass,
        conf.rabbitPort,
        conf.rabbitTimeout,
        conf.parseQueue)

      new Prog(conf, matchFinder, accountScheduler, transport).start
    } catch {
      case ex: ParseException   => logger.error(s"Parse Exception: $ex") 
      case ex: ConfigException  => logger.error(s"Configuration parse exception: $ex") 
    }
  }
}
