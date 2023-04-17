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
    {period, phRecords, tempRecords}: { period: Period, phRecords: PhRecord[], tempRecords: PhRecord[] }
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
