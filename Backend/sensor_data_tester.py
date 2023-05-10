import paho.mqtt.client as mqtt
import random
import datetime
import time
# import the threading module
import threading

# see: https://pypi.org/project/paho-mqtt/

def get_sensor_record_mqtt_message(sensor_type):
    device_id = "device_manual_tests"
    value = str(random.uniform(1.0, 12.0))
    timestamp = str(round(time.time()))
    return "device_id: " + device_id + ", value: " + value + ", timestamp: " + timestamp + ", sensor_type: " + sensor_type + ""

def get_message_without_value():
    device_id = "device_manual_tests"
    timestamp = str(round(time.time()))
    return "device_id: " + device_id + ", timestamp: " + timestamp + ""

def get_message_with_sensors_error():
    device_id = "device_manual_tests"
    timestamp = str(round(time.time()))
    sensors = "ph, humidity, temperature, water_flow, water_level, flood"
    return "device_id: " + device_id + ", timestamp: " + timestamp + ", sensors: " + sensors + ""

def get_message_with_device_error():
    device_id = "device_manual_tests"
    timestamp = str(round(time.time()))
    error = "something went wrong"
    return "device_id: " + device_id + ", timestamp: " + timestamp + ", error: " + error + ""

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
            client.publish("sensor_record", get_sensor_record_mqtt_message("water_flow"))
            time.sleep(1)
            client.publish("sensor_record", get_sensor_record_mqtt_message("water_level"))


class thread2(threading.Thread):
    def __init__(self, client):
        threading.Thread.__init__(self)
        self.client = client
        
    # helper function to execute the threads
    def run(self):
        while True:
            client.publish("sensor_error", get_message_with_sensors_error())
            time.sleep(5)
            client.publish("device_error", get_message_with_device_error())
            time.sleep(5)
            client.publish("water_leak", get_message_without_value())
            time.sleep(5)

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("localhost", 1883, 60)

thread1 = thread1(client)
thread2 = thread2(client)
 
thread1.start()
thread2.start()


# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()