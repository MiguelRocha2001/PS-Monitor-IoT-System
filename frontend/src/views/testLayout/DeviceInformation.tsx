import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Loading} from "../Loading";
import {MyCard, MyLink} from "../Commons";
import {Device} from "../../services/domain";
import {Col} from "react-bootstrap";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import "./DeviceInformation.css";

export function DeviceInfo() {
    const setError = useSetError()
    const { deviceId } = useParams<string>()
    const [device, setDevice] = React.useState<Device | null>(null);

    useEffect(() => {
        async function fetchDevice() {
            if (deviceId) { // TODO: SHOULDN'T BE NEEDED !!!
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
                <div className="card">
                    <DeviceInfoAux device={device} />
                    <button className="link-button">
                        <MyLink to={`/device-data/${device.id}`} center={false} text="Sensors" />
                    </button>
                </div>
            </ErrorController>
        );
}

function DeviceInfoAux({device}: { device: Device }) {
    return (
        <Col className = {"information-about-device"}>
            <p><b>Id:</b> {device.id}</p>
            <p><b>Email:</b> {device.email}</p>
        </Col>
    );
}