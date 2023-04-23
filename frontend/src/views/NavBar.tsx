import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import React, {useEffect} from "react";
import {MyLink} from "./Commons";
import {useIsLoggedIn, useSetIsLoggedIn} from "./auth/Authn";
import {services} from "../services/services";
import Button from "react-bootstrap/Button";

function NavBar() {
    const isLoggedIn = useIsLoggedIn()
    const setIsLoggedIn = useSetIsLoggedIn()

    const logout =
        isLoggedIn ?
            (<Button variant="outline-primary" onClick={async () => {
                services.logout()
                    .catch(error => console.log(error))
                setIsLoggedIn(false)
            }}>LOGOUT</Button> ) : <></>

    const devicesLink = isLoggedIn ? <MyLink text={'Devices'} to="/devices" center={true} margin={'1em'}/> : <></>

    const authenticationLink = isLoggedIn ? <></>
        : <MyLink text={'Authentication'} to="/auth/login" center={true} margin={'1em'}/>

    return (
        <Navbar bg="light" expand="lg" style={{marginTop: '1em', marginBottom: '2em'}}>
            <Container>
                <Navbar.Brand href="/">Industrial IoT Solutions</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {devicesLink}
                        {authenticationLink}
                        {logout}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavBar;