package info.pudgestats.core.transport

import com.rabbitmq.client.{ConnectionFactory, MessageProperties}

object RequiresRabbitMQQueue {
  val PersistMethod = MessageProperties.PERSISTENT_TEXT_PLAIN 
}

/** Shared components among Consumer / Transport
  * that use RabbitMQ
  */
abstract class RequiresRabbitMQQueue(
    username: String,
    host: String,
    password: String,
    port: Int,
    connectionTimeout: Int,
    protected val queueName: String)
{
  protected val factory = new ConnectionFactory

  this.factory.setUsername(username)
  this.factory.setHost(host)
  this.factory.setPassword(password)
  this.factory.setPort(port)
  this.factory.setConnectionTimeout(connectionTimeout)
  this.factory.setAutomaticRecoveryEnabled(true)
  this.factory.setTopologyRecoveryEnabled(true)
  this.factory.setNetworkRecoveryInterval(10000)

  private lazy val connection = this.factory.newConnection

  lazy val channel = {
    val channel = this.connection.createChannel
    channel.basicQos(1)
    channel.queueDeclare(this.queueName, true, false, false, null)
    channel
  }

  def close = { this.channel.close; this.connection.close }
}
