A java jms producer

Compiling the client:

   >mvn install

Running the example:

   >mvn -Pproducer


Edit the profile in the pom.xml to configure properties e.g.

   <arguments>
       <argument>localhost</argument>
	   <argument>61616</argument>
	   <!-- Number of producer threads -->
	   <argument>10</argument>
	   <!-- Number of messages per thread -->
	   <argument>100</argument>
   </arguments>

To change the destination edit Producer.java

   Destination destination = session.createQueue("TESTQUEUE");
