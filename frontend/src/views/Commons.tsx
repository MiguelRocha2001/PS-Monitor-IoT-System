import {Link} from 'react-router-dom'
import React, {useEffect} from "react";
import {Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";
import {Device} from "../services/domain";
import {services} from "../services/services";

export function MyLink({text, to, width, color, bold}: { text: string, to: string, width?: string, color?: string, bold?: boolean }) {
    const defaultWidth = 'auto';
    return (
        <Link
            to={to}
            style={{
                textAlign: 'center',
                margin: 'auto',
                marginLeft: '1em',
                marginRight: '1em',
                width: width ?? defaultWidth,
                textDecoration: 'none',
                color: color ?? 'black',
                fontWeight: bold ? 'bold' : 'normal',
            }}>
            {text}
        </Link>
    )
}

export function MyCard({children, title, text}: { children: any, title?: string, text?: string }) {
    return (
        <Row className="justify-content-center">
            <Card>
                <Card.Body>
                    <Card.Title>
                        {title}
                    </Card.Title>
                    <Card.Text>
                        {text}
                    </Card.Text>
                    {children}
                </Card.Body>
            </Card>
        </Row>
    );
}

export function ChooseDevice({onDeviceSelected}: { onDeviceSelected: (deviceId: string) => void}) {
    const [devices, setDevices] = React.useState<Device[]>([]);

    useEffect(() => {
        async function fetchDevices() {
            const devices = await services.getDevices();
            setDevices(devices);
        }
        fetchDevices();
    }, []);

    return (
        <Card>
            <Card.Body>
                <Card.Title>Choose Device</Card.Title>
                <Card.Text>
                    <select onChange={(e) => onDeviceSelected(e.target.value)}>
                        {devices.map(device => (
                            <option key={device.id} value={device.id}>{device.id}</option>
                        ))}
                    </select>
                </Card.Text>
            </Card.Body>
        </Card>
    );
}