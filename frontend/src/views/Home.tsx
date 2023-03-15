import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {Alert, Container, NavLink, Row, Stack} from "react-bootstrap";
import React from "react";
import {StillInProgressAlert} from "./StillInProgressAlert";

function Home() {
    return (
        <Container>
            <Row className="justify-content-center">
                <Card>
                    <Card.Body>
                        <Card.Title>Home</Card.Title>
                        <Card.Text>
                            This is the home page. It will display the latest data from the devices.
                        </Card.Text>
                        <NavigationalLinks />
                    </Card.Body>
                </Card>
            </Row>
        </Container>
    );
}

function NavigationalLinks() {
    return (
        <Stack gap={2}>
            <NavLink href="/devices"><Button variant="primary">See available devices</Button></NavLink>
            <NavLink href="/ph"><Button variant="primary">PH Graph</Button></NavLink>
            <NavLink href="/temperature"><Button variant="primary">Temperature Graph</Button></NavLink>
        </Stack>
    )
}

export default Home;