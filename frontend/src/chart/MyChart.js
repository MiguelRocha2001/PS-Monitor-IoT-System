import React from "react";
import "./styles.css";
import {dataSet} from "./data";
import {useChart} from "./useChart";

export function MyChart() {
    const canvasRef = React.useRef(null);
    const [data, setData] = React.useState(dataSet);

    const handleToggleBars = color => {
        setData(prev => ({
            ...prev,

            [color]: {
                ...prev[color],

                isVisible: !prev[color].isVisible
            }
        }));
    };

    console.log(
        "re",
        Object.keys(data)
            .filter(key => data[key].isVisible)
            .map(key => data[key].value)
    );

    useChart(canvasRef, {
        type: "line",

        data: {
            labels: Object.keys(data).filter(key => data[key].isVisible),

            datasets: [
                {
                    label: "# of Votes",

                    data: Object.keys(data)
                        .filter(key => data[key].isVisible)
                        .map(key => data[key].value),

                    backgroundColor: Object.keys(data)
                        .filter(key => data[key].isVisible)
                        .map(key => data[key].bgColor),

                    borderColor: Object.keys(data)
                        .filter(key => data[key].isVisible)
                        .map(key => data[key].borderColor),

                    borderWidth: 1
                }
            ]
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
    });

    return (
        <div className="App">
            <h1>React Hook for the Chart JS </h1>

            <canvas ref={canvasRef} width="400" height="200" />

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

const Button = props => (
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
