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

export type State =
    {
        type : "periodNotSelected"
    }
    |
    {
        type : "setYearPeriod",
        year : number
    }
    |
    {
        type : "setMonthPeriod",
        year: Year
    }
    |
    {
        type : "setDayPeriod",
        month: Month
    }
    |
    {
        type : "setHourPeriod",
        day: Day
    }
    |
    {
        type : "boundsNotSelected",
        periodType : PeriodType
    }


type Action =
    {
        type : "setYearPeriod",
        year : number
    }
    |
    {
        type : "setMonthPeriod",
        year: Year
    }
    |
    {
        type : "setDayPeriod",
        month: Month
    }
    |
    {
        type : "setHourPeriod",
        day: Day
    }

function reducer(state:State, action:Action): State {
    switch(action.type){
        case "setYearPeriod": return {type: "setYearPeriod", year: action.year};
        case "setMonthPeriod": return {type: "setMonthPeriod", year: action.year};
        case "setDayPeriod": return {type: "setDayPeriod", month: action.month};
        case "setHourPeriod": return {type: "setHourPeriod", day: action.day};
    }
}

export function DeviceSensorialData() {
    const { deviceId } = useParams<string>()
    return (
        <Card>
            <Card.Body>
                <Graph deviceId={deviceId} />
            </Card.Body>
        </Card>
    );
}

enum PeriodType { YEAR, MONTH, DAY, HOUR }

function Graph({deviceId}: { deviceId: string | undefined}) {
    const [phRecords, setPhRecords] = React.useState<PhRecord[]>([]);
    const [tempRecords, setTempRecords] = React.useState<TemperatureRecord[]>([]);
    const [errorMessage, setErrorMessage] = React.useState<string | undefined>(undefined);
    const [state, dispatcher] = useReducer(reducer, {type : "periodNotSelected"});

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

    function onBoundChange() {
        if (state.type === PeriodType.YEAR && year !== undefined) {
            setPeriod(new Year(year));
        } else if (periodType === PeriodType.MONTH && year !== undefined && month !== undefined) {
            const yearObj = new Year(year);
            setPeriod(new Month(yearObj));
        } else if (periodType === PeriodType.DAY && year !== undefined && month !== undefined && day !== undefined) {
            const yearObj = new Year(year);
            const monthObj = new Month(yearObj);
            setPeriod(new Day(monthObj));
        } else if (periodType === PeriodType.HOUR && year !== undefined && month !== undefined && day !== undefined) {
            const yearObj = new Year(year);
            const monthObj = new Month(yearObj);
            const dayObj = new Day(monthObj);
            setPeriod(new Hour(dayObj));
        } else if (periodType === PeriodType.HOUR && year !== undefined && month !== undefined && day !== undefined && hour !== undefined) {
            const yearObj = new Year(year);
            const monthObj = new Month(yearObj);
            const dayObj = new Day(monthObj);
            const hourObj = new Hour(dayObj);
            setPeriod(hourObj);
        }
    }

    if (errorMessage !== undefined) {
        return (<SomethingWentWrong details={errorMessage} />);
    } else if (period) {
        return ( // TODO -> maybe use <MyCard> here?
            <Common
                deviceId={deviceId}
                periodType={state.type}
                periodHandler={(periodType: PeriodType) => setPeriodType(periodType)}
                setYear={(year: number) => setYear(year)}
                setMonth={(month: number) => setMonth(month)}
                setDay={(day: number) => setDay(day)}
                setHour={(hour: number) => setHour(hour)}
                child={
                    <MyChart period={period} phRecords={phRecords} tempRecords={tempRecords}
                />}
            />
        )
    } else {
        return (
            <Common
                deviceId={deviceId}
                periodType={periodType}
                periodHandler={(periodType) => setPeriod(periodType)}
                setYear={(year: number) => setYear(year)}
                setMonth={(month: number) => setMonth(month)}
                setDay={(day: number) => setDay(day)}
                setHour={(hour: number) => setHour(hour)}
                child={
                    <Alert variant="info">
                        <Alert.Heading>Choose a period</Alert.Heading>
                        <p>
                            Please choose a period to display the data.
                        </p>
                    </Alert>
                }
            />
        )
    }
}

