package info.pudgestats.core.transport

import com.rabbitmq.client.ConnectionFactory

/** Transport mechanism backed by RabbitMQ */
class RabbitMQQueueTransport(
    username: String       = ConnectionFactory.DEFAULT_USER,
    host: String           = ConnectionFactory.DEFAULT_HOST,
    password: String       = ConnectionFactory.DEFAULT_PASS,
    port: Int              = ConnectionFactory.DEFAULT_AMQP_PORT, 
    connectionTimeout: Int = ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT,
    queueName: String      = "")
  extends RequiresRabbitMQQueue(
    username, 
    host, 
    password, 
    port, 
    connectionTimeout, 
    queueName)
  with Transport 
{
  def send(data: Digest) = {
    this.channel.basicPublish("", this.queueName, 
      RequiresRabbitMQQueue.PersistMethod, data.toBytes)
  }
}
