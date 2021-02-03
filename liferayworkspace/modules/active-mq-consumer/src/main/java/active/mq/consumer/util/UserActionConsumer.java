package active.mq.consumer.util;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import static active.mq.initializer.constants.ActiveMQConstants.AUDIT_QUEUE_NAME;
import static active.mq.initializer.constants.ActiveMQConstants.HOSTNAME;
import static active.mq.initializer.constants.ActiveMQConstants.PORT;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class UserActionConsumer {

    private static final String URL = "tcp://"+HOSTNAME+":"+PORT;

    private static final String USER = ActiveMQConnection.DEFAULT_USER;

    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

    private static final String DESTINATION_QUEUE = AUDIT_QUEUE_NAME;

    private static final boolean TRANSACTED_SESSION = false;

    private static final int TIMEOUT = 1000;

    private static Map<String, Integer> consumedMessageTypes;

    private static int totalConsumedMessages = 0;


    public static void processMessages() throws JMSException {

    	consumedMessageTypes = new HashMap<String, Integer>();
    	totalConsumedMessages = 0;
    	
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
        final Connection connection = connectionFactory.createConnection();

        connection.start();

        final Session session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(DESTINATION_QUEUE);
        final MessageConsumer consumer = session.createConsumer(destination);

        processAllMessagesInQueue(consumer);

        consumer.close();
        session.close();
        connection.close();

        showProcessedResults();
    }

    private static void processAllMessagesInQueue(MessageConsumer consumer) throws JMSException {
        Message message;
        while ((message = consumer.receive(TIMEOUT)) != null) {
            proccessMessage(message);
        }
    }

    private static void proccessMessage(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            final String text = textMessage.getText();
            incrementMessageType(text);
            totalConsumedMessages++;
        }
    }

    private static void incrementMessageType(String message) {
        if (consumedMessageTypes.get(message) == null) {
            consumedMessageTypes.put(message, 1);
        } else {
            final int numberOfTypeMessages = consumedMessageTypes.get(message);
            consumedMessageTypes.put(message, numberOfTypeMessages + 1);
        }
    }

    private static void showProcessedResults() {
        System.out.println("Procesados un total de " + totalConsumedMessages + " mensajes");
        for (String messageType : consumedMessageTypes.keySet()) {
            final int numberOfTypeMessages = consumedMessageTypes.get(messageType);
            System.out.println("Tipo " + messageType + " Procesados " + numberOfTypeMessages + " (" +
                    (numberOfTypeMessages * 100 / totalConsumedMessages) + "%)");
        }
    }

}