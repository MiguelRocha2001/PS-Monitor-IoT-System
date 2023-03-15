import React from "react";
import {BrowserRouter, useLocation, Route, Routes} from 'react-router-dom'
import Ph from "./views/Ph";
import Home from "./views/Home";
import Temp from "./views/Temp";
import NewDevice from "./views/NewDevice";
import {Container, NavLink, Row, Stack} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {Devices} from "./views/Devices";
import {StillInProgressAlert} from "./views/StillInProgressAlert";
import {MyNavLink} from "./views/Commons";

function App() {
    return (
        <Container style={{width: '50%', margin: 'auto', marginTop: '30px'}}>
            <Stack gap={3}>
                <Row>
                    <StillInProgressAlert />
                </Row>
                <Row className="justify-content-center">
                    <BrowserRouter >
                        <Stack gap={4}>
                            <Routes>
                                <Route path='/' element={<Home />} />
                                <Route path='/devices' element={<Devices />} />
                                <Route path='/add-new-device' element={<NewDevice />} />
                                <Route path='/ph' element={<Ph />} />
                                <Route path='/temperature' element={<Temp />} />
                                <Route path='*' element={<div>404</div>} />
                            </Routes>
                            <HomeNavLink />
                        </Stack>
                    </BrowserRouter>
                </Row>
            </Stack>
        </Container>
    );
}

function HomeNavLink() {
    const location = useLocation();
    const homeButton = location.pathname !== '/' ? <MyNavLink text={"Home"} href={'/'} width={'30%'}/> : null;

    return (
        <Row className="justify-content-center">
            {homeButton}
        </Row>
    )
}

export default App;
