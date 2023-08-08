package com.oneHealth.Video.controller;

import java.util.ArrayList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@CrossOrigin("*")
public class MainController 
{
	ArrayList<String> users = new ArrayList<String>();

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    ObjectMapper objectMapper; 

    
 
    @MessageMapping("/testServer")
    @SendTo("/topic/testServer")
    public String testServer(String Test){
        System.out.println("Testing Server");
        return Test;
    }
    
    
    
    
    


    @MessageMapping("/addUser")
    public void addUser(String user){
        System.out.println("Adding User");
        users.add(user);
        for (String u :users) {
            System.out.println(u);
        }
        System.out.println("User Added Successfully");
    }
    
    
    
    
    
    
    
    @MessageMapping("/leave")
    public void leave(String participantId) {
        System.out.println("Participant with ID: " + participantId + " is leaving the meeting.");

        // Remove the leaving participant from the users list
        users.remove(participantId);

        // Broadcast the "leave" message to all participants except the sender
        try {
            String leaveMessage = objectMapper.writeValueAsString(participantId);
            for (String user : users) {
                if (!user.equals(participantId)) {
                    simpMessagingTemplate.convertAndSendToUser(user, "/topic/leave", leaveMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    
    
    @MessageMapping("/call")
    public void Call(String call){
        JSONObject jsonObject = new JSONObject(call);
        System.out.println("Calling to: "+jsonObject.get("callTo")+" Call from "+jsonObject.get("callFrom"));
        System.out.println("Calling to class: "+jsonObject.get("callTo").getClass()+" Call from class "+jsonObject.get("callFrom").getClass());
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("callTo"),"/topic/call",jsonObject.get("callFrom"));
    }
    
//    @MessageMapping("/call")
//    public void Call(String call) {
//        JSONObject jsonObject = new JSONObject(call);
//        String callTo = jsonObject.getString("callTo");
//        String callFrom = jsonObject.getString("callFrom");
//
//        System.out.println("Calling to: " + callTo + " Call from " + callFrom);
//
//        // Notify the callee (2nd person) about the call request
//        simpMessagingTemplate.convertAndSendToUser(callTo, "/topic/call", callFrom);
//    }
    
    
    
    

    @MessageMapping("/offer")
    public void Offer(String offer){

        System.out.println("Offer Came");
        JSONObject jsonObject = new JSONObject(offer);
        System.out.println(jsonObject.get("offer"));
        System.out.println(jsonObject.get("toUser"));
        System.out.println(jsonObject.get("fromUser"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/offer",offer);
        System.out.println("Offer Sent");
    }
    
    
    
    

    @MessageMapping("/answer")
    public void Answer(String answer){
        System.out.println("Answer came");
        System.out.println(answer);
        JSONObject jsonObject = new JSONObject(answer);
        System.out.println(jsonObject.get("toUser"));
        System.out.println(jsonObject.get("fromUser"));
        System.out.println(jsonObject.get("answer"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/answer",answer);
        System.out.println("Answer Sent");
    }
    
//    @MessageMapping("/answer")
//    public void Answer(String answer) {
//        JSONObject jsonObject = new JSONObject(answer);
//        String toUser = jsonObject.getString("toUser");
//        String fromUser = jsonObject.getString("fromUser");
//        String answerText = jsonObject.getString("answer");
//
//        System.out.println("Answer came from: " + fromUser + " to: " + toUser);
//
//        // Notify the caller (1st person) about the call acceptance
//        //simpMessagingTemplate.convertAndSendToUser(toUser, "/topic/answer", answer);
//        
//    }
    
    
    
    
    @MessageMapping("/candidate")
    public void Candidate(String candidate){
        System.out.println("Candidate came");
        JSONObject jsonObject = new JSONObject(candidate);
        System.out.println(jsonObject.get("toUser"));
        System.out.println(jsonObject.get("fromUser"));
        System.out.println(jsonObject.get("candidate"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/candidate",candidate);
        System.out.println("Candidate Sent");


    }
}
