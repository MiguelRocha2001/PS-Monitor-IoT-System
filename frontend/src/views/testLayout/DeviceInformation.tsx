import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Device} from "../../services/domain";
import {useSetError} from "../error/ErrorContainer";
import {ChartWithPeriodSelection} from "./text";
import {Devices} from "./DevicesPage";
import {Navigate} from 'react-router-dom';
import "./DeviceInformation.css";

export function DeviceInfo() {
    const { userId } = useParams<string>() // if 'my' it should show the devices of the logged in user
    const setError = useSetError()
    const { deviceId } = useParams<string>()
    const [device, setDevice] = React.useState<Device | null>(null);
    const [seeLogs, setSeeLogs] = React.useState<boolean>(false);

    useEffect(() => {
        async function fetchDevice() {
            if (deviceId) { // TODO: SHOULDN'T BE NEEDED !!! passar email e id como props
                services.getDeviceById(deviceId)
                    .then(device => setDevice(device))
                    .catch(error => setError(error.message))
            }
        }
        fetchDevice();
    }, [deviceId]);

    if (device == null)
        return <></>
    else if (seeLogs)
        return <Navigate to={`/users/${userId}/devices/${deviceId}/logs`} replace={true}/>
    else
        return (
            <div className="app-container">
                <div className="devices-container">
                    <Devices userIdParam={userId}/>
                </div>
                <div className="chart-container">
                    <ChartWithPeriodSelection  deviceId={device.id} deviceEmail={device.alertEmail}/>
                </div>
                <div>
                    <button onClick={() => setSeeLogs(true)}>See Logs</button>
                </div>
            </div>
        );
}
