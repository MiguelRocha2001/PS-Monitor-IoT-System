import {Device} from "../services/domain";
import React, {useEffect, useState} from "react";
import ListGroup from "react-bootstrap/ListGroup";
import {services} from "../services/services";
import Card from "react-bootstrap/Card";
import {NavLink, Stack} from "react-bootstrap";
import Button from "react-bootstrap/Button";

export function Devices() {
    const [devices, setDevices] = useState<Device[]>([])

    useEffect(() => {
        async function fetchDevices() {
            const devices = await services.getDevices()
            setDevices(devices)
        }
        fetchDevices()
    }, [])

    return (
        <Card>
            <Card.Body>
                <Stack gap={2}>
                    <Card.Title>Available Devices</Card.Title>
                    <Stack gap={3}>
                        <ListGroup>
                            {devices.map(device => (
                                <ListGroup.Item key={device.id}>
                                    <text style={{fontWeight: 'bold'}}>Device Id</text>
                                    <br/>
                                    <text> {device.id}</text>
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                        <NavLink href="/add-new-device"><Button variant="primary">Add New Device</Button></NavLink>
                    </Stack>
                </Stack>
            </Card.Body>
        </Card>
    )
}