package net.kaleidos.groovytint.worker

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer

import magick.ImageInfo
import magick.MagickImage

import net.kaleidos.groovytint.ImageTransformer

import groovy.json.JsonSlurper

class Worker {
    private final conn
    private final channel
    private final consumer
    private final processes
    private final slurper = new JsonSlurper()
    private final transformer
    private config

    Worker(config) {
        def factory = new ConnectionFactory()
        factory.username = config.worker.rabbitMQ.get('username', 'quest')
        factory.password = config.worker.rabbitMQ.get('password', 'quest')
        factory.virtualHost = config.worker.rabbitMQ.get('virtualHost', '/')
        factory.host = config.worker.rabbitMQ.get('host', 'localhost')
        factory.port = config.worker.rabbitMQ.get('port', 5672)

        conn = factory.newConnection()

        channel = conn.createChannel()

        def queueName = config.worker.rabbitMQ.get('queue', 'groovyTint')
        channel.queueDeclare(queueName, true, false, false, [:])

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
        print 'Message wait main loop'
        while (true) {
            println 'Wating message...'
            delivery = consumer.nextDelivery()
            msg = slurper.parseText(new String(delivery.body, 'UTF-8'))
            processMessage(msg)
            channel.basicAck(delivery.envelope.deliveryTag, false)
        }
    }

    protected void finalize() {
        channel.close()
        conn.close()
    }
}

static void main(String[] args) {
    config = new ConfigSlurper().parse(new File('groovyTintWorker.properties').toURL())
    def worker = new Worker(config)
    worker.mainLoop()
}
