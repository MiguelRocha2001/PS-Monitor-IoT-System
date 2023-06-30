import matplotlib.pyplot as plt
import csv

x = []
y = []

with open("current.csv", 'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=';')

    for row in plots:
        if row[0].isnumeric():
            x.append(float(row[0]))
            y.append(float(row[1].replace(",", ".")))

# Sort the data based on x-values
data = sorted(zip(x, y), key=lambda point: point[0])
x, y = zip(*data)

print("Sum:", sum(y))

# Subtract 80 units from the y-values
#x = [i - 80 for i in x]

plt.plot(x, y, color='g', linestyle='dashed', marker='o', label="Current")
plt.xticks(rotation=25)
plt.xlabel('Readings')
plt.ylabel('Current')
plt.title('ESP32-S2 Consumption', fontsize=20)
plt.grid()
plt.legend()
plt.show()
