import {Alert, Container, Row} from "react-bootstrap";
import React from "react";
import Card from "react-bootstrap/Card";

export function StillInProgressAlert() {
    return (
        <Container>
            <Alert variant="success">
                <Alert.Heading>Hey, nice to see you</Alert.Heading>
                <p>
                    Be advised that this is still a work in progress.
                    The contents of this website are not final.
                </p>
            </Alert>
            <Row className="justify-content-center">
                <Card>
                    <Card.Body>
                        <Card.Title>Loading</Card.Title>
                        <Card.Text>
                            Please wait while the application is loading.
                        </Card.Text>
                    </Card.Body>
                </Card>
            </Row>
        </Container>
    );
}
