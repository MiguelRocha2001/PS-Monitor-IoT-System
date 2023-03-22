import React, {useEffect} from "react";
import {BrowserRouter, Outlet, Route, Routes} from 'react-router-dom'
import Ph from "./views/Ph";
import Home from "./views/Home";
import Temperature from "./views/Temperature";
import NewDevice from "./views/NewDevice";
import {Col, Container} from "react-bootstrap";
import {Devices} from "./views/Devices";
import {StillInProgressAlert} from "./views/StillInProgressAlert";
import NavBar from "./views/NavBar";
import {AuthnContainer} from "./views/auth/Authn";
import {RequireAuthn} from "./views/auth/RequireAuthn";
import {Authentication} from "./views/auth/Authentication";
import {services} from "./services/services";
import {SomethingWentWrong} from "./views/SomethingWentWrong";

function App() {
    const [componentToDisplay, setComponentToDisplay]
        = React.useState<React.ReactElement>(<div>Loading...</div>);

    useEffect(() => {
        // Ensures that the Services module extracts all available Siren information, from the backend.
        services.getBackendSirenInfo().then(() => {
            console.log("Siren information extracted from the backend.")
            setComponentToDisplay(getRouterComponent())
        }).catch((error) => {
            const errorToLogAndDisplay = "Error while extracting Siren information from the backend: " + error
            console.log(errorToLogAndDisplay)
            const errorComponent = <SomethingWentWrong details={errorToLogAndDisplay}/>;
            setComponentToDisplay(errorComponent)
        });
    }, []);

    return (componentToDisplay);
}

export default App;

function getRouterComponent() {
    return (
        <AuthnContainer><Outlet />
            <BrowserRouter >
                <NavBar/>
                <Col style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                    <StillInProgressAlert />
                </Col>
                <Container style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                    <Routes>
                        <Route path='/' element={<Home />} />
                        <Route path='/sign-in' element={<Authentication title={'Sign in'} action={'login'}/>} />
                        <Route path='/sign-up' element={<Authentication title={'Sign Up'} action={'register'}/>} />
                        <Route path='/devices' element={<RequireAuthn children={<Devices />} />} />
                        <Route path='/add-new-device' element={<RequireAuthn children={<NewDevice />} />} />
                        <Route path='/ph' element={<RequireAuthn children={<Ph />} />} />
                        <Route path='/temperature' element={<RequireAuthn children={<Temperature />} />} />
                        <Route path='*' element={<div>404</div>} />
                    </Routes>
                </Container>
            </BrowserRouter>
        </AuthnContainer>
    );
}