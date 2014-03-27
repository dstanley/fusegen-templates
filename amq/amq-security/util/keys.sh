#!/bin/bash

# The below scripts show the commands used to generate the self-signed keys for this sample.
# If you use the below script to create your own keys be sure to change the passwords used here
# DO NOT USE THE SUPPLIED KEYS IN PRODUCTION--everyone has them!!
# For production recommended to use keys signed by a third-party certificate authority (CA)

rm *.ks
rm *.ts
rm *.cer

keytool -genkey -validity 730 -alias brokerkey -keyalg RSA -keystore broker.ks -dname "cn=broker,o=fuse,c=us" -storepass brokerpass -keypass brokerpass
keytool -export -alias brokerkey -keystore broker.ks -file broker.cer -storepass brokerpass

keytool -genkey -validity 730 -alias clientkey -keyalg RSA -keystore client.ks -dname "cn=client,o=fuse,c=us" -storepass clientpass -keypass clientpass
keytool -import -noprompt -trustcacerts -alias brokerkey -keystore client.ts -file broker.cer -storepass clienttrustpass

# Place client public cert in broker truststore
# Note this needs to be done only if client/mutual authentication is required

keytool -export -alias clientkey -keystore client.ks -file client.cer -storepass clientpass
keytool -import -noprompt -trustcacerts -alias client -keystore broker.ts -file client.cer -storepass brokertrustpass

# Verify cert contents
#keytool -list -v -keystore client.ks -storepass clientpass
#keytool -list -v -keystore broker.ks -storepass brokerpass
#keytool -list -v -keystore broker.ts -storepass brokertrustpass
#keytool -list -v -keystore client.ts -storepass clienttrustpass

