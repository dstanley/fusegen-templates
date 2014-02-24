
import java.io.FileInputStream;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;


public class client
{	
  	public static void main (String args[]) throws Exception {
	    
		boolean done = false;
	    String endpoint="http://localhost:9000";
	    String filename="request.xml";
	    
	    if (args.length == 0) {
            printUsage();
            return;
        }

	   
		try {
			
			while (!done && args.length > 0) {
				done = true;
				if ("-msg".equals(args[0])) {
				    filename = args[1];
				    String tmp[] = new String[args.length-2];
				    System.arraycopy(args,2,tmp,0,tmp.length);
				    args = tmp;
				    done = false;
				} else if (args[0].startsWith("-endpoint")) {
				    endpoint = args[1];
				    String tmp[] = new String[args.length-2];
				    System.arraycopy(args,2,tmp,0,tmp.length);
				    args = tmp;
				    done = false;
				}
			}

			//Create the connection
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnFactory.createConnection();
			
			//Create the actual message
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();
			
			//Create objects for the message parts            
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody body = envelope.getBody();
			
			//Populate the Message
			StreamSource preppedMsgSrc = new StreamSource(new FileInputStream(filename));
			soapPart.setContent(preppedMsgSrc);
			
			
			//Check the input
	        System.out.println("\nREQUEST:\n");
	        message.writeTo(System.out);
	        System.out.println();
			
			//Send the message
			SOAPMessage reply = connection.call(message, endpoint);
			System.out.println("\nRESPONSE:\n");
			
			//Create the transformer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			//Extract the content of the reply
			Source sourceContent = reply.getSOAPPart().getContent();

			//Set the output for the transformation
			StreamResult result = new StreamResult(System.out);
			transformer.transform(sourceContent, result);
			System.out.println();

			//Close the connection            
			connection.close();
            
        } catch(Exception e) {
            System.out.println("Exception: "  + e.getMessage());
        }


	}
	
    public static void printUsage() {
    	System.out.println("Send a SOAP message to a given endpoint");
    	System.out.println("Syntax is: ");
        System.out.println("");
        System.out.println(" client -msg <path to soap message> -endpoint <soap endpoint>");
        System.out.println(" -msg       File path to a complete xml soap message (e.g. /home/dstanley/request.xml)");
        System.out.println(" -endpoint  The soap endpoint (e.g. http://localhost:9000/hello)");
    }	
}