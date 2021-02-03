package active.mq.producer.util;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import static active.mq.initializer.constants.ActiveMQConstants.AUDIT_QUEUE_NAME;
import static active.mq.initializer.constants.ActiveMQConstants.HOSTNAME;
import static active.mq.initializer.constants.ActiveMQConstants.PORT;

import java.util.Random;

public class MessageSender {

    public enum UserAction {

        PORTADA("ACCESO A HOME"),
        LOGIN("ACCESO A PORTAL"),
        HISTORIAL("ACCESO HISTORIAL CLINICO");

        private final String userAction;

        private UserAction(String userAction) {
            this.userAction = userAction;
        }

        public String getActionAsString() {
            return this.userAction;
        }
    }

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final String URL = "tcp://"+HOSTNAME+":"+PORT;

    private static final String USER = ActiveMQConnection.DEFAULT_USER;

    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

    private static final String DESTINATION_QUEUE = AUDIT_QUEUE_NAME;

    private static final boolean TRANSACTED_SESSION = true;
    
    private static final int MESSAGES_TO_SEND = 20;

    public static void sendMessages() throws JMSException {

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        final Session session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(DESTINATION_QUEUE);

        final MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        sendMessages(session, producer);
        session.commit();

        session.close();
        connection.close();

        System.out.println("Mensajes enviados a la cola");
    }

    private static void sendMessages(Session session, MessageProducer producer) throws JMSException {
        final MessageSender messageSender = new MessageSender();
        for (int i = 1; i <= MESSAGES_TO_SEND; i++) {
            final UserAction userActionToSend = getRandomUserAction();
            messageSender.sendMessage(userActionToSend.getActionAsString(), session, producer);
        }
    }

    private void sendMessage(String message, Session session, MessageProducer producer) throws JMSException {
        final TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
    }

    private static UserAction getRandomUserAction() {
        final int userActionNumber = (int) (RANDOM.nextFloat() * UserAction.values().length);
        return UserAction.values()[userActionNumber];
    }

}