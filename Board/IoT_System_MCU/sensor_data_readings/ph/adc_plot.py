import matplotlib.pyplot as plt
import csv

x = []
y1 = []
y2 = []

title = "ph4_power_up"

with open(title+".csv", 'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')

    for row in plots:
        x.append(float(row[0]))
        y1.append(float(row[2]))
        y2.append(float(row[1]))

plt.plot(x, y1, color='g', linestyle='dashed', marker='o', label="ADC")
plt.plot(x, y2, color='b', linestyle='dotted', marker='s', label="Voltage")
plt.xticks(rotation=25)
plt.xlabel('Time (s)')
plt.ylabel('ADC / Voltage')
plt.title(title, fontsize=20)
plt.grid()
plt.legend()
plt.show()
