import React, {useEffect} from "react";
import {Route, Routes, useNavigate} from 'react-router-dom'
import Home from "./views/Home";
import NewDevice from "./views/NewDevice";
import {Col, Container} from "react-bootstrap";
import {Devices} from "./views/Devices";
import {StillInProgressAlert} from "./views/StillInProgressAlert";
import NavBar from "./views/NavBar";
import {services} from "./services/services";
import {SomethingWentWrong} from "./views/SomethingWentWrong";
import {useAuth} from "./auth/auth";
import LoginView from "./views/auth/Login";
import {DeviceSensorialData} from "./views/DeviceData";

function App() {
    const navigate = useNavigate();
    const {isAuthenticated} = useAuth();
    const [componentToDisplay, setComponentToDisplay] = React.useState<JSX.Element>(<StillInProgressAlert></StillInProgressAlert>);

    useEffect(() => {
        if (!isAuthenticated) {
            navigate("/login");
        }
    }, [isAuthenticated]);

    useEffect(() => {
        if (isAuthenticated) {
            // Ensures that the Services module extracts all available Siren information, from the backend.
            services.getBackendSirenInfo().then(() => {
                console.log("Siren information extracted from the backend.")
                setComponentToDisplay(getRouterComponent());
            }).catch((error) => {
                const errorToLogAndDisplay = "Error while extracting Siren information from the backend: " + error
                console.log(errorToLogAndDisplay)
                setComponentToDisplay(<SomethingWentWrong details={errorToLogAndDisplay}/>);
            });
        }
    }, [isAuthenticated]);

    return componentToDisplay
}

export default App;

function getRouterComponent() {
    return (
        <div>
            <NavBar/>
            <Col style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <StillInProgressAlert />
            </Col>
            <Container style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <Routes>
                    <Route path='/' element={<Home />} />
                    <Route path='/login' element={<LoginView />} />
                    <Route path='/devices' element={<Devices />} />
                    <Route path='/add-new-device' element={<NewDevice />} />
                    <Route path='/device-data' element={<DeviceSensorialData />} />
                    <Route path='*' element={<div>404</div>} />
                </Routes>
            </Container>
        </div>
    );
}