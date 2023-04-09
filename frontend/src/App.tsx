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
import {Logger} from "tslog";
import {Loading} from "./views/Loading";
import {DeviceInfo} from "./views/device/DeviceInfo";
import {Authentication} from "./views/auth/Authentication";
import {DeviceSensorialData} from "./views/device/DeviceData";
import {DeviceCreated} from "./views/device/DeviceCreated";

const logger = new Logger({ name: "App" });

function App() {
    const navigate = useNavigate();
    const {isAuthenticated} = useAuth();
    const [componentToDisplay, setComponentToDisplay]
        = React.useState<JSX.Element>(<Loading></Loading>);

    useEffect(() => {
        if (!isAuthenticated) {
            logger.info("User is not authenticated, redirecting to login page.")
            navigate("/auth/login");
        } else {
            logger.info("User is authenticated, redirecting to home page.")
            navigate("/");
        }
    }, [isAuthenticated]);

    useEffect(() => {
        // Ensures that the Services module extracts all available Siren information, from the backend.
        services.getBackendSirenInfo().then(() => {
            logger.info("Siren information extracted from the backend.")
            setComponentToDisplay(getRouterComponent());
        }).catch((error) => {
            const errorToLogAndDisplay = "Error while extracting Siren information from the backend: " + error
            logger.error(errorToLogAndDisplay)
            setComponentToDisplay(<SomethingWentWrong details={errorToLogAndDisplay}/>);
        });
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
                    <Route path='/auth/:action' element={<Authentication />} />
                    <Route path='/devices' element={<Devices />} />
                    <Route path={'/devices/:deviceId'} element={<DeviceInfo />} />
                    <Route path='/add-new-device' element={<NewDevice />} />
                    <Route path='/device-data/:deviceId' element={<DeviceSensorialData />} />
                    <Route path='/device-created/:deviceId' element={<DeviceCreated />} />
                    <Route path='*' element={<p>404</p>} />
                </Routes>
            </Container>
        </div>
    );
}