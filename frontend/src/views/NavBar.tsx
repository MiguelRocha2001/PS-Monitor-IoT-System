import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import React, {useEffect} from "react";
import {MyLink} from "./Commons";
import {useIsLoggedIn, useSetIsLoggedIn} from "./auth/Authn";
import {services} from "../services/services";
import Button from "react-bootstrap/Button";
import {useSetError} from "./error/ErrorContainer";

function NavBar() {
    const setError = useSetError()
    const setIsLoggedIn = useSetIsLoggedIn()
    const isLoggedIn = useIsLoggedIn()

    const logout =
        isLoggedIn ?
            (<Button variant="outline-primary" onClick={async () => {
                await services.logout()
                    .then(() => setIsLoggedIn(false))
                    .catch(error => setError(error))
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