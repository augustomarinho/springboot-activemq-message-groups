package com.study.springboot.activemq.consumers;

import com.study.springboot.activemq.ActiveMQSender;
import com.study.springboot.activemq.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class QueueB implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQSender.class);

    @JmsListener(destination = Queues.QUEUE_B,
            concurrency = "10-10",
            containerFactory = "jmsTransactionalContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(Message message) {
        try {
            String redeliveryCount = message.getStringProperty("JMSXDeliveryCount");
            String jmxGroupId = message.getStringProperty("JMSXGroupID");
            String msg = ((TextMessage) message).getText();
            logger.info("Receiving message {} from queue {} [RedeliveryCount={}, MessageID={}, JMSXGroupID={}]", msg, Queues.QUEUE_ORDERED, redeliveryCount, message.getJMSMessageID(), jmxGroupId);

            if (message != null && msg.startsWith("error-")) {
                throw new Exception("Forcing reading jms message problems for message read in queue " + Queues.QUEUE_ORDERED);
            }

        } catch (Exception e) {
            logger.error("Problems for consuming messagem from queue {}.", Queues.QUEUE_B);
            throw new RuntimeException(e.getMessage());
        }
    }
}