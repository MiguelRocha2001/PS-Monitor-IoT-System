import Card from 'react-bootstrap/Card';
import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {Row} from "react-bootstrap";
import {services} from "../../services/services";
import {PhRecord, TemperatureRecord} from "../../services/domain";
import {MyChart} from "../../chart/MyChart";
import {SomethingWentWrong} from "../SomethingWentWrong";
import {MyCard} from "../Commons";

export function DeviceCreated() {
    const { deviceId } = useParams<string>()

    return (
        <MyCard
            title="Device Created !!!"
            subtitle={`Device Id: ${deviceId}`}
            text={[
                "You can now use this device to send data to the server",
                "Now, insert this device id in your IoT device and start sending data."
            ]}
        />
    );
}