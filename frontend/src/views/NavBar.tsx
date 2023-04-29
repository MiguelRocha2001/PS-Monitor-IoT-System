import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import React, {useEffect} from "react";
import {MyLink} from "./Commons";
import {useIsLoggedIn, useSetIsLoggedIn} from "./auth/Authn";
import {services} from "../services/services";
import Button from "react-bootstrap/Button";
import {useSetError} from "./error/ErrorContainer";
import '../style/NavBarStyle.css'


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
        : <MyLink text={'Login/SignUp'} to="/auth/login" center={true} margin={'1em'}/>

    const navbarContent = isLoggedIn ? (
        <>
            <Navbar id="basic-navbar-nav">
                <Nav
                    className="me-auto my-2 my-lg-0"
                    style={{ maxHeight: '100px' }}
                    navbarScroll
                >
                    <Nav.Link>{devicesLink}</Nav.Link>
                    <Nav.Link>{authenticationLink}</Nav.Link>
                    <Nav.Link>{logout}</Nav.Link>
                </Nav>
            </Navbar>
        </>
    ) : (
        <MyLink text={'Login/SignUp'} to="/auth/login" center={true} margin={'1em'}/>
    );

    return (
        <Navbar expand="light" className={"primaryNavbar"}>
            <Container>
                <Navbar.Brand href="/">
                    <img src= {"./favicon.ico"} alt="logo"/>
                </Navbar.Brand>
                {navbarContent}
            </Container>
        </Navbar>
    );
}

export default NavBar;