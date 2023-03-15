import React from "react";
import {BrowserRouter, Outlet, Route, Routes} from 'react-router-dom'
import PhView from "./views/ph-view";
import Home from "./views/home-view";
import TempView from "./views/temp-view";
import AddNewDevice from "./views/add-new-device";
import {Container, NavLink, Row, Stack} from "react-bootstrap";
import Button from "react-bootstrap/Button";

function App() {
    return (
        <Container style={{width: '50%', margin: 'auto', marginTop: '30px'}}>
            <Stack gap={3}>
                <Row className="justify-content-center">
                    <BrowserRouter >
                        <Routes>
                            <Route path='/' element={<Home />} />
                            <Route path='/ph' element={<PhView />} />
                            <Route path='/temperature' element={<TempView />} />
                            <Route path='/add-new-device' element={<AddNewDevice />} />
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
