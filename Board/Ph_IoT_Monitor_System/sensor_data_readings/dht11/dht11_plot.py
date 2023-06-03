import matplotlib.pyplot as plt
import csv

x = []
y1 = []
y2 = []

with open("dht11/dht11_values_2.csv", 'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')

    for row in plots:
        x.append(float(row[0]))
        y1.append(float(row[2]))
        y2.append(float(row[1]))

plt.plot(x, y1, color='g', linestyle='dashed', marker='o', label="Temperature")
plt.plot(x, y2, color='b', linestyle='dotted', marker='s', label="Humidity")
plt.xticks(rotation=25)
plt.xlabel('Time (s)')
plt.ylabel('Temperature / Humidity')
plt.title('DHT11 Sensor readings', fontsize=20)
plt.grid()
plt.legend()
plt.show()
