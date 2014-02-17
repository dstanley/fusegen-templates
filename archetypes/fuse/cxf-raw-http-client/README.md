A simple client that reads in a raw soap xml message and fires it at a soap endpoint. 

Compiling the client:
   >javac client

Running the client:

   >java client -msg [soap message] -endpoint [endpoint]

   e.g. java client -msg request.xml -endpoint http://localhost:9000


Uses:

- Helpful where dynamic invocation clients such as SOAPScope and SOATest cannot parse the wsdl (for example the MTOSI wsdl).

- Useful for testing badly/unusually formatted messages

- Quick once you capture the message.

