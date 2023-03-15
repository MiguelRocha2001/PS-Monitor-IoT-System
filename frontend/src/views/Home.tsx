import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {Alert, Container, NavLink, Row, Stack} from "react-bootstrap";
import React from "react";
import {StillInProgressAlert} from "./StillInProgressAlert";
import {MyNavLink} from "./Commons";

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
    const navLinkWidth = 'auto';
    return (
        <Stack gap={2}>
            <MyNavLink text={'See available devices'} href={'/devices'} width={navLinkWidth}/>
            <MyNavLink text={'PH'} href={'/ph'} width={navLinkWidth}/>
            <MyNavLink text={'Temperature'} href={'/temperature'} width={navLinkWidth}/>
        </Stack>
    )
}

export default Home;