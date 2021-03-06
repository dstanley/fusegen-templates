Example configurations for securing JBoss A-MQ

This quickstart shows how to secure JBoss A-MQ using public key authentication, certificate based authentication
and using the standard PropertiesLoginModule.


Notes:
------

1) The example includes a helper script that can be used to generate sample client and server certificates.

>chmod +x ./util/keys.sh
>./util/keys.sh

2) In ./etc/activemq.xml, jaasCertificateAuthenticationPlugin is used to configure certificate based authentication to the the openwire transport. 

<plugins>
	    <jaasCertificateAuthenticationPlugin configuration="karaf" />
        
		<!-- IMPORTANT: DONT specify a groupClass ala AMQ-3883, as we are authenticating based on the certificates DN -->
        <authorizationPlugin>
            <map>
               <authorizationMap>
                  <authorizationEntries>
					 <authorizationEntry queue="TEST.>" read="admin,ro" write="admin,rw" admin="admin" />
                     <authorizationEntry topic="ActiveMQ.Advisory.>" read="admin" write="admin" admin="admin" />
					 <authorizationEntry topic=">" read="admin" write="admin" admin="admin" /> 
					
                 </authorizationEntries>
              </authorizationMap>
           </map>
        </authorizationPlugin>

 </plugins>

<sslContext>
        <sslContext keyStore="${karaf.base}/etc/broker.ks"
          keyStorePassword="brokerpass" keyStoreKeyPassword="brokerpass" trustStore="${karaf.base}/etc/broker.ts"
          trustStorePassword="brokertrustpass"/>
</sslContext>

  <transportConnectors>
        <transportConnector name="openwire" uri="ssl://0.0.0.0:61616?keepAlive=true&amp;transport.needClientAuth=true"/>
        
 </transportConnectors>

3) In ./etc/users.properties we define a certificate based user that matches the DN of the client cert generated by keys.sh.

client=CN=client, O=fuse, C=us

4) The etc/key_auth.xml defines multiple jaas realms. Separate jaas realms are configured for ssh console authentication and jmx authentication

<!-- Use pulic key auth for ssh -->
<jaas:config name="ssh" rank="2">
    <jaas:module className="org.apache.karaf.jaas.modules.publickey.PublickeyLoginModule"
                 flags="required">
        users = $[karaf.base]/etc/keys.properties
    </jaas:module>
</jaas:config>

<!-- Use certificate authentication for ssl transport -->
<jaas:config name="karaf" rank="2">
    <jaas:module className="org.apache.activemq.jaas.TextFileCertificateLoginModule"
                 flags="required">
        org.apache.activemq.jaas.textfiledn.user = etc/users.properties
		org.apache.activemq.jaas.textfiledn.group = etc/groups.properties

    </jaas:module>
</jaas:config>

<!-- Setup a separate jaas realm for jmx. We will use username/pwd with mutual auth over ssl -->
<jaas:config name="jmx" rank="2">
    <jaas:module className="org.apache.karaf.jaas.modules.properties.PropertiesLoginModule" flags="required">
         users = $[karaf.base]/etc/jmxusers.properties
    </jaas:module>
</jaas:config>

5) In ./etc/org.apache.karaf.shell.cfg we set the jaas realm for the console to be ssh

#
# sshRealm defines which JAAS domain to use for password authentication.
#
sshRealm=ssh


Testing it out
==============

1) Start with a clean JBoss A-MQ install.
2) Generate some sample keys by running ./util/keys.sh
3) Copy the generated broker.ks and broker.ts into ./etc

Certificate based openwire authentication:
------------------------------------------

a) Copy the contents of ./etc to your <broker>/etc directory

b) Verify with an SSL client and the generated client certificates you can authenticate with the brokers openwire transport.

   * Clients need to configure their keystore and truststore e.g.
    
     -Djavax.net.ssl.keyStore=client.ks -Djavax.net.ssl.keyStorePassword=clientpass -Djavax.net.ssl.trustStore=client.ts

   * If need a java ssl client, run 'fusegen new amq-consumer-spring' to generate an ssl consumer the client certs can be plugged into.



Public key authentication to the karaf shell:
---------------------------------------------

a) Add your public key to <broker>/etc/keys.properties (e.g. ~/.ssh/id_rsa.pub). The format should be username=<public key>,role

b) Verify its possible to ssh directly to the karaf shell using public key auth
   
   ssh -p 8101 karaf@localhost

   Where the username karaf is defined in keys.properties

c) Verify other authentication attempts are rejected as expected.
   


PropertiesLoginModule Authentication via jmx
--------------------------------------------

In this example the jmx realm just uses the standard PropertiesLoginModule. 

a) Verify you can connect via jmx using a user defined in ./etc/jmxusers.properties

b) Verify invalid users are rejected as expected.



