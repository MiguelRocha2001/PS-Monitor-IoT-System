import {Device} from "../services/domain";
import React, {useEffect, useState} from "react";
import ListGroup from "react-bootstrap/ListGroup";
import {services} from "../services/services";
import Card from "react-bootstrap/Card";
import {Stack} from "react-bootstrap";
import {MyLink} from "./Commons";
import {SomethingWentWrong} from "./SomethingWentWrong";

export function Devices() {
    const [devices, setDevices] = useState<Device[]>([])
    const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined)

    useEffect(() => {
        async function fetchDevices() {
            const devices = await services.getDevices()
            setDevices(devices)
        }
        try {
            fetchDevices()
        } catch (e: any) {
            setErrorMessage(e.message)
        }
    }, [])

    if (errorMessage == undefined) {
        return (
            <Card>
                <Card.Body>
                    <Stack gap={2}>
                        <Card.Title>Available Devices</Card.Title>
                        <Stack gap={3}>
                            <ListGroup>
                                {devices.map(device => (
                                    <ListGroup.Item key={device.id}>
                                        <b>Device Id</b>
                                        <br/>
                                        {device.id}
                                    </ListGroup.Item>
                                ))}
                            </ListGroup>
                            <MyLink to="/add-new-device" text={'Add New Device'} color={'DodgerBlue'} bold={true} />
                        </Stack>
                    </Stack>
                </Card.Body>
            </Card>
        )
    } else
        return (<SomethingWentWrong details={errorMessage} />);
}