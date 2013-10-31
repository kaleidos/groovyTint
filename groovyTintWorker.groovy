System.setProperty("jmagick.systemclassloader","false")

@Grab(group='com.rabbitmq', module='amqp-client', version='3.1.4')
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

@Grab(group='jmagick', module='jmagick', version='6.6.9')
import magick.ImageInfo
import magick.MagickImage

import net.kaleidos.groovytint.ImageTransformer

import groovy.json.JsonSlurper

class Worker {
    private def conn
    private def channel
    private def config
    private def consumer
    private def processes
    private def slurper = new JsonSlurper()
    private def transformer

    Worker(config) {
        def factory = new ConnectionFactory()
        factory.username = config.worker.rabbitMQ.get('username', 'guest')
        factory.password = config.worker.rabbitMQ.get('password', 'guest')
        factory.virtualHost = config.worker.rabbitMQ.get('virtualHost', '/')
        factory.host = config.worker.rabbitMQ.get('host', 'localhost')
        factory.port = config.worker.rabbitMQ.get('port', 5672)

        conn = factory.newConnection()

        channel = conn.createChannel()

        def queueName = config.worker.rabbitMQ.get('queue', 'groovyTint')
        channel.queueDeclare(queueName, true, false, false, new HashMap())

        consumer = new QueueingConsumer(channel)
        channel.basicConsume(queueName, false, consumer)

        processes = slurper.parseText(new File(config.worker.processes.get('filename', 'examples.json')).text)

        transformer = new ImageTransformer()
    }

    void processMessage(msg) {
        println msg
        ImageInfo ii = new ImageInfo(msg.input)
        MagickImage mi = new MagickImage(ii)
        mi = transformer.transform(mi, processes."${msg.process}")
        mi.fileName = msg.output
        mi.writeImage(ii)
    }

    void mainLoop() {
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
