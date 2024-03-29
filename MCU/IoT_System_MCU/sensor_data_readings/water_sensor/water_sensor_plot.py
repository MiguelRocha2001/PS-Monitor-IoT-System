import matplotlib.pyplot as plt
import csv

x = []
y1 = []
y2 = []

with open("water_sensor.csv", 'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')

    for row in plots:
        x.append(float(row[0]))
        y1.append(float(row[2]))
        y2.append(float(row[1]))

plt.plot(x, y1, color='g', linestyle='dashed', marker='o', label="ADC")
plt.plot(x, y2, color='b', linestyle='dotted', marker='s', label="Voltage")
plt.xticks(rotation=25)
plt.xlabel('Time (ms)', fontsize=23)
plt.ylabel('ADC / Voltage', fontsize=23)
plt.title('ADC readings', fontsize=30)
plt.grid()
plt.legend(prop={'size': 20})
plt.show()
