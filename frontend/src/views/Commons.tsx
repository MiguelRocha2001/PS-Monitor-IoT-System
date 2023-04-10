import {Link} from 'react-router-dom'
import React, {useEffect} from "react";
import {Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";
import {Device} from "../services/domain";
import {services} from "../services/services";

export function MyLink({text, to, width, color, bold, center, margin}:
                           {
                               text: string,
                               to: string,
                               width?: string,
                               color?: string,
                               bold?: boolean,
                               center: boolean,
                               margin?: string
                           }) {
    const defaultWidth = 'auto';
    const defaultMargin = '1em';
    return (
        <Link
            to={to}
            style={{
                textAlign: center ? 'center' : 'left',
                margin: 'auto',
                marginLeft: margin ?? 0,
                marginRight: margin ?? 0,
                width: width ?? defaultWidth,
                textDecoration: 'none',
                color: color ?? 'black',
                fontWeight: bold ? 'bold' : 'normal',
            }}>
            {text}
        </Link>
    )
}

export function MyCard({children, title, boldTitle, text, subtitle }:
                           {
                               children?: any,
                               title?: string,
                               boldTitle?: boolean,
                               text?: string[],
                               subtitle?: string
                           }) {
    return (
        <Row className="justify-content-center">
            <Card style={{marginBottom: '3em'}}>
                <Card.Body>
                    <Card.Title>
                        {title && <b style={{fontWeight: boldTitle ? 'bold' : 'normal'}}>{title}</b>}
                    </Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        {subtitle}
                    </Card.Subtitle>
                    {text && text.map((line, index) => (
                        <Card.Text key={index}>
                            {line}
                        </Card.Text>
                    ))}
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