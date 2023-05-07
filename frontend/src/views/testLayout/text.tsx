import React, {useEffect, useState} from "react";
import {Day, Hour, Month, MyChart, Period, Year} from "../chart/MyChart";
import {services} from "../../services/services";
import {PhRecord, TemperatureRecord} from "../../services/domain";
import "./text.css";


export function ChartWithPeriodSelection({deviceId}: { deviceId: string }) {
    const [phRecords, setPhRecords] = React.useState<PhRecord[]>([]);
    const [tempRecords, setTempRecords] = React.useState<TemperatureRecord[]>([]);
    const [errorMessage, setErrorMessage] = useState<string>();

    const [period, setPeriod] = useState<Period>();

    const handlePeriodButtonClick = (newPeriod: Period) => {
        setPeriod(newPeriod);
    };


    useEffect(() => {
        async function fetchPh() {
            if (deviceId !== undefined) {
                const ph = await services.getPhData(deviceId);
                setPhRecords(ph.records);
            }
        }
        async function fetchTemp() {
            if (deviceId !== undefined) {
                const temp = await services.getTemperatureData(deviceId);
                setTempRecords(temp.records);
            }
        }
        try {
            fetchPh();
            fetchTemp();
        } catch (e: any) {
            setErrorMessage(e.message);
        }
    }, [deviceId]);


    function exportToCsv(phRecords: PhRecord[], tempRecords: TemperatureRecord[]) {
        let csvContent = "data:text/csv;charset=utf-8,";

        csvContent += "Date,PH,Temperature\n";

        phRecords.forEach(function (phRecord) {
            csvContent += phRecord.date + "," + phRecord.value + "\n";
        });

        tempRecords.forEach(function (tempRecord) {
            csvContent += tempRecord.date + "," + tempRecord.value + "\n";
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
                setPeriod(new Day(new Date().getDate(), new Month(new Date().getMonth() + 1, new Year(new Date().getFullYear()))));
                break;
            case "last5days":
                setPeriod(new Day(new Date().getDate() - 4, new Month(new Date().getMonth() + 1, new Year(new Date().getFullYear()))));
                break;
            case "lastmonth":
                setPeriod(new Month(new Date().getMonth(), new Year(new Date().getFullYear())));
                break;
            case "last3months":
                setPeriod(new Month(new Date().getMonth() - 2, new Year(new Date().getFullYear())));
                break;
            case "last6months":
                setPeriod(new Year(new Date().getFullYear() - 1));
                break;
            case "lastyear":
                setPeriod(new Year(new Date().getFullYear() - 1));
                break;
            case "last2years":
                setPeriod(new Year(new Date().getFullYear() - 2));
                break;
            case "last5years":
                setPeriod(new Year(new Date().getFullYear() - 5));
                break;
            case "last10years":
                setPeriod(new Year(new Date().getFullYear() - 10));
                break;
            default:
                break;
        }
    };

    return (
        <div>
            <div className={"chart-buttons"}>
                <select onChange={handlePeriodSelection}>
                    <option value="">Select period</option>
                    <option value="today">Today</option>
                    <option value="last5days">Last 5 days</option>
                    <option value="lastmonth">Last month</option>
                    <option value="last3months">Last 3 months</option>
                    <option value="last6months">Last 6 months</option>
                    <option value="lastyear">Last year</option>
                    <option value="last2years">Last 2 years</option>
                    <option value="last5years">Last 5 years</option>
                    <option value="last10years">Last 10 years</option>
                </select>
            </div>
            {period && <MyChart period={period} phRecords={phRecords} tempRecords={tempRecords}/>}
            {period && <button id={"export-csv"} onClick={() => exportToCsv(phRecords, tempRecords)}>
                Export to CSV
            </button>}
        </div>
    );
}
