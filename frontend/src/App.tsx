import React from "react";
import {BrowserRouter, Outlet, Route, Routes} from 'react-router-dom'
import Ph from "./views/Ph";
import Home from "./views/Home";
import Temp from "./views/Temp";
import NewDevice from "./views/NewDevice";
import {Container, NavLink, Row, Stack} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {Devices} from "./views/Devices";

function App() {
    return (
        <Container style={{width: '50%', margin: 'auto', marginTop: '30px'}}>
            <Stack gap={3}>
                <Row className="justify-content-center">
                    <BrowserRouter >
                        <Routes>
                            <Route path='/' element={<Home />} />
                            <Route path='/devices' element={<Devices />} />
                            <Route path='/add-new-device' element={<NewDevice />} />
                            <Route path='/ph' element={<Ph />} />
                            <Route path='/temperature' element={<Temp />} />
                            <Route path='*' element={<div>404</div>} />
                        </Routes>
                    </BrowserRouter>
                </Row>
                <Row className="justify-content-center">
                    <NavLink href="/" style={{width: 'min-content'}}><Button variant="primary">Home</Button></NavLink>
                </Row>
            </Stack>
        </Container>
    );
}

export default App;
