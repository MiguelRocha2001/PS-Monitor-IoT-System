import React from "react";
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Ph from "./views/Ph";
import Home from "./views/Home";
import Temp from "./views/Temp";
import NewDevice from "./views/NewDevice";
import {Col, Container} from "react-bootstrap";
import {Devices} from "./views/Devices";
import {StillInProgressAlert} from "./views/StillInProgressAlert";
import NavBar from "./views/NavBar";

function App() {
    return (
        <BrowserRouter >
            <NavBar/>
            <Col style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <StillInProgressAlert />
            </Col>
            <Container style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <Routes>
                    <Route path='/' element={<Home />} />
                    <Route path='/devices' element={<Devices />} />
                    <Route path='/add-new-device' element={<NewDevice />} />
                    <Route path='/ph' element={<Ph />} />
                    <Route path='/temperature' element={<Temp />} />
                    <Route path='*' element={<div>404</div>} />
                </Routes>
            </Container>
        </BrowserRouter>
    );
}

export default App;
