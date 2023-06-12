import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Device, DeviceWakeUpLogs} from "../../services/domain";
import {useSetError} from "../error/ErrorContainer";
import {ChartWithPeriodSelection} from "./text";
import {Devices} from "./DevicesPage";
import {Navigate} from 'react-router-dom';
import "./DeviceInformation.css";
import {ListGroup} from "react-bootstrap";
import {Loading} from "../Loading";

export function DeviceLogs() {
    const { userId } = useParams<string>() // if 'self' it should show the devices of the logged in user
    const setError = useSetError()
    const { deviceId } = useParams<string>()
    const [logs, setLogs] = React.useState<DeviceWakeUpLogs | undefined>(undefined);

    useEffect(() => {
        async function fetchDeviceLogs() {
            if (deviceId) {
                services.getDeviceWakeUpLogs(deviceId)
                    .then(logs => setLogs(logs))
                    .catch(error => setError(error.message))
            }
        }
        fetchDeviceLogs();
    }, [deviceId]);


    if (logs == undefined)
        return <Loading />
    else
        return (
            <div className="app-container">
                <p>Device {deviceId} logs:<br/></p>
                <ListGroup>
                    {logs.logs.map((log, index) => (
                        <ListGroup.Item key={index}>
                            {log.reason + " at " + log.date}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            </div>
        );
}
