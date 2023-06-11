# PS-Monitor-IoT-System

This repo holds all the work done for our final project's course (LEIC - ISEL).

The Internet of Things (IoT) is revolutionizing the way machines work and communicate
with people and other systems. Our main objective consists in the creation of a smart
and automated system, using IoT technology, to enhance the efficiency and accuracy of
an industrial filtration process. By incorporating sensors into these filters, we can detect
problems before they lead to costly downtime or safety hazards. The sensors will collect
data relative to the filtration system’s environment, such as the water temperature and pH.
All this data is to be sent to a central server, and through real-time data analysis, we will
identify if the filtration system is not working optimally or is closer to failure. In these
situations, an alert will be sent to a device controlled by the filtration system operator or
manager, enabling them to take prompt action to resolve the issue. Our project seeks to
improve efficiency, reduce costs and increase safety by automating the fault detection process
of filtration system inspection.

## Authors
- 47185: Miguel Agostinho da Silva Rocha, a47185@alunos.isel.pt
- 47128: Pedro Miguel Martins da Silva, a47128@alunos.isel.pt

## Adviser
- Rui Duarte, rui.duarte@isel.pt

## Organization
All project source files are included in the this private Git Hub repository, of which we can highlight:

- MCU Firmware, a CMake project available in "/MCU/IoT\_System\_MCU". There are also some python scripts to plot sensor data, collected during sensor experimentations, in "/MCU/IoT\_System\_MCU/sensor\_data\_readings/...";
    
- The Backend composed by the Application Server, available in "/Backend". The Broker, described in the Proposal and final report, is currently lunched in from the Server application, and thus, there are no other resources dedicated to him. There is also the "sensor\_data\_tester" python script in order to simulate sensor data being sent to the Backend server;
    
- The Frontend, which, for this project, is a Single Page Application, accessed from the root of the repository;

- Project documentation, including diagrams, MCU data-sheets, the ongoing report, etc;

- A Postman request collection, located in the Backend folder.

## Pre-Requisites
- item To run python scripts:
    - item Install python: v3.10

- To compile and load MCU firmware:
    - Install ESP IDF: v5.0.1. We recommend using the VS Code Expressive IDF plugin, which already brings the most used commands.

- Database lunch:
    - Install Postgres DB: <TODO: insert version>
    - Install InfluxDB: v2.7.1: https://portal.influxdata.com/downloads/

- Application Server lunch:
    - Install JDK 17
    
- Website lunch:
    - Install Node Package Manager

- Docker:
    

## Instructions to lunch
Currently, we are still dealing with the deployment. We plan on finishing the docker images to lunch the application locally, and then, deploy it on a cloud provider.
There are two ways of launching the system:
- Without Docker:
    - Go to each project and start the correspondent application:
        - Go to Spring server project and lunch the server;
        - Lunch the Postgres DB;
        - Lunch de InfluxDB;
        - Go to website project and lunch the website, using webpack;
        - Go to MCU project and load the firmware in the MCU;
- Using Docker:
    - Go to "/Backend/iot\_data\_server" folder and use dokcer-compose to lunch the 

Instructions:
- Without Docker:
    - Load MCU firmware in device:
        - Go to "/MCU/IoT\_System\_MCU" folder
        - Connect ESP32-S2 to the computer, with the appropriate USB cable.
        - \$ idf.py build
        - \$ idf.py -p PORT flash monitor (for example using COM5)
        - Last two steps can be automated by using the VS Code Expressive IDF plugin.
        - After the MCU initializes, it will, eventually, try to connect to the Wi-Fi...
        - Use the ESP Touch Android application to connect the ESP32-S2 to pass the Wi-Fi credentials, along with the Device ID.

    - Database lunch:
        - Make sure there is a Postgres DB running on port 5432;
        - Make sure there is a InfluxDB running on port 8086;
    - Application Server lunch:
        - Open Application Server in the terminal;
        - \$ java -jar iot_data_server-0.0.1-SNAPSHOT.jar
    
    - Website lunch:
        - Open root project in the terminal;
        - \$ npm start

- Using Docker:
    - Go to "/Backend/iot\_data\_server" folder
    - Windows:
        - \$ docker compose down -v && docker system prune -a --volumes && gradlew clean && gradlew build -x test && docker compose up -d
    - Linux:
        - \$ docker compose down -volumes ; docker system prune -a --volumes ; gradle clean ; gradle build -x test ; docker compose up -d


