import matplotlib.pyplot as plt
import csv

x = []
y1 = []
y2 = []

with open("adc/adc_values_2.csv", 'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')

    for row in plots:
        x.append(float(row[2]))
        y1.append(float(row[0]))
        y2.append(float(row[1]))

plt.plot(x, y1, color='g', linestyle='dashed', marker='o', label="ADC")
plt.plot(x, y2, color='b', linestyle='dotted', marker='s', label="Voltage")
plt.xticks(rotation=25)
plt.xlabel('Second')
plt.ylabel('ADC / Voltage')
plt.title('ADC readings', fontsize=20)
plt.grid()
plt.legend()
plt.show()
