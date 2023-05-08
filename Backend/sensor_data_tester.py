import paho.mqtt.client as mqtt
import random
import datetime
import time

# see: https://pypi.org/project/paho-mqtt/

def get_message_with_value():
    device_id = "\"device_manual_tests\""
    value = "\"" + str(random.uniform(1.0, 12.0)) + "\""
    timestamp = "\"" + str(round(time.time())) + "\""
    return "{\"device_id\": " + device_id + ", \"value\": " + value + ", \"timestamp\": " + timestamp + "}"

def get_message_without_value():
    device_id = "\"device_manual_tests\""
    timestamp = "\"" + str(round(time.time())) + "\""
    return "{\"device_id\": " + device_id + ", \"timestamp\": " + timestamp + "}"

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("$SYS/#")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("localhost", 1883, 60)

while True:
    client.publish("ph", get_message_with_value())
    time.sleep(1)
    client.publish("temperature", get_message_with_value())
    time.sleep(1)
    client.publish("flood", get_message_without_value())

# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()