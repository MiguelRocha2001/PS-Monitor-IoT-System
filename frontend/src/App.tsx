import React, {useEffect} from "react";
import {Route, Routes, useNavigate} from 'react-router-dom'
import Home from "./views/Home";
import NewDevice from "./views/device/NewDevice";
import {Container} from "react-bootstrap";
import {Devices} from "./views/device/Devices";
import NavBar from "./views/NavBar";
import {services} from "./services/services";
import {SomethingWentWrong} from "./views/SomethingWentWrong";
import {useAuth} from "./auth/auth";
import LoginView from "./views/auth/Login";
import {DeviceSensorialData} from "./views/device/DeviceData";
import {Logger} from "tslog";
import {Loading} from "./views/Loading";
import {DeviceInfo} from "./views/device/DeviceInfo";

const logger = new Logger({ name: "App" });

function App() {
    const navigate = useNavigate();
    const {isAuthenticated} = useAuth();
    const [componentToDisplay, setComponentToDisplay] = React.useState<JSX.Element>(<Loading></Loading>);

    useEffect(() => {
        if (!isAuthenticated) {
            navigate("/login");
        }
    }, [isAuthenticated]);

    useEffect(() => {
        if (isAuthenticated) {
            // Ensures that the Services module extracts all available Siren information, from the backend.
            services.getBackendSirenInfo().then(() => {
                logger.info("Siren information extracted from the backend.")
                setComponentToDisplay(getRouterComponent());
            }).catch((error) => {
                const errorToLogAndDisplay = "Error while extracting Siren information from the backend: " + error
                logger.error(errorToLogAndDisplay)
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
            <Container style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <Routes>
                    <Route path='/' element={<Home />} />
                    <Route path='/login' element={<LoginView />} />
                    <Route path='/devices' element={<Devices />} />
                    <Route path={'/devices/:deviceId'} element={<DeviceInfo />} />
                    <Route path='/add-new-device' element={<NewDevice />} />
                    <Route path='/device-data/:deviceId' element={<DeviceSensorialData />} />
                    <Route path='*' element={<p>404</p>} />
                </Routes>
            </Container>
        </div>
    );
}