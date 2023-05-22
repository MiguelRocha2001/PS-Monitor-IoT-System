import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Card} from "react-bootstrap";
import {Navigate} from "react-router-dom"
import React from "react";
import {services} from "../../services/services";
import {useSetError} from "../error/ErrorContainer";
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
    const [isEmailWrong, setIsEmailWrong] = React.useState<boolean>(false);

    if (deviceId)
        return <Navigate to={`/device-created/${deviceId}`} replace={true}/>

    async function submitForm() {

        if (!email) {
            setIsEmailWrong(true)
            console.log("email is empty")
            return;
        }

        // Check if email is in a valid format
        const emailRegex = /^\S+@\S+\.\S+$/;
        if (!emailRegex.test(email)) {
            console.log("email is not valid")
            setIsEmailWrong(true)
            return;
        }
        setIsEmailWrong(false)
        services.createDevice(email)
            .then(deviceId => setDeviceId(deviceId))
            .catch(error => setError(error.message))
    }

    return(
        <Form>
            <fieldset disabled={false}>
                <Form.Group className="mb-3 ">
                    <Form.Label>This email will be used to send alerts about the device status </Form.Label>
                    <Form.Control id={isEmailWrong ? "wrong-email" : ""} placeholder="Email" onChange={(ev) => {
                        setEmail(ev.target.value)
                    }} />
                </Form.Group>
                <Button onClick={submitForm}>Submit</Button>
            </fieldset>
        </Form>
    );
}