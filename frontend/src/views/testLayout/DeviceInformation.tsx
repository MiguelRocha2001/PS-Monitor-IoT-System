import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Loading} from "../Loading";
import {MyCard, MyLink} from "../Commons";
import {Device} from "../../services/domain";
import {Col} from "react-bootstrap";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import {DeviceSensorialData} from "./deviceSensorialData";
import {ChartWithPeriodSelection} from "./text";
import "./DeviceInformation.css";

export function DeviceInfo() {
    const setError = useSetError()
    const { deviceId } = useParams<string>()
    const [device, setDevice] = React.useState<Device | null>(null);

    useEffect(() => {
        async function fetchDevice() {
            if (deviceId) { // TODO: SHOULDN'T BE NEEDED !!! passar email e id como props
                services.getDevice(deviceId)
                    .then(device => setDevice(device))
                    .catch(error => setError(error))
            }
        }

        fetchDevice();
    }, [deviceId]);

    if (device == null)
        return <></>
    else
        return (
            <ErrorController>
                <div className={"device-sensor-data"}>
                    <ChartWithPeriodSelection deviceId={device.id}/>
                </div>
            </ErrorController>
        );
}

function DeviceInfoAux({device}: { device: Device }) {
    return (
        <div className = {"information-about-device"}>
            <p><b>Identifier:</b> {device.id}</p>
            <p><b>Associated email:</b> {device.email}</p>
        </div>
    );
}