package com.study.springboot.activemq.config;

import com.study.springboot.activemq.Queues;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import javax.jms.DeliveryMode;
import javax.jms.Session;

@Component
@EnableJms
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String brokerUsername;

    @Value("${spring.activemq.password}")
    private String brokerPassword;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setPassword(brokerUsername);
        connectionFactory.setUserName(brokerPassword);
        connectionFactory.setTrustAllPackages(true);

        //Config Redelivery Policy in Redelivery Policy Map
        ActiveMQQueue queueOrdered = new ActiveMQQueue(Queues.QUEUE_ORDERED);
        RedeliveryPolicy qp10Seconds = new RedeliveryPolicy();
        qp10Seconds.setInitialRedeliveryDelay(10000);
        qp10Seconds.setUseCollisionAvoidance(true);
        qp10Seconds.setRedeliveryDelay(10000);
        qp10Seconds.setUseExponentialBackOff(false);
        qp10Seconds.setMaximumRedeliveries(1);
        qp10Seconds.setDestination(queueOrdered);

        RedeliveryPolicyMap rdMap = connectionFactory.getRedeliveryPolicyMap();
        rdMap.put(queueOrdered, qp10Seconds);

        connectionFactory.setRedeliveryPolicyMap(rdMap);

        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        template.setDeliveryMode(DeliveryMode.PERSISTENT);
        return template;
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager() {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(connectionFactory());
        return jmsTransactionManager;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsTransactionalContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        factory.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable throwable) {

            }
        });
        return factory;
    }
}