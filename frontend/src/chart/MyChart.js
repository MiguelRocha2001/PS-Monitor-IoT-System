const {useChart} = require("./useChart");
const {dataSet} = require("./data");
const React = require("react");

const dataSample = [
    {x: 'January', y: 10},
    {x: 'February', y: 5},
    {x: 'March', y: 15},
    {x: 'April', y: 20},
    {x: 'May', y: 30},
    {x: 'June', y: 40},
    {x: 'July', y: 20},
    {x: 'August', y: 30},
    {x: 'September', y: 50},
    {x: 'October', y: 60},
    {x: 'November', y: 30},
    {x: 'December', y: 25},
]

// months
const labels = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
];

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

    const phDataset = data["PH"].isVisible ? {
        label: data["PH"].label,
            data: data["PH"].data,
            backgroundColor: data["PH"].bgColor,
            borderColor: data["PH"].borderColor,
            borderWidth: 1
    } : {};

    const tempDataset = data["Temperature"].isVisible ? {
        label: data["Temperature"].label,
        data: data["Temperature"].data,
        backgroundColor: data["Temperature"].bgColor,
        borderColor: data["Temperature"].borderColor,
        borderWidth: 1
    } : {};

    const config = {
        type: "line",
        data: {
            labels: labels,
            datasets: [phDataset, tempDataset]
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
