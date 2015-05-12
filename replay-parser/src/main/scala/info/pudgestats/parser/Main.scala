package info.pudgestats.parser

import java.io.IOException

import com.typesafe.config.ConfigException

import org.slf4j.LoggerFactory

import org.apache.commons.cli.{Option, DefaultParser, Options, 
  HelpFormatter, ParseException}

import info.pudgestats.core.transport.{RabbitMQQueueTransport, 
  RabbitMQQueueConsumer}

/** Launcher for the Replay Parser
  *
  * Depending on the arguments passed, launching the 
  * replay parser will either parse a single replay, 
  * or launch the parser, which receives paths to replays,
  * parses them, and pushes the digest to the server.
  */
object ReplayParserMain extends App {
  /** Options available to the program */
  object ProgramOptions {
    val Help        = Option.builder("h")
                            .longOpt("help")
                            .desc("display help and exit")
                            .build
    val ConfFile    = Option.builder("c")
                            .hasArg
                            .longOpt("conf")
                            .desc("configuration file to use")
                            .build
    val ReplayFile  = Option.builder("r")
                            .hasArg
                            .longOpt("replay")
                            .desc("replay file to parse")
                            .build
    val Verbose     = Option.builder("v")
                            .longOpt("verbose")
                            .desc("print output")
                            .build
  }

  val logger  = LoggerFactory.getLogger("info.pudgestats.parser")
  val opts    = new Options
  val parser  = new DefaultParser
  val helpf   = new HelpFormatter

  opts.addOption(ProgramOptions.Help)
  opts.addOption(ProgramOptions.ConfFile)
  opts.addOption(ProgramOptions.ReplayFile)
  opts.addOption(ProgramOptions.Verbose)

  try {
    val cmd = parser.parse(opts, args)
    
    if (cmd.hasOption("h")) {
      helpf.printHelp("replay-parser", opts)
    } else {
      val comm = if (cmd.hasOption("c")) {
        val conf = new Config(cmd.getOptionValue("c"))
        Some(
          (new RabbitMQQueueTransport(
             conf.rabbitUser,
             conf.rabbitHost,
             conf.rabbitPass,
             conf.rabbitPort,
             conf.rabbitTimeout,
             conf.saveQueue),
           new RabbitMQQueueConsumer(
             conf.rabbitUser,
             conf.rabbitHost,
             conf.rabbitPass,
             conf.rabbitPort,
             conf.rabbitTimeout,
             conf.parseQueue)))
      } else {
        None
      }

      if (cmd.hasOption("r")) {
        val replay = cmd.getOptionValue("r")

        logger.info(s"Parsing single replay...$replay")
        
        val digest = comm match {
          case Some(pair) => 
            new Prog(pair._2, pair._1).parseReplayAndSend(replay)
          case None => Prog.parseReplay(replay)
        }

        if (cmd.hasOption("v")) logger.info(digest.toPrettyJsonString)
      } else {        
        comm match {
          case Some(pair) => new Prog(pair._2, pair._1).run
          case None => throw new ConfigException.Missing("No config file specified")
        }
      }

      comm match {
        case Some(pair) => pair._1.close; pair._2.close
        case None => 
      }
    }
  } catch {
    case ex: ParseException   => logger.error(s"Parse Exception: $ex")
    case ex: IOException      => logger.error(s"IO Exception: $ex"); ex.printStackTrace
    case ex: ConfigException  => logger.error(s"Configuration parse exception: $ex") 
  }
}