function Common(
    {deviceId, child, periodType, periodHandler, setYear, setMonth, setDay, setHour}:
    {
        deviceId: string | undefined,
        child: React.ReactNode,
        periodType: PeriodType | undefined
        periodHandler: (periodType: PeriodType) => void,
        setYear: (year: number) => void,
        setMonth: (month: number) => void,
        setDay: (day: number) => void,
        setHour: (hour: number) => void
    }
) {
    const displayYearSelector = periodType != undefined
    const displayMonthSelector = periodType !== undefined && periodType !== PeriodType.YEAR
    const displayDaySelector = periodType !== undefined && periodType !== PeriodType.YEAR && periodType !== PeriodType.MONTH
    const displayHourSelector = periodType !== undefined && periodType !== PeriodType.YEAR && periodType !== PeriodType.MONTH && periodType !== PeriodType.DAY

    return (
        <Card>
            <Card.Body>
                <Card.Title>Device Sensorial Data</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">Device Id: {deviceId}</Card.Subtitle>
                <br/>
                <Stack gap={5}>
                    {child}
                    <Col style={{width: '100%', alignContent: 'center'}}>
                        <Stack gap={3} style={{width: '70%'}}>
                            <PeriodSelector handler={(periodType) => periodHandler(periodType)}/>
                            {displayYearSelector && <YearSelector setYear={(year: number) => setYear(year)}/>}
                            {displayMonthSelector && <MonthSelector setMonth={(month: number) => setMonth(month)}/>}
                            {displayDaySelector && <DaySelector setDay={(day: number) => setDay(day)}/>}
                            {displayHourSelector && <HourSelector setHour={(hour: number) => setHour(hour)}/>}
                        </Stack>
                    </Col>
                </Stack>
            </Card.Body>
        </Card>
    )
}

// TODO -> add more options, later
function PeriodSelector({handler}: { handler: (periodType: PeriodType) => void }) {
    return (
        <ButtonToolbar aria-label="Toolbar with button groups" style={{alignContent: 'center', margin: 'auto'}}>
            <ButtonGroup aria-label="Basic example">
                <Button variant={"secondary"} onClick={() => handler(PeriodType.YEAR)}>Year</Button>
                <Button variant={"secondary"} onClick={() => handler(PeriodType.MONTH)}>Month</Button>
                <Button variant={"secondary"} onClick={() => handler(PeriodType.DAY)}>Day</Button>
                <Button variant={"secondary"} onClick={() => handler(PeriodType.HOUR)}>Hour</Button>
            </ButtonGroup>
        </ButtonToolbar>
    );
}

function YearSelector({setYear}: { setYear: (year: number) => void }) {
    return (
        <ButtonToolbar aria-label="Toolbar with button groups" style={{alignContent: 'center', margin: 'auto'}}>
            <ButtonGroup aria-label="Basic example">
                <Button variant={"secondary"} onClick={() => setYear(2021)}>2021</Button>
                <Button variant={"secondary"} onClick={() => setYear(2020)}>2020</Button>
                <Button variant={"secondary"} onClick={() => setYear(2019)}>2019</Button>
                <Button variant={"secondary"} onClick={() => setYear(2018)}>2018</Button>
            </ButtonGroup>
        </ButtonToolbar>
    );
}

function MonthSelector({setMonth}: { setMonth: (month: number) => void }) {
    return (
        <ButtonToolbar aria-label="Toolbar with button groups" style={{alignContent: 'center', margin: 'auto'}}>
            <ButtonGroup aria-label="Basic example">
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>January</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>February</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>March</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>April</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>May</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>June</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>July</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>August</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>September</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>October</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>November</Button>
                <Button variant={"secondary"} onClick={() => setMonth(2021)}>December</Button>
            </ButtonGroup>
        </ButtonToolbar>
    );
}

function DaySelector({setDay}: { setDay: (day: number) => void }) {
    return (
        <Form.Select aria-label="Default select example">
            {Array.from(Array(30).keys()).map((day) => {
                day = day + 1
                return <option key={day} onSelect={() => setDay(day)} value={day}>{day}</option>
            })}
        </Form.Select>
    );
}

function HourSelector({setHour}: { setHour: (hour: number) => void }) {
    return (
        <Form.Select aria-label="Default select example">
            {Array.from(Array(24).keys()).map((hour) => {
                hour = hour + 1
                return <option key={hour} onSelect={() => setHour(hour)} value={hour}>{hour}</option>
            })}
        </Form.Select>
    );
}