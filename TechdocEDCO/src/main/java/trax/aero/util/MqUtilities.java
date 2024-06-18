package trax.aero.util;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;

public class MqUtilities {

	private static final String HOST = System.getProperty("TechdocEDCO_host"); // Host name or IP address
    private static final int PORT = Integer.valueOf(System.getProperty("TechdocEDCO_port")).intValue(); // Listener port for your queue manager
    private static final String CHANNEL = System.getProperty("TechdocEDCO_channel"); // Channel name
    private static final String QMGR = System.getProperty("TechdocEDCO_qmgr"); // Queue manager name
    private static final String APP_USER = System.getProperty("TechdocEDCO_user"); // User name that application uses to connect to MQ
    private static final String APP_PASSWORD = System.getProperty("TechdocEDCO_password"); // Password that the application uses to connect to MQ
    private static final String QUEUE_NAME_SENDER = System.getProperty("TechdocEDCO_send"); // Queue that the application uses to put and get messages to and from
    private static final String QUEUE_NAME_RECEIVE = System.getProperty("TechdocEDCO_receive"); // Queue that the application uses to put and get messages to and from
    
    public static MQQueueConnectionFactory createMQQueueConnectionFactory() throws JMSException {
	    /*MQ Configuration*/
	    MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
	    mqQueueConnectionFactory.setHostName(HOST);
	    mqQueueConnectionFactory.setChannel(CHANNEL);//communications link
	    mqQueueConnectionFactory.setPort(PORT);
	    mqQueueConnectionFactory.setQueueManager(QMGR);//service provider 
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
       
        mqQueueConnectionFactory.setStringProperty(WMQConstants.USERID, APP_USER); 
        mqQueueConnectionFactory.setStringProperty(WMQConstants.PASSWORD,APP_PASSWORD );
	    return mqQueueConnectionFactory;
    }
	
    public static Boolean sendMqText(String text) throws JMSException {
    	
    	
    	QueueSender queueSender = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		
		try {
            /*MQ Configuration*/
			MQQueueConnectionFactory mqQueueConnectionFactory = MqUtilities.createMQQueueConnectionFactory();
            /*Create Connection */
            queueConnection = mqQueueConnectionFactory.createQueueConnection();
            queueConnection.start();

            /*Create session */
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            /*Create text message */
            TextMessage textMessage = queueSession.createTextMessage(text);
            //textMessage.setJMSReplyTo(queue);
            textMessage.setJMSType("mcd://xmlns");//message type
            textMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT); 

            /*Create sender queue */
            queueSender = queueSession.createSender(queueSession.createQueue(QUEUE_NAME_SENDER));
            queueSender.send(textMessage);

            /*After sending a message we get message id */
            System.out.println("after sending a message we get message id "+ textMessage.getJMSMessageID());
            return true;
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	 queueSender.close();
             queueSession.close();
             queueConnection.close();
        }
		return false;
    }
    
    public static String receiveMqText() throws JMSException {
		QueueReceiver queueReceiver = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		
		try {
            /*MQ Configuration*/
			MQQueueConnectionFactory mqQueueConnectionFactory = MqUtilities.createMQQueueConnectionFactory();
            /*Create Connection */
            queueConnection = mqQueueConnectionFactory.createQueueConnection();
            queueConnection.start();

            /*Create session */
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            /*Create response queue */
            Queue queue = queueSession.createQueue(QUEUE_NAME_RECEIVE);
            
            /*Within the session we have to create queue reciver */
             queueReceiver = queueSession.createReceiver(queue);


            /*Receive the message from*/
            Message message = queueReceiver.receive(60*1000);
            if(message != null ) {
            	String responseMsg = ((TextMessage) message).getText();
                System.out.println("responseMsg "+ responseMsg);
               
                return responseMsg;
            }
            
            

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
             queueReceiver.close();
             queueSession.close();
             queueConnection.close();
        }
		return null;
    }
    
}
