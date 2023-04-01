import Card from 'react-bootstrap/Card';
import {Alert, Col, Container, Row} from "react-bootstrap";
import React from "react";
import {StillInProgressAlert} from "./StillInProgressAlert";

// TODO: Still in progress alert component is duplicated (also placed in App.tsx). Refactor it.
export function SomethingWentWrong({details} : {details: string}) {
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
                        <Card.Title>Something went wrong!</Card.Title>
                        <Card.Text>
                            {details}
                        </Card.Text>
                    </Card.Body>
                </Card>
            </Row>
        </Container>
    );
}