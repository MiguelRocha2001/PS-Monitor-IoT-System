import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {services} from "../../services/services";
import {Device} from "../../services/domain";
import {Loading} from "../Loading";
import {MyCard} from "../Commons";

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
            <MyCard title={'Device Info'} children={<></>}/>
        );
}