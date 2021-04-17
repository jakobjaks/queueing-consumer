package org.jroots.queueing.cli;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SqsConsumer {

    private final String sqsUrl;
    private final AmazonSQS amazonSQSClient;
    private final MetricRegistry metrics = new MetricRegistry();
    private final Counter counter = Counter.build().namespace("queue_cluster").name("outgoing_messages").help("my counter").register();

    private final Executor executor;

    private final Logger logger = LoggerFactory.getLogger(SqsConsumer.class);

    public SqsConsumer(Executor executor) {
        try {
            new HTTPServer(9091);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.executor = executor;
        sqsUrl = "https://sqs.us-east-1.amazonaws.com/328945660164/rate_limiter_exit_queue";
        amazonSQSClient = AmazonSQSClientBuilder.standard().withRegion("us-east-1").build();
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
    }

    public void startConsuming() {
        ThreadPoolExecutor pollExecutor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        pollExecutor.submit(() -> {
            logger.info("===STARTED CONSUMING===");
            while (true) {
                try {
                    var request = new ReceiveMessageRequest().withMaxNumberOfMessages(10).withWaitTimeSeconds(20).withQueueUrl(sqsUrl);
                    var messages = amazonSQSClient.receiveMessage(request).getMessages();
                    logger.info("Made request to SQS");
                    for (var message : messages) {
                        executor.execute(() -> {
                            counter.inc();
                            logger.info("NEW MESSAGE: {}", message.getBody());
                            deleteMessage(message.getReceiptHandle());
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deleteMessage(String receiptHandle) {
        amazonSQSClient.deleteMessage(sqsUrl, receiptHandle);
    }

}
