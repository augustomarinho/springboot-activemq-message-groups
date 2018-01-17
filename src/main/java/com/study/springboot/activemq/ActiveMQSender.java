package com.study.springboot.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class ActiveMQSender {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQSender.class);

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public void sendMessage(final String queueName, String message, String partition) {

        logger.debug("Sending for queue {} a message {}", queueName, message);

        jmsTemplate.send(queueName, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message jmsMessage = session.createTextMessage(message);
                jmsMessage.setStringProperty("JMSXGroupID", partition);
                return jmsMessage;
            }
        });
    }
}