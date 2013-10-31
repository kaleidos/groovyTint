@Grapes(
    @Grab(group='com.rabbitmq', module='amqp-client', version='3.1.4')
)

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import groovy.json.JsonSlurper

class Worker {
    def conn
    def channel
    def config
    def consumer

    Worker(config) {
        def factory = new ConnectionFactory()
        factory.username = config.worker.rabbitMQ.get('username', 'guest')
        factory.password = config.worker.rabbitMQ.get('password', 'guest')
        factory.virtualHost = config.worker.rabbitMQ.get('virtualHost', '/')
        factory.host = config.worker.rabbitMQ.get('host', 'localhost')
        factory.port = config.worker.rabbitMQ.get('port', 5672)

        def queueName = config.worker.rabbitMQ.get('queue', 'groovyTint')

        conn = factory.newConnection()

        channel = conn.createChannel()

        channel.queueDeclare(queueName, true, false, false, new HashMap())

        consumer = new QueueingConsumer(channel)
        channel.basicConsume(queueName, false, consumer)
    }

    void processMessage(msg) {
        println msg
    }

    void mainLoop() {
        def slurper = new JsonSlurper()
        def delivery
        def msg
        print "Message wait main loop"
        while (true) {
            println "Wating message..."
            delivery = consumer.nextDelivery()
            msg = slurper.parseText(new String(delivery.body, "UTF-8"))
            processMessage(msg)
            channel.basicAck(delivery.envelope.deliveryTag, false)
        }
    }

    void finalize() {
        channel.close()
        conn.close()
    }
}

config = new ConfigSlurper().parse(new File('groovyTintWorker.properties').toURL())
def worker = new Worker(config)
worker.mainLoop()
