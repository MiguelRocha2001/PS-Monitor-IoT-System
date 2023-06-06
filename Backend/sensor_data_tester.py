import paho.mqtt.client as mqtt
import random
import datetime
import time
# import the threading module
import threading

# see: https://pypi.org/project/paho-mqtt/

device_id = "device_manual_tests"

def sorround_with_quotes(value):
    return "\"" + value + "\""

def get_sensor_record_mqtt_message(sensor_type):
    value = str(random.uniform(1.0, 12.0))
    timestamp = str(round(time.time()))
    return "\"device_id\": " + sorround_with_quotes(device_id) + ", \"value\": " + sorround_with_quotes(value) + ", \"timestamp\": " + sorround_with_quotes(timestamp) + ", \"sensor_type\": " + sorround_with_quotes(sensor_type) + ""

def get_message_without_value():
    timestamp = str(round(time.time()))
    return "\"device_id\": " + sorround_with_quotes(device_id) + ", \"timestamp\": " + sorround_with_quotes(timestamp) + ""

def get_message_with_sensors_error(sensor):
    timestamp = str(round(time.time()))
    return "\"device_id\": " + sorround_with_quotes(device_id) + ", \"timestamp\": " + sorround_with_quotes(timestamp) + ", \"sensor_type\": " + sorround_with_quotes(sensor) + ""

def get_message_with_device_log(reason):
    timestamp = str(round(time.time()))
    reason = "something went wrong"
    return "\"device_id\": " + sorround_with_quotes(device_id) + ", \"timestamp\": " + sorround_with_quotes(timestamp) + ", \"reason\": " + sorround_with_quotes(reason) + ""

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("$SYS/#")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))

 
class thread1(threading.Thread):
    def __init__(self, client):
        threading.Thread.__init__(self)
        self.client = client
        
    # helper function to execute the threads
    def run(self):
        while True:
            client.publish("sensor_record", get_sensor_record_mqtt_message("ph"))
            time.sleep(1)
            client.publish("sensor_record", get_sensor_record_mqtt_message("humidity"))
            time.sleep(1)
            client.publish("sensor_record", get_sensor_record_mqtt_message("temperature"))
            time.sleep(1)
            client.publish("sensor_record", get_sensor_record_mqtt_message("water-flow"))
            time.sleep(1)
            client.publish("sensor_record", get_sensor_record_mqtt_message("water-level"))


class thread2(threading.Thread):
    def __init__(self, client):
        threading.Thread.__init__(self)
        self.client = client
        
    # helper function to execute the threads
    def run(self):
        while True:
            client.publish("device_wake_up_log", get_message_with_device_log("water-leak"))
            time.sleep(5)
            client.publish("device_wake_up_log", get_message_with_device_log("unknown"))
            time.sleep(5)
            client.publish("device_wake_up_log", get_message_with_device_log("power-on"))
            time.sleep(5)
            client.publish("device_wake_up_log", get_message_with_device_log("software"))
            time.sleep(5)
            client.publish("device_wake_up_log", get_message_with_device_log("exception-panic"))
            time.sleep(5)
            client.publish("device_wake_up_log", get_message_with_device_log("brownout"))
            time.sleep(5)

class thread3(threading.Thread):
    def __init__(self, client):
        threading.Thread.__init__(self)
        self.client = client
        
    # helper function to execute the threads
    def run(self):
        while True:
            client.publish("error_reading_sensor", get_message_with_sensors_error("initial-ph"))
            time.sleep(5)
            client.publish("error_reading_sensor", get_message_with_sensors_error("final-ph"))
            time.sleep(5)
            client.publish("error_reading_sensor", get_message_with_sensors_error("humidity"))
            time.sleep(5)
            client.publish("error_reading_sensor", get_message_with_sensors_error("temperature"))
            time.sleep(5)
            client.publish("error_reading_sensor", get_message_with_sensors_error("water-flow"))
            

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("localhost", 1883, 60)

thread1 = thread1(client)
thread2 = thread2(client)
thread3 = thread3(client)
 
thread1.start()
thread2.start()
thread3.start()


# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()