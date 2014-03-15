A camel-smooks blueprint example that can be deployed into JBoss Fuse.


Compiling the demo:

   >mvn install

Running the example inside JBoss Fuse 6.x 


1) Copy the files in <camel-smooks>/etc to <JBoss Fuse>/etc
	
	
2) Install dependent bundles

   >osgi:install -s mvn:org.freemarker/freemarker/2.3.15
   >osgi:install -s 'wrap:mvn:jaxen/jaxen/1.1.1$Bundle-SymbolicName=Jaxen&Bundle-Version=1.1.1&Export-Package=org.jaxen*;version=1.1.1'   
   >osgi:install -s mvn:org.ow2.bundles/ow2-bundles-externals-opencsv/1.0.23
   >osgi:install -s mvn:org.milyn/milyn-smooks-all/1.5.2

3) Install the demo

   >osgi:install -s mvn:com.redhat.support/test-project/1.0-SNAPSHOT


Notes:

The demo defines a configuration pid of 'camel.smooks'. This allows the location of the smooks-config.xml to be configurable via the properties file './etc/camel-smooks.cfg'


Expected output:

Once the bundle is started, a log:display should show the camel timer kicking in periodically. This triggers the smooks component:

2014-03-15 15:00:58,123 | INFO  | #0 - timer://foo | timerToLog                       | ?                                   ? | 130 - org.apache.camel.camel-core - 2.10.0.redhat-60024 | The message contains <people><person number="1"><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></person><person number="2"><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></person></people>
2014-03-15 15:01:02,921 | INFO  | #0 - timer://foo | timerToLog                       | ?                                   ? | 130 - org.apache.camel.camel-core - 2.10.0.redhat-60024 | The message contains <people><person number="1"><firstname>Tom</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>4</age><country>Ireland</country></person><person number="2"><firstname>Mike</firstname><lastname>Fennelly</lastname><gender>Male</gender><age>2</age><country>Ireland</country></person></people>



To deploy the demo to a Fuse Fabric Maven Proxy:

   >mvn deploy
