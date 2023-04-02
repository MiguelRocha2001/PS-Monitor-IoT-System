import Card from 'react-bootstrap/Card';
import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {Row} from "react-bootstrap";
import {services} from "../../services/services";
import {PhRecord, TemperatureRecord} from "../../services/domain";
import {MyChart} from "../../chart/MyChart";
import {SomethingWentWrong} from "../SomethingWentWrong";

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
                    <Card.Text>
                        <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                            <MyChart phRecords={phRecords} tempRecords={tempRecords}/>
                        </Row>
                    </Card.Text>
                </Card.Body>
            </Card>
        )
    }
}