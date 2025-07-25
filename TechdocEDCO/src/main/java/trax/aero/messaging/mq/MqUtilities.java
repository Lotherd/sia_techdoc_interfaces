/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.messaging.mq;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import javax.jms.*;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.tinylog.Logger;
import trax.aero.notification.EmailNotificationManager;

public class MqUtilities {

    private static final String HOST = System.getProperty("Techdoc_host"); // Host name or IP address
    private static final int PORT =
            Integer.parseInt(System.getProperty("Techdoc_port")); // Listener port for your queue manager
    private static final String CHANNEL = System.getProperty("Techdoc_channel"); // Channel name
    private static final String QMGR = System.getProperty("Techdoc_qmgr"); // Queue manager name
    private static final String APP_USER =
            System.getProperty("Techdoc_user"); // Username that application uses to connect to MQ
    private static final String APP_PASSWORD =
            System.getProperty("Techdoc_password"); // Password that the application uses to connect to MQ
    private static final String QUEUE_NAME_SENDER =
            System.getProperty(
                    "Techdoc_send"); // Queue that the application uses to put and get messages to and from
    private static final String QUEUE_NAME_RECEIVE =
            System.getProperty(
                    "Techdoc_receive"); // Queue that the application uses to put and get messages to and from

    private static final String JKS_LOCATION =
            System.getProperty(
                    "JKS_LOCATION"); // Queue that the application uses to put and get messages to and from
    private static final String JKS_PWD =
            System.getProperty(
                    "JKS_PWD"); // Queue that the application uses to put and get messages to and from
    private static final String CIPHER =
            System.getProperty(
                    "CIPHER"); // Queue that the application uses to put and get messages to and from

    public static MQQueueConnectionFactory createMQQueueConnectionFactory() throws Exception {
        /*MQ Configuration*/
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();

        SSLSocketFactory sslSocketFactory = createSSLSocketFactory();

        mqQueueConnectionFactory.setHostName(HOST);
        mqQueueConnectionFactory.setChannel(CHANNEL); // communications link
        mqQueueConnectionFactory.setPort(PORT);
        mqQueueConnectionFactory.setQueueManager(QMGR); // service provider

        mqQueueConnectionFactory.setSSLFipsRequired(false);
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
        mqQueueConnectionFactory.setSSLCipherSuite(CIPHER);
        mqQueueConnectionFactory.setSSLSocketFactory(sslSocketFactory);

        mqQueueConnectionFactory.setStringProperty(WMQConstants.USERID, APP_USER);
        mqQueueConnectionFactory.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
        return mqQueueConnectionFactory;
    }

    public static SSLSocketFactory createSSLSocketFactory() throws Exception {

        char[] KSPW = JKS_PWD.toCharArray();

        KeyStore ks = KeyStore.getInstance("JKS");

        ks.load(Files.newInputStream(Paths.get(JKS_LOCATION)), KSPW);
        // Logger.info("Number of keys on JKS: "  + Integer.toString(ks.size()));

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        // Initialise the managers
        keyManagerFactory.init(ks, KSPW);

        // Accessing available algorithm/protocol in the SunJSSE provider
        // see http://java.sun.com/javase/6/docs/technotes/guides/security/SunProviders.html
        SSLContext sslContext = SSLContext.getInstance("SSLv3");

        // Acessing available algorithm/protocol in the IBMJSSE2 provider
        // see
        // http://www.ibm.com/developerworks/java/jdk/security/142/secguides/jsse2docs/JSSE2RefGuide.html
        // SSLContext sslContext = SSLContext.getInstance("SSL_TLS");
        //   Logger.info("SSLContext provider: " +       sslContext.getProvider().toString());

        // Initialise our SSL context from the key/trust managers
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        // Get an SSLSocketFactory to pass to WMQ

        return sslContext.getSocketFactory();
    }

    public static Boolean sendMqText(String text) throws JMSException {

        QueueSender queueSender = null;
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;

        try {
            /*MQ Configuration*/
            MQQueueConnectionFactory mqQueueConnectionFactory =
                    MqUtilities.createMQQueueConnectionFactory();
            /*Create Connection */
            queueConnection = mqQueueConnectionFactory.createQueueConnection();
            queueConnection.start();

            /*Create session */
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            /*Create text message */
            TextMessage textMessage = queueSession.createTextMessage(text);
            // textMessage.setJMSReplyTo(queue);
            textMessage.setJMSType("mcd://xmlns"); // message type
            textMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

            /*Create sender queue */
            queueSender = queueSession.createSender(queueSession.createQueue(QUEUE_NAME_SENDER));
            queueSender.send(textMessage);

            return true;
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            if (queueSender != null) queueSender.close();
            if (queueSession != null) queueSession.close();
            if (queueConnection != null) queueConnection.close();
        }
        return false;
    }

    public static String receiveMqText(boolean sendEmail) throws JMSException {
        QueueReceiver queueReceiver = null;
        QueueConnection queueConnection = null;
        QueueSession queueSession = null;

        try {
            /*MQ Configuration*/
            MQQueueConnectionFactory mqQueueConnectionFactory =
                    MqUtilities.createMQQueueConnectionFactory();
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
            Message message = queueReceiver.receive(60 * 1000);
            if (message != null) {
                // Logger.info("JMSCorrelationID: "+ message.getJMSCorrelationID() + " JMSMessageID: " +
                // message.getJMSMessageID()  );

                return ((TextMessage) message).getText();
            }

        } catch (Exception e) {
            if (sendEmail) {
                EmailNotificationManager.sendEmailMQ(ExceptionUtils.getStackTrace(e));
            }
            Logger.error(e);
        } finally {
            try {
                if (queueReceiver != null) queueReceiver.close();
                if (queueSession != null) queueSession.close();
                if (queueConnection != null) queueConnection.close();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        return null;
    }
}
