package @package;

import java.io.File;
import java.util.Scanner;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Topic;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * JMS Producer
 * 
 */
public class Producer {
	private int PRODUCER_COUNT=5;
	private int START_DESTINATION_COUNT=0;
	private int END_DESTINATION_COUNT=100;
	private int NUM_MSGS_SEND = 500;
	
	private ConnectionFactory factory;
	private String uri;
	

	public static void main(String[] args) {
	    
	    if (args.length < 4) {
			System.out.println("Args length" + args.length);			
	    	System.out.println("mvn exec:java -Dexec.mainClass=@package.Producer -Dexec.args=\"localhost 61616 5 0 800\"");
			System.out.println("where:    arg[0]=hostname (e.g. localhost)");
			System.out.println("          arg[1]=port (e.g. 61616)");
			System.out.println("          arg[2]=numProducerThreads (e.g. 5)");
			System.out.println("          arg[3]=numMsgs to send ");
			
			System.out.println("** USE 'producer' PROFILE in the top level pom.xml to configure properties **");
			System.exit(1);
		}
		
		new Producer(args).sendMessages();	
	}

	/**
	 * Sets up the JMS ConnectionFactory for this instance
	 */
	public Producer(String[] args) {
		
		this.uri="tcp://"+args[0]+":"+args[1]+"?wireFormat.maxInactivityDuration=0";
		this.PRODUCER_COUNT=Integer.parseInt(args[2]);
		this.NUM_MSGS_SEND=Integer.parseInt(args[3]);
	}

	/**
	 * 
	 */
	public void sendMessages() {
		
		Thread[] producers = new Thread[PRODUCER_COUNT];
		try {
			System.out.println("Starting producers ..");
			System.out.println("BROKER_URI: " + uri);
			System.out.println("PRODUCER_COUNT (threads): " + PRODUCER_COUNT); 
			System.out.println("NUM_MSGS_SEND  : " + NUM_MSGS_SEND); 
		
			for(int index=0; index<PRODUCER_COUNT; index++) {
				Producer producerWorker = new Producer(uri, index);
				producers[index] = new Thread(producerWorker);
				producers[index].start();
			}
		
			for(int index=0; index<PRODUCER_COUNT; index++) {
				producers[index].join();
			}

		} catch(Throwable t) {
			System.out.println("Exception starting producer thread!");
			t.printStackTrace();
			System.exit(1);
			
		}		
	}
	
	
/**
 * Message producer running as a separate thread
 *
 */
class Producer implements Runnable{
	public Object init = new Object();
	
	private Log logger = null;
	private String url;
	private int producerId;
	private int numMsgs;
	private String text = "<activemq>Test message X now=Y</activemq>";
	
	private ArrayList<MessageProducer> producers = new ArrayList<MessageProducer>();
	
	public Producer(String url, int producerId) {
		
		logger = LogFactory.getLog(Producer.class);
		this.url=url;
		this.producerId=producerId;
	}	
	
	/**
	 * connect to broker and constantly send messages
	 */
	public void run() {
		
		
		Connection connection = null;
		Session session = null;
		MessageProducer producer = null;
		
		
		try {
			
			ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory(url);
			connection = amq.createConnection();
			
	        connection.setExceptionListener(new javax.jms.ExceptionListener() { 
	        	public void onException(javax.jms.JMSException e) {
	        		e.printStackTrace();
	        	} 
	        });
	
	        connection.start();
	        
	        // Create a Session
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	       
	        logger.warn("Thread[" + producerId + "] started");
	        
	        // Create a MessageProducer from the Session to the Topic or Queue
			Destination destination = session.createQueue("TESTQUEUE");
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producers.add(producer);

	        long sendStartTime = System.currentTimeMillis();
	        int sent=0;
	        
	        // Create and send message
	        for (int index=0; index<NUM_MSGS_SEND; index++) {
	       	    
	       	    String t1 = text.replace("X", String.valueOf(index));
	       	    String t2 = t1.replace("Y", String.valueOf(System.currentTimeMillis()));
	            TextMessage message = session.createTextMessage(t2);
				producer.send(message);
	        	sent++;
				
	            if ((index % 500) == 0)
	            	logger.warn("Thread[" + producerId + "] sent " + sent + " messages");
	            
	            //try {Thread.sleep(500); } catch (Exception ex) {}
	        }    
	        
	        long sendEndTime = System.currentTimeMillis();
	        
	        long totalTime = (sendEndTime - sendStartTime);
	        double msgPerSec = NUM_MSGS_SEND / (totalTime / (double)1000);
	        logger.warn("Thread[" + producerId + "] sent " + NUM_MSGS_SEND + " messages (" + msgPerSec + " msgs/s)");
	        
		} catch (Exception ex) {
			System.out.println("Thread[" + producerId + "]  exiting due to exception:" + ex.getMessage());
			logger.error(ex);
			return;
		}
		finally {
			
			try {
				for(MessageProducer p : producers) {
					p.close();
				}
		        if (session != null)
		        	session.close();
		        if (connection != null)
		        	connection.close();
			} catch (Exception e) {
				logger.error("Thread[" + producerId + "] Problem closing down JMS objects: " + e);
			}
		}
	}
}


}

