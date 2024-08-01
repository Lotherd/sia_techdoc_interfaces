package trax.aero.util;

import java.util.logging.Logger;

import com.google.gson.Gson;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import trax.aero.logger.LogManager;
import trax.aero.pojo.json.OUTPUT;

public class SqsUtilities {

	static Logger logger = LogManager.getLogger("TechdocEDCO");
	
	public static Boolean sendResend(OUTPUT resend, String id ) {
		
		Gson gson = new Gson();
		String json;
		String queueUrlTo = System.getProperty("Techdoc_ToSQS");

		logger.info("Sending JSON To EDCO");

		try {
			json = gson.toJson(resend);
			logger.info("Request Body: " + json);
			
			SqsClient sqsClient = SqsClient.builder().build();
			
			SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder().queueUrl(queueUrlTo)
					.entries(SendMessageBatchRequestEntry.builder().id(id).messageBody(json).build())
		            .build();
		    sqsClient.sendMessageBatch(sendMessageBatchRequest);
		     
		    return true;
		}catch(Exception e){
			logger.info(e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	
}
