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
import com.oneHealth.Video.DTO.ChatMessage;

import ch.qos.logback.core.net.SyslogOutputStream;


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
    public void addUser(String payload){
        System.out.println("Adding User");
        JSONObject jsonObject = new JSONObject(payload);

        String myId = jsonObject.getString("myId");
        String remoteId = jsonObject.getString("remoteId");
        String role = jsonObject.getString("role");
        users.add(myId);
        
        
        System.out.println("Role is ..."+role);
        System.out.println(users.contains(remoteId));
        System.out.println(!(users.contains(remoteId)));
        
        
        if(role.equals("Doctor")&& users.contains(remoteId)) {
        	simpMessagingTemplate.convertAndSendToUser(myId, "/topic/is-patient-joined", true);
        }else if(role.equals("Doctor")&& !(users.contains(remoteId))) {
        	simpMessagingTemplate.convertAndSendToUser(myId, "/topic/is-patient-joined", false);
        	
        }else {
        	simpMessagingTemplate.convertAndSendToUser(remoteId, "/topic/patient-joined", true);
        }
    }
    
    
    
    
    
    
    
    @MessageMapping("/leave")
    public void leave(String participantIds) {
        
    	JSONObject jsonObject = new JSONObject(participantIds);
    	String myId = jsonObject.getString("myId");
        String remoteId = jsonObject.getString("remoteId");
    	
        System.out.println("Removing User with ID : "+myId);
        users.removeIf(element -> element.equals(myId));
        System.out.println("Modified List: " + users);
        simpMessagingTemplate.convertAndSendToUser(remoteId,"/topic/callEnded",myId);
    }

    
    
    
    
    @MessageMapping("/call")
    public void Call(String call){
        JSONObject jsonObject = new JSONObject(call);
        System.out.println("Calling to: "+jsonObject.get("callTo")+" Call from "+jsonObject.get("callFrom"));
        System.out.println("Calling to class: "+jsonObject.get("callTo").getClass()+" Call from class "+jsonObject.get("callFrom").getClass());
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("callTo"),"/topic/call",jsonObject.get("callFrom"));
    }
    
    
    
    
    @MessageMapping("/call-request")
    public void Call_Request(String call){
        JSONObject jsonObject = new JSONObject(call);
        System.out.println("Calling to: "+jsonObject.get("callTo")+" Call from "+jsonObject.get("callFrom"));
        System.out.println("Calling to class: "+jsonObject.get("callTo").getClass()+" Call from class "+jsonObject.get("callFrom").getClass());
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("callTo"),"/topic/call-request",jsonObject.get("callFrom"));
    }
    
    
    @MessageMapping("/call-rejected")
    public void Call_Rejected(String rejectionMessage) {
    	JSONObject jsonObject = new JSONObject(rejectionMessage);
        System.out.println("Call Rejected By "+jsonObject.get("rejectedBy")+" Message To "+jsonObject.get("messageTo"));
        System.out.println("Call rejected By class: "+jsonObject.get("rejectedBy").getClass()+" Message To class "+jsonObject.get("messageTo").getClass());
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("messageTo"),"/topic/call-rejected",jsonObject.get("rejectedBy"));
    }
    
    
    @MessageMapping("/call-accept")
    public void callAccept(String callAccept) {
        JSONObject jsonObject = new JSONObject(callAccept);
        String caller = jsonObject.getString("caller");
        String callee = jsonObject.getString("callee");

        // Create a room or session for the call
        // You can use a library like WebRTC adapter or Jitsi for handling rooms

        // Notify the caller and callee about the call acceptance
        simpMessagingTemplate.convertAndSendToUser(caller, "/topic/call-accepted", callee);
        simpMessagingTemplate.convertAndSendToUser(callee, "/topic/call-accepted", caller);
    }


  
    
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
    

        
    @MessageMapping("/candidate")
    public void Candidate(String candidate){
        System.out.println("Candidate came");
        JSONObject jsonObject = new JSONObject(candidate);
        System.out.println(jsonObject.get("toUser"));
        System.out.println(jsonObject.get("fromUser"));
        System.out.println("Candidate Received..........");
        System.out.println(jsonObject.get("candidate"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/candidate",candidate);
        System.out.println("Candidate Sent");


    }
    
    
    @MessageMapping("/sendMessage")
    public void sendMessage(String message) {
        // Handle the reminder message here
        // You can broadcast the message to all connected clients
    	System.out.println("Remainder Received : "+message);
        simpMessagingTemplate.convertAndSend("/topic/reminder", message);
    }
    
    
    @MessageMapping("/recordRequest")
    public void getRecordingRequest(String details) {
    	System.out.println("Recording request came");
        JSONObject jsonObject = new JSONObject(details);
        System.out.println(jsonObject.get("toUser"));
        System.out.println(jsonObject.get("fromUser"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/recordRequest",jsonObject.get("fromUser"));
        System.out.println("Record request sent");
    }
    
    
    @MessageMapping("/recordRequestAcceptance")
    public void patientAcceptedRecordRequest(String acceptance) {
    	System.out.println("Recording request acceptance came");
        JSONObject jsonObject = new JSONObject(acceptance);
        System.out.println(jsonObject.get("toUser"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/recordRequestAcceptance","Accepted");
        System.out.println("Acceptance Sent");
    }
    
    
    @MessageMapping("/recordingStopped")
    public void recordingStoppedNotification(String message) {
    	System.out.println("Recording stopped message came");
        JSONObject jsonObject = new JSONObject(message);
        System.out.println(jsonObject.get("toUser"));
        simpMessagingTemplate.convertAndSendToUser(jsonObject.getString("toUser"),"/topic/recordingStopped","Stopped");
        System.out.println("Recording Stopped message Sent");
    }
    
    
    
    
    
    
}
