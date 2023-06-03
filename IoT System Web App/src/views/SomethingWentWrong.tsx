import Card from 'react-bootstrap/Card';
import {Container, Row} from "react-bootstrap";
import React from "react";

// TODO: Still in progress alert component is duplicated (also placed in App.tsx). Refactor it.
export function SomethingWentWrong({details} : {details: string}) {
    return (
        <Container>
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