import React, {useEffect} from "react";
import Card from "react-bootstrap/Card";
import {ChooseDevice} from "./Commons";
import {PhData} from "../services/domain";
import {services} from "../services/services";
import {Row} from "react-bootstrap";
import {MyChart} from "../chart/MyChart";

export function Temperature() {
    const [selectedDevice, setSelectedDevice] = React.useState<string | undefined>(undefined);
    function onDeviceSelected(deviceId: string) {
        setSelectedDevice(deviceId);
    }

    return (
        <Card>
            <Card.Body>
                <ChooseDevice onDeviceSelected={onDeviceSelected}/>
                <TempGraph deviceId={selectedDevice} />
            </Card.Body>
        </Card>
    );
}

function TempGraph({deviceId}: { deviceId: string | undefined}) {
    const [phData, setPhData] = React.useState<PhData[]>([]);

    useEffect(() => {
        async function fetchTemp() {
            if (deviceId !== undefined) {
                const ph = await services.getTemperatureData(deviceId);
            }
        }
        fetchTemp();
    }, [deviceId]);

    return (
        <Card>
            <Card.Body>
                <Card.Title>Temperature Graph</Card.Title>
                <Card.Text>
                    Later, this will display the <b>real</b> Temperature graph.
                    <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                        <MyChart />
                    </Row>
                </Card.Text>
            </Card.Body>
        </Card>
    )
}