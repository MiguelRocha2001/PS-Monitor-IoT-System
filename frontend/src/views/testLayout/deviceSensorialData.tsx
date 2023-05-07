import Card from 'react-bootstrap/Card';
import React, {useEffect, useReducer} from "react";
import {useParams} from "react-router-dom";
import {Alert, Col, Row, Stack} from "react-bootstrap";
import {services} from "../../services/services";
import {PhRecord, TemperatureRecord} from "../../services/domain";
import {Day, Hour, Month, MyChart, Period, Year} from "../chart/MyChart";
import {SomethingWentWrong} from "../SomethingWentWrong";
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import ButtonToolbar from 'react-bootstrap/ButtonToolbar';
import Button from "react-bootstrap/Button";
import Form from 'react-bootstrap/Form';
import { Dropdown, DropdownButton } from 'react-bootstrap';
import "./sensorialData.css"

export type State =
    {
        type : "periodNotSelected"
    }
    |
    {
        type : "setYearPeriod",
        year: Year
    }
    |
    {
        type : "setMonthPeriod",
        month: Month
    }
    |
    {
        type : "setDayPeriod",
        day: Day
    }
    |
    {
        type : "setHourPeriod",
        hour: Hour
    }

type Action =
    {
        type : "setYearPeriod",
        year: Year
    }
    |
    {
        type : "setMonthPeriod",
        month: Month
    }
    |
    {
        type : "setDayPeriod",
        day: Day
    }
    |
    {
        type : "setHourPeriod",
        hour: Hour
    }

function reducer(state:State, action:Action): State {
    switch(action.type){
        case "setYearPeriod": return {type: "setYearPeriod", year: action.year};
        case "setMonthPeriod": return {type: "setMonthPeriod", month: action.month};
        case "setDayPeriod": return {type: "setDayPeriod", day: action.day};
        case "setHourPeriod": return {type: "setHourPeriod", hour: action.hour};
    }
}
interface DeviceProps {
    id: string;
    email: string;
}

export function DeviceSensorialData(device: DeviceProps) {
    return (
        <>
            <Graph deviceId={device.id} email={device.email}/>
        </>
    );
}

enum PeriodType { YEAR, MONTH, DAY, HOUR }

function Graph({deviceId,email}: { deviceId: string | undefined, email: string | undefined}) {
    const [phRecords, setPhRecords] = React.useState<PhRecord[]>([]);
    const [tempRecords, setTempRecords] = React.useState<TemperatureRecord[]>([]);
    const [errorMessage, setErrorMessage] = React.useState<string | undefined>(undefined);
    const [state, dispatcher] = useReducer(reducer, {type : "periodNotSelected"});

    /*
    switch (state.type) {
        case "setYearPeriod":
            console.log(state.year.year);
            break;
        case "setMonthPeriod":
            console.log("year: ", state.month.year.year);
            console.log("month: ", state.month.month);
            break;
        case "setDayPeriod":
            console.log(state.day.month.year.year);
            console.log(state.day.month.month);
            console.log(state.day.day);
            break;
        case "setHourPeriod":
            console.log(state.hour.day.month.year.year);
            console.log(state.hour.day.month.month);
            console.log(state.hour.day.day);
            console.log(state.hour.hour);
            break;
    }
     */

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

    function periodHandler(periodType: PeriodType) {
        const year = new Year(2020);
        const month = new Month(1, year);
        const day = new Day(1, month);
        const hour = new Hour(1, day);
        switch (periodType) {
            case PeriodType.YEAR:
                dispatcher({type: "setYearPeriod", year});
                break;
            case PeriodType.MONTH:
                dispatcher({type: "setMonthPeriod", month});
                break;
            case PeriodType.DAY:
                dispatcher({type: "setDayPeriod", day});
                break;
            case PeriodType.HOUR:
                dispatcher({type: "setHourPeriod", hour});
                break;
        }
    }

    function onYearChangeHandler(year: number) {
        switch (state.type) {
            case "setYearPeriod":
                dispatcher({type: "setYearPeriod", year: new Year(year)});
                break;
            case "setMonthPeriod":
                dispatcher({type: "setMonthPeriod", month: new Month(state.month.month, new Year(year))});
                break;
            case "setDayPeriod":
                dispatcher({type: "setDayPeriod", day: new Day(state.day.day, new Month(state.day.month.month, new Year(year)))});
                break;
            case "setHourPeriod":
                dispatcher({type: "setHourPeriod", hour: new Hour(state.hour.hour, new Day(state.hour.day.day, new Month(state.hour.day.month.month, new Year(year))))});
                break;
        }
    }

    function onMonthChangeHandler(month: number) {
        switch (state.type) {
            case "setMonthPeriod":
                dispatcher({type: "setMonthPeriod", month: new Month(month, state.month.year)});
                break;
            case "setDayPeriod":
                dispatcher({type: "setDayPeriod", day: new Day(state.day.day, new Month(month, state.day.month.year))});
                break;
            case "setHourPeriod":
                dispatcher({type: "setHourPeriod", hour: new Hour(state.hour.hour, new Day(state.hour.day.day, new Month(month, state.hour.day.month.year)))});
                break;
        }
    }

    function onDayChangeHandler(day: number) {
        switch (state.type) {
            case "setDayPeriod":
                dispatcher({type: "setDayPeriod", day: new Day(day, state.day.month)});
                break;
            case "setHourPeriod":
                dispatcher({type: "setHourPeriod", hour: new Hour(state.hour.hour, new Day(day, state.hour.day.month))});
                break;
        }
    }

    function onHourChangeHandler(hour: number) {
        switch (state.type) {
            case "setHourPeriod":
                dispatcher({type: "setHourPeriod", hour: new Hour(hour, state.hour.day)});
                break;
        }
    }

    const displayYearSelector = state.type !== "periodNotSelected"
    const displayMonthSelector = state.type !== "periodNotSelected" && state.type !== "setYearPeriod"
    const displayDaySelector = state.type !== "periodNotSelected" && state.type !== "setYearPeriod" && state.type !== "setMonthPeriod"
    const displayHourSelector = state.type !== "periodNotSelected" && state.type !== "setYearPeriod" && state.type !== "setMonthPeriod" && state.type !== "setDayPeriod"

    const period = state.type === "setYearPeriod" ? state.year : state.type === "setMonthPeriod" ? state.month : state.type === "setDayPeriod" ? state.day : state.type === "setHourPeriod" ? state.hour : undefined
    const chart = period !== undefined ? <MyChart period={period} phRecords={phRecords} tempRecords={tempRecords}/> : undefined

    if (errorMessage !== undefined) {
        return (<SomethingWentWrong details={errorMessage} />);
    } else {
        return (
            <Card>
                <Card.Body>
                    <Card.Title>Sensorial Data</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">Device Id: {deviceId}</Card.Subtitle>
                    <Card.Subtitle className="mb-2 text-muted">Associated Email: {email}</Card.Subtitle>
                    <br/>
                    <Stack gap={5}>
                        {chart}
                        <Col>
                            <Stack gap={3} style={{width: '70%'}}>
                                <PeriodSelector handler={(periodType) => periodHandler(periodType)}/>
                                {displayYearSelector && <YearSelector setYear={(year: number) => onYearChangeHandler(year)}/>}
                                {displayMonthSelector && <MonthSelector setMonth={(month: number) => onMonthChangeHandler(month)}/>}
                                {displayDaySelector && <DaySelector setDay={(day: number) => onDayChangeHandler(day)}/>}
                                {displayHourSelector && <HourSelector setHour={(hour: number) => onHourChangeHandler(hour)}/>}
                            </Stack>
                        </Col>
                    </Stack>
                </Card.Body>
            </Card>
        )
    }
}

