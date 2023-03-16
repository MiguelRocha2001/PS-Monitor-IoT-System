import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import React from "react";
import {MyLink} from "./Commons";

// TODO: Use router links instead of <Nav.Link href="...">...</Nav.Link> (line 18)

function NavBar() {
    return (
        <Navbar bg="light" expand="lg" style={{marginTop: '1em', marginBottom: '2em'}}>
            <Container>
                <Navbar.Brand href="/">Industrial IoT Solutions</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link href="devices">Devices</Nav.Link>
                        <NavDropdown title="Data" id="basic-nav-dropdown">
                            <NavDropdown.Item>
                                <MyLink text={'pH'} to="/ph" />
                            </NavDropdown.Item>
                            <NavDropdown.Item>
                                <MyLink text={'Temperature'} to="/temperature" />
                            </NavDropdown.Item>
                        </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavBar;