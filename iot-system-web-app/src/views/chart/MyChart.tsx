import {JSXElementConstructor, ReactElement, ReactFragment, ReactPortal, useEffect} from "react";
import {mapToData, toLabels} from "./chartLabels";
import {SensorData} from "../../services/domain";
import "./chart.css";

const {useChart} = require("./useChart");
const React = require("react");


export type TimeUnit = "hour" | "day" | "month" | "year";

export function MyChart(
    {start, end, timeUnit, sensorsData} :
        {
            start: Date
            end: Date
            timeUnit: TimeUnit
            sensorsData: SensorData[],
        }
) {
    const canvasRef = React.useRef(null);
    const [metadata, setMetadata] = React.useState(undefined);

    // metadata initialization
    useEffect(() => {
        const meta: any = {}
        sensorsData.forEach((sensorData: SensorData) => {
            const r = Math.floor(Math.random() * 256)
            const g = Math.floor(Math.random() * 256)
            const b = Math.floor(Math.random() * 256)
            meta[sensorData.type] = {
                isVisible: false,
                label: sensorData.type,
                // bgColor: "rgba(" + r + "," + g + "," + b + ",0.2)",
                // borderColor: "rgba(" + r + "," + g + "," + b + ",1)",
            }
        })
        setMetadata(meta);
    }, [sensorsData]);

    const handleToggleBars = (property: string) => {
        const newMetadata = {...metadata};
        Object.keys(newMetadata).map(key => ( // set all to false
            newMetadata[key].isVisible = false
        ));

        setMetadata((prev: { [x: string]: { isVisible: any; }; }) => ({
            ...prev,

            [property]: {
                ...prev[property],

                isVisible: !prev[property].isVisible
            }
        }));
    };

    const labels = toLabels(start, end, timeUnit);
    const datasets = (metadata !== undefined) ?
        sensorsData.map((sensorData: SensorData) => {
            const data = mapToData(start, end, timeUnit, sensorData.records);
            return metadata[sensorData.type].isVisible ? {
                label: sensorData.type,
                data: data,
                backgroundColor: metadata[sensorData.type].bgColor,
                borderColor: metadata[sensorData.type].borderColor,
                borderWidth: 1
            } : {};
        }) : [];

    const dataset =
        datasets.filter((d: any) => Object.keys(d).length > 0);

    if(dataset.length === 0) {
        console.log("No dataset")
    }


    const sensors = Object.keys(dataset)
    const labelY = (sensors.length > 0) ? dataset[0].label : ""
    const labelX = "Time in " + timeUnit

    const options = {
        scales: {
            xAxes: [{
                scaleLabel: {
                    display: true,
                    labelString: labelX
                }
            }],
            yAxes: [{
                ticks: {
                    beginAtZero: true
                },
                scaleLabel: {
                    display: true,
                    labelString: labelY
                }
            }]
        }
    };

    const config = {
        type: "line",
        data: {
            labels: labels,
            datasets: dataset
        },
        options: options
    }


    useChart(canvasRef, config);

    const sensorOptionButtons = (metadata !== undefined) ?
        Object.keys(metadata).map(key => (
                <Button
                    key={key}
                    handleToggleBars={handleToggleBars}
                    options={{
                        caption: key,
                        bgColor: metadata[key].bgColor,
                        borderColor: metadata[key].borderColor,
                        isVisible: metadata[key].isVisible
                    }}
                />
            )) : <></>;
    return (
        <div className="App">
            <canvas className={"chart"} ref={canvasRef}/>
            {sensorOptionButtons}
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