// TODO -> add more options, later
function PeriodSelector({handler}: { handler: (periodType: PeriodType) => void }) {
    return (
        <DropdownButton title="Filter by date" variant="secondary">
            <Dropdown.Item onClick={() => handler(PeriodType.YEAR)}>Year</Dropdown.Item>
            <Dropdown.Item onClick={() => handler(PeriodType.MONTH)}>Month</Dropdown.Item>
            <Dropdown.Item onClick={() => handler(PeriodType.DAY)}>Day</Dropdown.Item>
            <Dropdown.Item onClick={() => handler(PeriodType.HOUR)}>Hour</Dropdown.Item>
        </DropdownButton>
    );
}

function YearSelector({setYear}: { setYear: (year: number) => void }) {
    return (
        <DropdownButton title="Select year" variant="secondary">
            <Dropdown.Item onClick={() => setYear(2021)}>2021</Dropdown.Item>
            <Dropdown.Item onClick={() => setYear(2020)}>2020</Dropdown.Item>
            <Dropdown.Item onClick={() => setYear(2019)}>2019</Dropdown.Item>
            <Dropdown.Item onClick={() => setYear(2018)}>2018</Dropdown.Item>
        </DropdownButton>
    );
}

function MonthSelector({setMonth}: { setMonth: (month: number) => void }) {
    const months = [
        {value: 0, label: 'January'},
        {value: 1, label: 'February'},
        {value: 2, label: 'March'},
        {value: 3, label: 'April'},
        {value: 4, label: 'May'},
        {value: 5, label: 'June'},
        {value: 6, label: 'July'},
        {value: 7, label: 'August'},
        {value: 8, label: 'September'},
        {value: 9, label: 'October'},
        {value: 10, label: 'November'},
        {value: 11, label: 'December'}
    ];

    return (
        <Dropdown onSelect={(value: any) => setMonth(+value)}>
            <Dropdown.Toggle variant="light" id="dropdown-basic">
                Month
            </Dropdown.Toggle>
            <Dropdown.Menu>
                {months.map((month) => (
                    <Dropdown.Item key={month.value} eventKey={month.value}>
                        {month.label}
                    </Dropdown.Item>
                ))}
            </Dropdown.Menu>
        </Dropdown>
    );
}

function DaySelector({setDay}: { setDay: (day: number) => void }) {
    const days = Array.from(Array(31).keys()).map(day => day + 1);

    return (
        <Dropdown onSelect={(value: any) => setDay(+value)}>
            <Dropdown.Toggle variant="light" id="dropdown-basic">
                Day
            </Dropdown.Toggle>
            <Dropdown.Menu>
                {days.map((day) => (
                    <Dropdown.Item key={day} eventKey={day}>
                        {day}
                    </Dropdown.Item>
                ))}
            </Dropdown.Menu>
        </Dropdown>
    );
}

function HourSelector({setHour}: { setHour: (hour: number) => void }) {
    const hours = Array.from(Array(24).keys());

    return (
        <Dropdown onSelect={(value: any) => setHour(+value)}>
            <Dropdown.Toggle variant="light" id="dropdown-basic">
                Hour
            </Dropdown.Toggle>
            <Dropdown.Menu>
                {hours.map((hour) => (
                    <Dropdown.Item key={hour} eventKey={hour}>
                        {hour}
                    </Dropdown.Item>
                ))}
            </Dropdown.Menu>
        </Dropdown>
    );
}


