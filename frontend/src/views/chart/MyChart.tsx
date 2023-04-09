import {JSXElementConstructor, ReactElement, ReactFragment, ReactPortal} from "react";
import {PhRecord} from "../../services/domain";
import {mapToLabel, toLabels} from "./chartLabels";

const {useChart} = require("./useChart");
const {dataSet} = require("./data");
const React = require("react");

export enum Period {SECOND, MINUTE, HOUR, DAY, MONTH, YEAR}

export function MyChart(
    {period, phRecords, tempRecords}: { period: Period, phRecords: PhRecord[], tempRecords: PhRecord[] }
) {
    const canvasRef = React.useRef(null);
    const [data, setData] = React.useState(dataSet);

    const handleToggleBars = (property: string) => {
        setData((prev: { [x: string]: { isVisible: any; }; }) => ({
            ...prev,

            [property]: {
                ...prev[property],

                isVisible: !prev[property].isVisible
            }
        }));
    };

    const labels = toLabels(period)

    const phDates = phRecords.map((phRecord) => {
        return phRecord.date
    })
    const phData = mapToLabel(period, phDates)

    const tempDates = tempRecords.map((tempRecord) => {
        return tempRecord.date
    })
    const tempData = mapToLabel(period, tempDates)

    const phDataset = data["PH"].isVisible ? {
        label: data["PH"].label,
        // data: data["PH"].data,
        data: phData,
        backgroundColor: data["PH"].bgColor,
        borderColor: data["PH"].borderColor,
        borderWidth: 1
    } : {};

    const tempDataset = data["Temperature"].isVisible ? {
        label: data["Temperature"].label,
        // data: data["Temperature"].data,
        data: tempData,
        backgroundColor: data["Temperature"].bgColor,
        borderColor: data["Temperature"].borderColor,
        borderWidth: 1
    } : {};

    const dataset =
        [phDataset, tempDataset]
        .filter((d: any) => Object.keys(d).length > 0);

    const config = {
        type: "line",
        data: {
            labels: labels,
            datasets: dataset
        },
        options: {
            legend: {
                display: true,
                labels: {
                    fontColor: "#ff0000"
                }
            },
            scales: {
                yAxes: [
                    {
                        ticks: {
                            beginAtZero: true
                        }
                    }
                ]
            }
        }
    };

    useChart(canvasRef, config);

    return (
        <div className="App">
            <canvas ref={canvasRef} width="400" height="200"/>

            {Object.keys(dataSet).map(key => (
                <Button
                    key={key}
                    handleToggleBars={handleToggleBars}
                    options={{
                        caption: key,
                        bgColor: data[key].bgColor,
                        borderColor: data[key].borderColor,
                        isVisible: data[key].isVisible
                    }}
                />
            ))}
        </div>
    );
}

const Button = (props: { options: { isVisible: any; borderColor: string | (string & {}) | undefined; bgColor: string | (string & {}) | undefined; caption: string | number | boolean | ReactElement<any, string | JSXElementConstructor<any>> | ReactFragment | ReactPortal | null | undefined; }; handleToggleBars: (arg0: any) => void; }) => (
    <button
        style={{
            marginTop: "32px",
            marginLeft: "24px",
            outline: "none",
            borderColor: props.options.isVisible
                ? props.options.borderColor
                : "#bfbfbf",
            backgroundColor: props.options.isVisible
                ? props.options.bgColor
                : "#efefef",
            color: props.options.isVisible ? "#444" : "#666",
            transitionDuration: ".2s",
            transitionProperty: "box-shadow"
        }}
        onClick={() => props.handleToggleBars(props.options.caption)}
        onFocus={event => {
            event.target.style.boxShadow = `0 0 0 2px ${props.options.borderColor}`;
        }}
        onBlur={event => {
            event.target.style.boxShadow = "initial";
        }}
    >
        {props.options.caption}
    </button>
);
