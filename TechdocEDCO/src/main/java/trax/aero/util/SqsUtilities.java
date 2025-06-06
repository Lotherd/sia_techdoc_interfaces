package trax.aero.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import org.jboss.logging.Logger;
import trax.aero.pojo.json.OUTPUT;

import java.util.UUID;

public class SqsUtilities {

    private static final Logger logger = Logger.getLogger(SftpUtilities.class);

    public static void sendResend(OUTPUT resend) throws Exception {

        Gson gson = new Gson();
        String json;
        String queueUrlTo = System.getProperty("Techdoc_ToSQS");

        logger.info("Sending JSON To EDCO");

        try {
            json = gson.toJson(resend);

            logger.info("Request Body: " + json);
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
            logger.info("after sending a message we get message id " + result.getMessageId());

        } catch (Exception e) {
            logger.error("ERROR", e);
            throw e;
        }
    }


}
