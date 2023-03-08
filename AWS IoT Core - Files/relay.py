import time as t
import json
import AWSIoTPythonSDK.MQTTLib as AWSIoTPyMQTT
import random

# Define ENDPOINT, CLIENT_ID, PATH_TO_CERT, PATH_TO_KEY, PATH_TO_ROOT, MESSAGE, TOPIC, and RANGE
ENDPOINT = "a2h16yf1bsojyl-ats.iot.eu-west-2.amazonaws.com"
CLIENT_ID = "iotconsole-26f5c897-3c1d-4cb6-baef-90c6042a4c29"
PATH_TO_CERT = "certificates/device_certificate.pem.crt"
PATH_TO_KEY = "certificates/PrivateKey.pem.key"
PATH_TO_ROOT = "certificates/AmazonRootCA1.pem"
MESSAGE = "Temperature: "
TOPIC = "test/testing"
RANGE = 20

myAWSIoTMQTTClient = AWSIoTPyMQTT.AWSIoTMQTTClient(CLIENT_ID)
myAWSIoTMQTTClient.configureEndpoint(ENDPOINT, 8883)
myAWSIoTMQTTClient.configureCredentials(PATH_TO_ROOT, PATH_TO_KEY, PATH_TO_CERT)
myAWSIoTMQTTClient.connect()

print('Begin Publish')

for i in range (RANGE):
    random_value = random.random() * 100
    data = "{} [{}]".format(MESSAGE, random_value)
    message = {"message" : data}
    myAWSIoTMQTTClient.publish(TOPIC, json.dumps(message), 1)
    print("Published: '" + json.dumps(message) + "' to the topic: " + "'test/testing'")
    t.sleep(1)

print('Publish End')

myAWSIoTMQTTClient.disconnect()