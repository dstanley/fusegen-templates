package @package;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;


/**
 * JMS Consumer Client
 * 
 */
public class Consumer implements MessageListener {

	Log log = LogFactory.getLog(ConsumerLoad.class);
	
	private int NUM_MSGS_CONSUME = 1000;
	private int MAX_EVENTS_PER_MESSAGE=150;
	private static int messageCount=0;
	private String config="consumer.xml";
	
	private Queue<TextMessage> queue = new ConcurrentLinkedQueue<TextMessage>();

	public static void main(String[] args) {
		new ConsumerLoad().doIt();
	}

    public void doIt() {
		Thread.currentThread().setContextClassLoader(ConsumerLoad.class.getClassLoader());
	    ClassPathXmlApplicationContext context1 = new ClassPathXmlApplicationContext(config);
		log.info("Spring context initialized .. waiting for messages");
			
		// Kick off worker thread
		MessageWorker mw = new MessageWorker(1000);
		Thread worker = new Thread(mw);
		worker.start();
			
		while(messageCount < NUM_MSGS_CONSUME) {
			//try {
			//	Thread.sleep(2000);
			//} catch(Exception ignore) {}
			
			if((messageCount % 100) == 0) {
				log.info("Consumed: " + messageCount);
			}
		}

		log.info("Completed: " + messageCount);
		
		context1.stop();	
		mw.finished() ;		
		System.exit(0);
	}

	
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			queue.add((TextMessage)message);
			messageCount=messageCount+1;
			
		}
		else { 
			Thread.currentThread().dumpStack();
			throw new IllegalArgumentException("Message must be of type TextMessage"); 
		}
	}
	
	class MessageWorker implements Runnable {
		private int timeout = 1000;
		private boolean stop=false;
		Log logger;
		
	
		public MessageWorker(int timeout) {
			logger = LogFactory.getLog(MessageWorker.class);
			this.timeout=timeout;	
		}
		
		public void finished() {
			this.stop=true;
		
		}
			
		public void run() {
			while(!stop) {
				StringBuilder sb = new StringBuilder("<msg>");				
				for (int i = 1; i < MAX_EVENTS_PER_MESSAGE; i++) {
					TextMessage msg = queue.poll();
					
					if (msg != null) { 
						sb.append(msg); 
						messageCount=messageCount+1; 
					} else { break; }
				}
				sb.append("</msg>");
				
				if(sb.length() > 11) {
					notify(sb.toString());
				}
				
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void notify(String msg) {
			log.info("Notify: " + msg);
		}
	}
}

