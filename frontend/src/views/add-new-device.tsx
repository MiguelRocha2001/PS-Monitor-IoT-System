import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {Card, Container} from "react-bootstrap";

function AddNewDeviceView() {
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

export default AddNewDeviceView;

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