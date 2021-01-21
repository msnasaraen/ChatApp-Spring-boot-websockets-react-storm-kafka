package com.psaw.chat.controller;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.psaw.chat.configuration.KafkaConstants;
import com.psaw.chat.configuration.Message;

@RestController
@RequestMapping("/chat")
@CrossOrigin("http://localhost:3000")
public class ChatController {

	@Autowired
    private Producer<String,String> producer;

	@Autowired
    SimpMessagingTemplate template;
	
    @PostMapping(value = "/sendMessage", consumes = "application/json", produces = "application/json")
    public void sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());
        try {
            //Sending the message to kafka topic queue
        	producer.send(new ProducerRecord<String, String>("chatApp",new Gson().toJson(message)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @PostMapping(value = "/pushMessage")
    public void listen(@RequestBody Message message) {
        System.out.println("sending via kafka listener..");
        template.convertAndSend("/topic/group", message);
    }

	
}
