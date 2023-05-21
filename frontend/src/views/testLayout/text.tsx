import React, {useEffect, useState} from "react";
import {MyChart, TimeUnit} from "../chart/MyChart";
import {services} from "../../services/services";
import {SensorData} from "../../services/domain";
import "./text.css";
import {Navigate, useLocation, useNavigate} from "react-router-dom"

export function ChartWithPeriodSelection({deviceId, deviceEmail}: { deviceId: string, deviceEmail: string }) {
    const [availableSensors, setAvailableSensors] = useState<string[]>([]);
    const [sensorsData, setSensorsData] = React.useState<SensorData[]>([]);
    const [start, setStart] = React.useState<Date>(new Date());
    const [end, setEnd] = React.useState<Date>(new Date());
    const [timeUnit, setTimeUnit] = React.useState<TimeUnit|undefined>(undefined);
    const [errorMessage, setErrorMessage] = useState<string>();

    useEffect(() => {
        async function fetchAvailableSensors() {
            const availableSensors = await services.availableSensors(deviceId);
            setAvailableSensors(availableSensors);
        }
        async function fetchSensorData(sensor: string) {
            if (deviceId !== undefined) {
                const sensorData = await services.getSensorData(deviceId, sensor);
                setSensorsData((prev) => [...prev, sensorData]);
            }
        }
        try {
            if (availableSensors.length === 0) {
                fetchAvailableSensors();
            } else {
                availableSensors.forEach((sensor) => fetchSensorData(sensor));
            }
        } catch (e: any) {
            setErrorMessage(e.message);
        }
    }, [availableSensors]);


    function exportToCsv(sensorsData: SensorData[]) {
        let csvContent = "data:text/csv;charset=utf-8,";

        csvContent += "Date,PH,Temperature\n";

        sensorsData.forEach(function (sensorRecord) {
            sensorRecord.records.forEach(function (record) {
                const row = record.date + "," + record.value + "\n";
                csvContent += row;
            });
        });

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "data.csv");
        document.body.appendChild(link); // Required for FF

        link.click();
    }

    const handlePeriodSelection = (event: React.ChangeEvent<HTMLSelectElement>) => {


        const value = event.target.value;
        switch (value) {
            case "today":
                setTimeUnit("hour");
                const today = new Date(new Date().setHours(0));
                const tomorrow = new Date(new Date().setHours(23));
                setStart(today); // today
                setEnd(tomorrow); // tomorrow
                break;
            case "last5days":
                setTimeUnit("day");
                const today2 = new Date(new Date().setHours(0, 0, 0, 0));
                const yesterday2 = new Date(today2.getTime() - 1);
                const fiveDaysAgo = new Date(today2.getTime() - (5 * 24 * 60 * 60 * 1000));
                setStart(fiveDaysAgo); // 5 days ago
                setEnd(yesterday2);
                break;
            case "last31days":
                setTimeUnit("day");
                const today3 = new Date(new Date());
                const yesterday = new Date(today3.getTime() - (1 * 24 * 60 * 60 * 1000));
                const lastMonth = new Date(today3.getFullYear(), today3.getMonth() - 1, today3.getDate());
                setStart(lastMonth); // last month
                setEnd(yesterday); // today
                break;
            case "last3months":
                setTimeUnit("month");
                const today4 = new Date();
                const threeMonthsAgo = new Date(today4.getFullYear(), today4.getMonth() - 3, 1);
                const lastMonth2 = new Date(today4.getFullYear(), today4.getMonth() - 1, today4.getDate());
                setStart(threeMonthsAgo); // 3 months ago
                setEnd(lastMonth2); // last month
                break;
            case "last6months":
                setTimeUnit("month");
                const today5 = new Date();
                const sixMonthsAgo = new Date(today5.getFullYear(), today5.getMonth() - 6, 1);
                const lastMonth3 = new Date(today5.getFullYear(), today5.getMonth() - 1, today5.getDate());
                setStart(sixMonthsAgo); // 6 months ago
                setEnd(lastMonth3); // last month
                break;
            case "lastyear":
                setTimeUnit("month");
                const today6 = new Date();
                const lastYear = new Date(today6.getFullYear() - 1, today6.getMonth(), today6.getDate());
                const lastMonth4 = new Date(today6.getFullYear(), today6.getMonth() - 1, today6.getDate());
                setStart(lastYear); // last year
                setEnd(lastMonth4); // last month
                break;
            case "last2years":
                setTimeUnit("year");
                const today7 = new Date();
                const lastYear2 = new Date(today7.getFullYear() - 1, today7.getMonth(), today7.getDate());
                const twoYearsAgo = new Date(today7.getFullYear() - 2, today7.getMonth(), today7.getDate());
                setStart(twoYearsAgo); // last year
                setEnd(lastYear2); // 2 years ago
                break;
            case "last5years":
                setTimeUnit("year");
                const today8 = new Date();
                const lastYear3 = new Date(today8.getFullYear() - 1, today8.getMonth(), today8.getDate());
                const fiveYearsAgo = new Date(today8.getFullYear() - 5, today8.getMonth(), today8.getDate());
                setStart(fiveYearsAgo); // 5 years ago
                setEnd(lastYear3); // last year
                break;
            case "last10years":
                setTimeUnit("year");
                const today9 = new Date();
                const lastYear4 = new Date(today9.getFullYear() - 1, today9.getMonth(), today9.getDate());
                const tenYearsAgo = new Date(today9.getFullYear() - 10, today9.getMonth(), today9.getDate());
                setStart(tenYearsAgo); // 10 years ago
                setEnd(lastYear4); // last year
                break;
            default:
                break;
        }
    };
    const navigate = useNavigate()
    return (
        <div className={"graph-view"}>
            <div className={"chart-buttons"}>
                <select onChange={handlePeriodSelection}>
                    <option value="">Select period</option>
                    <option value="today">Today</option>
                    <option value="last5days">Last 5 days</option>
                    <option value="last31days">Last 31 days</option>
                    <option value="last3months">Last 3 months</option>
                    <option value="last6months">Last 6 months</option>
                    <option value="lastyear">Last year</option>
                    <option value="last2years">Last 2 years</option>
                    <option value="last5years">Last 5 years</option>
                    <option value="last10years">Last 10 years</option>
                </select>
            </div>
            {timeUnit && <MyChart start={start} end={end} timeUnit={timeUnit} sensorsData={sensorsData} />}
            <div className={"get-information"}>
                <div>
                <p><b>Device identifier: </b>{deviceId}</p>
                <p><b>Associated email: </b>{deviceEmail}</p>
                </div>
                <div className={"buttons-graph"}>
                    {timeUnit && <button id={"export-csv"} onClick={() => exportToCsv(sensorsData)}>
                        Export CSV
                    </button>
                    }
                    {timeUnit &&<button className={"navigate-device"} onClick={()=> navigate("/auth/login") }>Devices </button>}
                </div>
            </div>

        </div>
    );
}
