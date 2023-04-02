import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Card} from "react-bootstrap";
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
    const [deviceId, setDeviceId] = React.useState<string>("");
    const [email, setEmail] = React.useState<string>("");
    const [mobile, setMobile] = React.useState<string>("");

    async function submitForm() {
        try {
            const device = new Device(deviceId, email, +mobile)
            await services.addDevice(device)
        } catch (e: any) {
            onError(e.message)
        }
    }

    return(
        <Form>
            <fieldset disabled={false}>
                <Form.Group className="mb-3">
                    <Form.Label>Device Id</Form.Label>
                    <Form.Control placeholder="Device Id" onChange={(ev) => {
                        setDeviceId(ev.target.value)
                    }} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control placeholder="Email" onChange={(ev) => {
                        setEmail(ev.target.value)
                    }} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Mobile</Form.Label>
                    <Form.Control
                        placeholder="Mobile Number"
                        type={"number"}
                        min={100000000}
                        max={999999999}
                        onChange={(ev) => {
                            setMobile(ev.target.value)
                        }}
                    />
                </Form.Group>
                <Button onClick={submitForm}>Submit</Button>
            </fieldset>
        </Form>
    );
}