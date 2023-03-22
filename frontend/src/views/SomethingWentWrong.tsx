import Card from 'react-bootstrap/Card';
import {Col, Row} from "react-bootstrap";
import React from "react";
import {StillInProgressAlert} from "./StillInProgressAlert";

// TODO: Still in progress alert component is duplicated (also placed in App.tsx). Refactor it.
export function SomethingWentWrong({details} : {details: string}) {
    return (
        <Row className="justify-content-center">
            <Col style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <StillInProgressAlert />
            </Col>
            <Card>
                <Card.Body>
                    <Card.Title>Something went wrong!</Card.Title>
                    <Card.Text>
                        {details}
                    </Card.Text>
                </Card.Body>
            </Card>
        </Row>
    );
}