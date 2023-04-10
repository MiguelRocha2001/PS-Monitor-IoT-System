import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Loading} from "../Loading";
import {MyCard, MyLink} from "../Commons";
import {Device} from "../../services/domain";
import {Col} from "react-bootstrap";

export function DeviceInfo() {
    const { deviceId } = useParams<string>()
    const [device, setDevice] = React.useState<Device | null>(null);

    useEffect(() => {
        async function fetchDevice() {
            if (deviceId) { // TODO: SHOULDN'T BE NEEDED !!!
                const device = await services.getDevice(deviceId)
                setDevice(device)
            }
        }

        fetchDevice();
    }, [deviceId]);

    if (device == null)
        return <Loading />
    else
        return (
            <MyCard title={'Device Info'} boldTitle={true} children={
                <div>
                    <DeviceInfoAux device={device}/>
                    <MyLink to={`/device-data/${device.id}`} center={false} text={'Sensors'}/>
                </div>
            }/>
        );
}

function DeviceInfoAux({device}: { device: Device }) {
    return (
        <Col>
            <p><b>Id:</b> {device.id}</p>
            <p><b>Email:</b> {device.email}</p>
        </Col>
    );
}