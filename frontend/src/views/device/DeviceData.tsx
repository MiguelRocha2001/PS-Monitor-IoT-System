import Card from 'react-bootstrap/Card';
import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {Row, Stack} from "react-bootstrap";
import {services} from "../../services/services";
import {PhRecord, TemperatureRecord} from "../../services/domain";
import {MyChart, Period} from "../chart/MyChart";
import {SomethingWentWrong} from "../SomethingWentWrong";
import ButtonGroup from 'react-bootstrap/ButtonGroup';
import ButtonToolbar from 'react-bootstrap/ButtonToolbar';
import Button from "react-bootstrap/Button";

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

function Graph({deviceId}: { deviceId: string | undefined}) {
    const [phRecords, setPhRecords] = React.useState<PhRecord[]>([]);
    const [tempRecords, setTempRecords] = React.useState<TemperatureRecord[]>([]);
    const [errorMessage, setErrorMessage] = React.useState<string | undefined>(undefined);
    const [period, setPeriod] = React.useState<Period>(Period.DAY);

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

    if (errorMessage !== undefined) {
        return (<SomethingWentWrong details={errorMessage} />);
    } else {
        return ( // TODO -> maybe use <MyCard> here?
            <Card>
                <Card.Body>
                    <Card.Title>Device Sensorial Data</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">Device Id: {deviceId}</Card.Subtitle>
                    <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                        <Stack gap={3} style={{width: '100%'}}>
                            <MyChart period={period} phRecords={phRecords} tempRecords={tempRecords}/>
                            <PeriodSelector setPeriod={(period: Period) => setPeriod(period)}/>
                        </Stack>
                    </Row>
                </Card.Body>
            </Card>
        )
    }
}

// TODO -> add more options, later
function PeriodSelector({setPeriod}: { setPeriod: (period: Period) => void }) {
    return (
        <ButtonToolbar aria-label="Toolbar with button groups" style={{alignContent: 'center', margin: 'auto'}}>
            <ButtonGroup aria-label="Basic example">
                <Button variant={"secondary"} onClick={() => setPeriod(Period.YEAR)}>Year</Button>
                <Button variant={"secondary"} onClick={() => setPeriod(Period.MONTH)}>Month</Button>
                <Button variant={"secondary"} onClick={() => setPeriod(Period.DAY)}>Day</Button>
                <Button variant={"secondary"} onClick={() => setPeriod(Period.HOUR)}>Hour</Button>
            </ButtonGroup>
        </ButtonToolbar>
    );
}

export default PeriodSelector;