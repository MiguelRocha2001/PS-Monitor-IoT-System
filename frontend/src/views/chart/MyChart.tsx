import {JSXElementConstructor, ReactElement, ReactFragment, ReactPortal} from "react";
import {PhRecord} from "../../services/domain";
import {mapToData, toLabels} from "./chartLabels";
const {useChart} = require("./useChart");
const {dataSet} = require("./data");
const React = require("react");

export interface Period {}
export class Year implements Period {constructor(public year: number) {}}
export class Month implements Period {constructor(public month: number, public year: Year) {}}
export class Day implements Period {constructor(public day: number, public month: Month) {}}
export class Hour implements Period {constructor(public hour: number, public day: Day) {}}

export function MyChart(
    {period, phRecords, tempRecords}: { period: Period, phRecords: PhRecord[], tempRecords: PhRecord[] } //FIXME os dados nao estão bem representados
) {
    const canvasRef = React.useRef(null);
    const [metadata, setMetadata] = React.useState(dataSet);

    const handleToggleBars = (property: string) => {
        setMetadata((prev: { [x: string]: { isVisible: any; }; }) => ({
            ...prev,

            [property]: {
                ...prev[property],

                isVisible: !prev[property].isVisible
            }
        }));
    };

    const labels = toLabels(period)

    const phData = mapToData(period, phRecords)

    const tempData = mapToData(period, tempRecords)

    const phDataset = metadata["PH"].isVisible ? {
        label: metadata["PH"].label,
        data: phData,
        backgroundColor: metadata["PH"].bgColor,
        borderColor: metadata["PH"].borderColor,
        borderWidth: 1
    } : {};

    const tempDataset = metadata["Temperature"].isVisible ? {
        label: metadata["Temperature"].label,
        data: tempData,
        backgroundColor: metadata["Temperature"].bgColor,
        borderColor: metadata["Temperature"].borderColor,
        borderWidth: 1
    } : {};

    const dataset =
        [phDataset, tempDataset]
        .filter((d: any) => Object.keys(d).length > 0);

    if(dataset.length === 0) {
        console.log("No dataset")
    }

    //check if its temperature or ph dataset
    if(dataset.filter((d: any) => d.label === "Temperature").length === 1) {
        console.log("Temperature dataset")
    }
    if(dataset.filter((d: any) => d.label === "pH").length === 1) {
        console.log("PH dataset")
    }

    const labelY = dataset.filter((d: any) => d.label === "Temperature").length === 1 ? "Temperature" : "pH"
    const labelX = "Time in " + period.constructor.name //FIXME: miguel da fix disto


    //print dataset
    console.log(dataset)

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

    return (
        <div className="App">
            <canvas ref={canvasRef} width="600" height="400"/>

            {Object.keys(dataSet).map(key => (
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
