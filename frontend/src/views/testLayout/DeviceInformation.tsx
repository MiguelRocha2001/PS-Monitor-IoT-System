import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Device} from "../../services/domain";
import {useSetError} from "../error/ErrorContainer";
import {ChartWithPeriodSelection} from "./text";
import {Devices} from "./DevicesPage";
import "./DeviceInformation.css";

export function DeviceInfo() {
    const setError = useSetError()
    const { deviceId } = useParams<string>()
    console.log(deviceId)
    const [device, setDevice] = React.useState<Device | null>(null);

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

    console.log(device)

    if (device == null)
        return <></>
    else
        return (
            <div className="app-container">
                <div className="devices-container">
                    <Devices />
                </div>
                <div className="chart-container">
                    <ChartWithPeriodSelection  deviceId={device.id} deviceEmail={device.email}/>
                </div>
            </div>
        );
}
