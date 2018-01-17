package com.study.springboot.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    private static final int MAX_QUEUE_CONSUMERS = 10;

    @Autowired
    private ActiveMQSender mqSender;

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String query(@RequestParam("queue") String queue,
                        @RequestParam("message") String message,
                        @RequestParam("cycleID") int cycleId,
                        @RequestParam("size") int size,
                        @RequestParam("forceErrorPosition") int errorPosition) {
        try {
            if (size <= 0) {
                mqSender.sendMessage(queue, message, getPartition(cycleId));
            } else {
                for (int i = 0; i < size; i++) {
                    if (errorPosition >= 0 && errorPosition == i) {
                        mqSender.sendMessage(queue, "error-" + message + "-" + i, getPartition(cycleId));
                    } else {
                        mqSender.sendMessage(queue, message + "-" + i, getPartition(cycleId));
                    }
                    Thread.sleep(10);
                }
            }
            return "OK";
        } catch (Exception e) {
            return "NOK";
        }
    }

    private String getPartition(int cycleId) {
        return Integer.toString(cycleId % MAX_QUEUE_CONSUMERS);
    }
}