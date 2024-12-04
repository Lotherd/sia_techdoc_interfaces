package trax.aero.util;

import java.util.UUID;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import trax.aero.pojo.json.OUTPUT;

public class SqsUtilities {
	
	public static Boolean sendResend(OUTPUT resend, String id ) throws Exception{
		
		Gson gson = new Gson();
		String json;
		String queueUrlTo = System.getProperty("Techdoc_ToSQS");

		 System.out.println("Sending JSON To EDCO");

		try {
			json = gson.toJson(resend);
			
			System.out.println("Request Body: " + json);
			AmazonSQS sqs = AmazonSQSClientBuilder.standard()
	                .withRegion(Regions.AP_SOUTHEAST_1)  // Specify the region here
	                .build(); 

			 String queueUrl = sqs.getQueueUrl(queueUrlTo).getQueueUrl();
			 
			SendMessageRequest send_msg_request = new SendMessageRequest()
			        .withQueueUrl(queueUrl)
			        .withMessageBody(json)
			        .withMessageGroupId(UUID.randomUUID().toString())
			        .withMessageDeduplicationId(UUID.randomUUID().toString());
			SendMessageResult result =  sqs.sendMessage(send_msg_request);
			System.out.println("after sending a message we get message id "+ result.getMessageId() );
			
		    return true;
		}catch(Exception e){
			System.err.println(e.toString());
			throw e;
		}
	}
	
	
}
