import Card from 'react-bootstrap/Card';
import {MyChart} from "../chart/MyChart";
import React, {useEffect} from "react";
import {Row} from "react-bootstrap";
import {services} from "../services/services";
import {Device, PhData} from "../services/domain";
import {ChooseDevice} from "./Commons";

export function Ph() {
    const [selectedDevice, setSelectedDevice] = React.useState<string | undefined>(undefined);
    function onDeviceSelected(deviceId: string) {
        setSelectedDevice(deviceId);
    }

    return (
        <Card>
            <Card.Body>
                <ChooseDevice onDeviceSelected={onDeviceSelected}/>
                <PhGraph deviceId={selectedDevice} />
            </Card.Body>
        </Card>
    );
}

function PhGraph({deviceId}: { deviceId: string | undefined}) {
    const [phData, setPhData] = React.useState<PhData[]>([]);

    useEffect(() => {
        async function fetchPh() {
            if (deviceId !== undefined) {
                const ph = await services.getPhData(deviceId);
            }
        }
        fetchPh();
    }, [deviceId]);

    return (
        <Card>
            <Card.Body>
                <Card.Title>PH Graph</Card.Title>
                <Card.Text>
                    Later, this will display the <b>real</b> PH graph.
                    <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                        <MyChart />
                    </Row>
                </Card.Text>
            </Card.Body>
        </Card>
    )
}