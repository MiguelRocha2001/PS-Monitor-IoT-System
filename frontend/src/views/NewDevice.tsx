import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Card} from "react-bootstrap";

function NewDevice() {
    return (
        <Card>
            <Card.Body>
                <Card.Title>Add a new IoT Device</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">Fill in the form</Card.Subtitle>
                <NewIoTDeviceForm />
            </Card.Body>
        </Card>
    );
}

export default NewDevice;

function NewIoTDeviceForm() {
    return(
        <Form>
            <fieldset disabled={false}>
                <Form.Group className="mb-3">
                    <Form.Label>Device Id</Form.Label>
                    <Form.Control placeholder="Device Id" />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control placeholder="Email" />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Mobile</Form.Label>
                    <Form.Control
                        placeholder="Mobile Number"
                        type={"number"}
                        min={100000000}
                        max={999999999}
                    />
                </Form.Group>
                <Button type="submit">Submit</Button>
            </fieldset>
        </Form>
    );
}