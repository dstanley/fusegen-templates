Example configurations for securing JBoss A-MQ

1) The example includes a helper script that can be used to generate sample client and server certificates.

>chmod +x ./util/keys.sh
>./util/keys.sh

2) The included ./etc/activemq.xml configures the jaasCertificateAuthenticationPlugin for certificate based
authentication to the the openwire transport. 

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

3) The included ./etc/users.properties defines a certificate based user.

client=CN=client, O=fuse, C=us

4) The etc/key_auth.xml defines multiple jaas realms. Separate jaas realms are defined for ssh console authentication and jmx authentication

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

5) Note in ./etc/org.apache.karaf.shell.cfg we set the jaas realm for the console to be ssh

#
# sshRealm defines which JAAS domain to use for password authentication.
#
sshRealm=ssh






