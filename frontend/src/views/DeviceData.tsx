import Card from 'react-bootstrap/Card';
import React, {useEffect} from "react";
import {Row} from "react-bootstrap";
import {services} from "../services/services";
import {Device, PhData, PhRecord} from "../services/domain";
import {ChooseDevice} from "./Commons";
import {MyChart} from "../chart/MyChart";

export function DeviceSensorialData() {
    const [selectedDevice, setSelectedDevice] = React.useState<string | undefined>(undefined);
    function onDeviceSelected(deviceId: string) {
        setSelectedDevice(deviceId);
    }

    return (
        <Card>
            <Card.Body>
                <ChooseDevice onDeviceSelected={onDeviceSelected}/>
                <Graph deviceId={selectedDevice} />
            </Card.Body>
        </Card>
    );
}

function Graph({deviceId}: { deviceId: string | undefined}) {
    const [phRecords, setPhRecords] = React.useState<PhRecord[]>([]);
    const [tempRecords, setTempRecords] = React.useState<PhRecord[]>([]);

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
        fetchPh();
        fetchTemp();
    }, [deviceId]);

    return (
        <Card>
            <Card.Body>
                <Card.Title>Device Sensorial Data</Card.Title>
                <Card.Text>
                    Later, this will display the <b>real</b> device sensorial data.
                    <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                        <MyChart phRecords={phRecords} tempRecords={tempRecords}/>
                    </Row>
                </Card.Text>
            </Card.Body>
        </Card>
    )
}