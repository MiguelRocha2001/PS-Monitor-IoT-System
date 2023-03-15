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
            <fieldset disabled={true}>
                <Form.Group className="mb-3">
                    <Form.Label htmlFor="disabledTextInput">Device Id</Form.Label>
                    <Form.Control id="disabledTextInput" placeholder="Disabled input" />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label htmlFor="disabledTextInput">Email</Form.Label>
                    <Form.Control id="disabledTextInput" placeholder="Disabled input" />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label htmlFor="disabledTextInput">Cellular</Form.Label>
                    <Form.Control
                        id="disabledTextInput"
                        placeholder="Disabled input"
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