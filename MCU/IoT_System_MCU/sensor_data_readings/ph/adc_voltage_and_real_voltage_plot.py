import matplotlib.pyplot as plt
import csv

x = []
# y1 = []
y2 = []
y3 = []

title = "MCU Mesured Voltage vs Real Voltage"

with open("ph8_8_with_real_voltage.csv", 'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')

    for row in plots:
        x.append(float(row[0]))
        # y1.append(float(row[1]))
        y2.append(float(row[2]))
        if len(row) == 4:
            y3.append(float(row[3]))
        else:
            y3.append(y3[y3.__len__()-1])

# plt.plot(x, y1, color='g', linestyle='dashed', marker='o', label="ADC")
plt.plot(x, y2, color='b', linestyle='dotted', marker='o', label="Voltage")
plt.plot(x, y3, color='r', linestyle='solid', label="Real Voltage")
plt.xticks(rotation=25)
plt.xlabel('Time (s)')
plt.ylabel('ADC / Voltage')
plt.title(title, fontsize=20)
plt.grid()
plt.legend()
plt.show()
