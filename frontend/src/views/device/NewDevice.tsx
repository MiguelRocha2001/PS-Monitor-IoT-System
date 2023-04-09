import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Card} from "react-bootstrap";
import {Navigate, useLocation, useNavigate} from "react-router-dom"
import React from "react";
import {services} from "../../services/services";
import {Device} from "../../services/domain";
import {SomethingWentWrong} from "../SomethingWentWrong";

function NewDevice() {
    const [error, setError] = React.useState<string | undefined>(undefined)

    if (error) return (<SomethingWentWrong details={error} />)
    else return (
        <Card>
            <Card.Body>
                <Card.Title>Add a new IoT Device</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">Fill in the form</Card.Subtitle>
                <NewIoTDeviceForm onError={(msg) => setError(msg)}/>
            </Card.Body>
        </Card>
    );
}

export default NewDevice;

function NewIoTDeviceForm({onError}: { onError: (error: string) => void }) {
    const navigate = useNavigate()

    const [deviceId, setDeviceId] = React.useState<string | undefined>(undefined);
    const [email, setEmail] = React.useState<string>("");

    if (deviceId)
        return <Navigate to={`/device-created/${deviceId}`} replace={true}/>

    async function submitForm() {
        try {
            const deviceId = await services.createDevice(email)
            setDeviceId(deviceId)
        } catch (e: any) {
            onError(e.message)
        }
    }

    return(
        <Form>
            <fieldset disabled={false}>
                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control placeholder="Email" onChange={(ev) => {
                        setEmail(ev.target.value)
                    }} />
                </Form.Group>
                <Button onClick={submitForm}>Submit</Button>
            </fieldset>
        </Form>
    );
}