import Card from 'react-bootstrap/Card';
import {MyChart} from "../chart/MyChart";
import React from "react";
import {Row} from "react-bootstrap";

function Ph() {
    return (
        <Card>
            <Card.Body>
                <Card.Title>PH Graph</Card.Title>
                <Card.Text>
                    Later, this will display the PH graph.
                    <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                        <MyChart />
                    </Row>
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default Ph;