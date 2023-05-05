import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Card} from "react-bootstrap";
import {Navigate, useNavigate} from "react-router-dom"
import React from "react";
import {services} from "../../services/services";
import {SomethingWentWrong} from "../SomethingWentWrong";
import {useError, useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import './AddNewDevice.css'

function NewDevice() {
    return (
        <ErrorController>
            <Card>
                <Card.Body>
                    <Card.Title>Add a new Device</Card.Title>
                    <NewIoTDeviceForm />
                </Card.Body>
            </Card>
        </ErrorController>
    );
}

export default NewDevice;

function NewIoTDeviceForm() {
    const setError = useSetError()

    const [deviceId, setDeviceId] = React.useState<string | undefined>(undefined);
    const [email, setEmail] = React.useState<string>("");

    if (deviceId)
        return <Navigate to={`/device-created/${deviceId}`} replace={true}/>

    async function submitForm() {
        services.createDevice(email)
            .then(deviceId => setDeviceId(deviceId))
            .catch(error => setError(error))
    }

    return(
        <Form>
            <fieldset disabled={false}>
                <Form.Group className="mb-3">
                    <Form.Label>This email will be used to send alerts about the device status </Form.Label>
                    <Form.Control placeholder="Email" onChange={(ev) => {
                        setEmail(ev.target.value)
                    }} />
                </Form.Group>
                <Button onClick={submitForm}>Submit</Button>
            </fieldset>
        </Form>
    );
}