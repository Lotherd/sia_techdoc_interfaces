package trax.aero.util;

import java.util.UUID;

import org.tinylog.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import trax.aero.pojo.json.OUTPUT;

public class SqsUtilities {

    public static Boolean sendResend(OUTPUT resend, String id) throws Exception {

        Gson gson = new Gson();
        String json;
        String queueUrlTo = System.getProperty("Techdoc_ToSQS");

        Logger.info("Sending JSON To EDCO");

        try {
            json = gson.toJson(resend);

            Logger.info("Request Body: " + json);
            AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTHEAST_1)  // Specify the region here
                    .build();

            String queueUrl = sqs.getQueueUrl(queueUrlTo).getQueueUrl();

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(json)
                    .withMessageGroupId(UUID.randomUUID().toString())
                    .withMessageDeduplicationId(UUID.randomUUID().toString());
            SendMessageResult result = sqs.sendMessage(send_msg_request);
            Logger.info("after sending a message we get message id " + result.getMessageId());

            return true;
        } catch (Exception e) {
            Logger.error(e);
            throw e;
        }
    }


}
