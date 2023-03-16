import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {Alert, Container, NavLink, Row, Stack} from "react-bootstrap";
import React from "react";
import {StillInProgressAlert} from "./StillInProgressAlert";
import {MyNavLink} from "./Commons";

function Home() {
    return (
        <Row className="justify-content-center">
            <Card>
                <Card.Body>
                    <Card.Title>Home</Card.Title>
                    <Card.Text>
                        Welcome to Industrial IoT Solutions!
                        In here you can see the available devices, the PH and temperature data.
                    </Card.Text>
                </Card.Body>
            </Card>
        </Row>
    );
}

export default Home;