package info.pudgestats.web.transport

import info.pudgestats.core.conf.RabbitConfig
import info.pudgestats.core.transport.RabbitMQQueueConsumer

/** Creates the RabbitMQ consumer with a config file 
  * and the name of a Queue
  */
trait RabbitMQConsumerSupport {
  protected val conf: RabbitConfig
  protected val queueName: String
  protected lazy val consumer = new RabbitMQQueueConsumer(
    conf.rabbitUser, conf.rabbitHost, conf.rabbitPass,
    conf.rabbitPort, conf.rabbitTimeout, this.queueName)
}
