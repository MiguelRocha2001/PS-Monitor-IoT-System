import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import React from "react";
import {MyLink} from "./Commons";
import {useCurrentUser} from "./auth/Authn";
import {services} from "../services/services";
import Button from "react-bootstrap/Button";

function NavBar() {
    const currentUser = useCurrentUser()

    const logout =
        currentUser ?
            (<Button variant="outline-primary" onClick={async () => {
                await services.logout()
            }}>LOGOUT</Button> ) : <></>

    return (
        <Navbar bg="light" expand="lg" style={{marginTop: '1em', marginBottom: '2em'}}>
            <Container>
                <Navbar.Brand href="/">Industrial IoT Solutions</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <MyLink text={'Devices'} to="/devices" center={true} margin={'1em'}/>
                        {logout}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavBar;