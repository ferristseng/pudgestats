package info.pudgestats.core.transport

import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}

/** Consumer backed by RabbitMQQueue */
class RabbitMQQueueConsumer(
    username: String         = ConnectionFactory.DEFAULT_USER,
    host: String             = ConnectionFactory.DEFAULT_HOST,
    password: String         = ConnectionFactory.DEFAULT_PASS,
    port: Int                = ConnectionFactory.DEFAULT_AMQP_PORT, 
    connectionTimeout: Int   = ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT,
    queueName: String        = "",
    val deliveryTimeout: Int = 5000)
  extends RequiresRabbitMQQueue(
    username, 
    host, 
    password, 
    port, 
    connectionTimeout, 
    queueName)
  with Consumer 
{
  private var lastDeliveryTag = 0l

  private lazy val queueingConsumer = {
    val consumer = new QueueingConsumer(this.channel)
    this.channel.basicConsume(this.queueName, false, consumer) 
    consumer
  }

  /** Wait 1500 ms for a delivery, if not received by then
    * return None
    *
    * TODO: Make the timeout configurable
    */
  def receive: Option[Array[Byte]] = {
    val msg = this.queueingConsumer.nextDelivery(this.deliveryTimeout)

    if (msg != null) {
      this.lastDeliveryTag = msg.getEnvelope.getDeliveryTag
      Some(msg.getBody) 
    } else None
  }

  def ack = this.channel.basicAck(lastDeliveryTag, false)
}
