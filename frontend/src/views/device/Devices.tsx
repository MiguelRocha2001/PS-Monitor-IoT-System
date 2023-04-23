import {Device} from "../../services/domain";
import React, {useEffect, useState} from "react";
import ListGroup from "react-bootstrap/ListGroup";
import {services} from "../../services/services";
import Card from "react-bootstrap/Card";
import {Stack} from "react-bootstrap";
import {MyLink} from "../Commons";
import {SomethingWentWrong} from "../SomethingWentWrong";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";

export function Devices() {
    const setError = useSetError()
    const [devices, setDevices] = useState<Device[]>([])

    useEffect(() => {
        async function fetchDevices() {
            services.getDevices()
                .then(devices => setDevices(devices))
                .catch(error => setError(error))
        }
        fetchDevices()
    }, [])

    return (
        <ErrorController>
            <Card>
                <Card.Body>
                    <Stack gap={2}>
                        <Card.Title>Available Devices</Card.Title>
                        <Stack gap={3}>
                            <ListGroup>
                                {devices.map(device => (
                                    <ListGroup.Item key={device.id}>
                                        <MyLink
                                            text={'Info Here'}
                                            to={`/devices/${device.id}`}
                                            center={false}
                                        />
                                        <br/>
                                        {device.id}
                                    </ListGroup.Item>
                                ))}
                            </ListGroup>
                            <MyLink
                                to="/add-new-device"
                                text={'Add New Device'}
                                color={'DodgerBlue'}
                                bold={true}
                                center={false}
                            />
                        </Stack>
                    </Stack>
                </Card.Body>
            </Card>
        </ErrorController>
    )
}