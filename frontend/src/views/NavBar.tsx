import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import React from "react";
import {MyLink} from "./Commons";
import {useCurrentUser, useSetUser} from "./auth/Authn";
import {services} from "../services/services";
import Button from "react-bootstrap/Button";

function NavBar() {
    const currentUser = useCurrentUser()
    const setUser = useSetUser()

    const logout =
        currentUser ?
            (<Button variant="outline-primary" onClick={async () => {
                setUser(undefined)
                await services.logout()
            }}>LOGOUT</Button> ) : <></>

    const signInUpDropdown =
        currentUser === undefined ?
            <NavDropdown title="Authentication" id="basic-nav-dropdown">
                <NavDropdown.Item>
                    <MyLink text={'Sign In'} to="/sign-in" />
                </NavDropdown.Item>
                <NavDropdown.Item>
                    <MyLink text={'Sign Up'} to="/sign-up" />
                </NavDropdown.Item>
            </NavDropdown> : <></>

    return (
        <Navbar bg="light" expand="lg" style={{marginTop: '1em', marginBottom: '2em'}}>
            <Container>
                <Navbar.Brand href="/">Industrial IoT Solutions</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <MyLink text={'Devices'} to="/devices" />

                        {signInUpDropdown}

                        <NavDropdown title="Data" id="basic-nav-dropdown">
                            <NavDropdown.Item>
                                <MyLink text={'pH'} to="/ph" />
                            </NavDropdown.Item>
                            <NavDropdown.Item>
                                <MyLink text={'Temperature'} to="/temperature" />
                            </NavDropdown.Item>
                        </NavDropdown>

                        {logout}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavBar;