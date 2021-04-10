package org.jroots.queueing;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jroots.queueing.cli.SqsConsumer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

public class QueueConsumerApplication extends Application<QueueConsumerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new QueueConsumerApplication().run(args);
    }

    @Override
    public String getName() {
        return "QueueConsumer";
    }

    @Override
    public void initialize(final Bootstrap<QueueConsumerConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final QueueConsumerConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application

        var consumer = new SqsConsumer(threadPoolTaskExecutor());
        consumer.startConsuming();
    }

    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(25);
        executor.setMaxPoolSize(50);
        executor.setThreadNamePrefix("sqsExecutor");
        executor.initialize();
        return executor;
    }

}
