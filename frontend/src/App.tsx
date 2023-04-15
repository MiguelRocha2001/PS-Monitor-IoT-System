import React, {useEffect, useReducer} from "react";
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
import {RequireAuthn} from "./views/auth/RequireAuthn";
import {AuthnContainer} from "./views/auth/Authn";

const logger = new Logger({ name: "App" });

export type State =
    {
        type : 'fetchingSirenInfo',
    }
    |
    {
        type : "sirenInfoFetched",
    }
    |
    {
        type : "sirenInfoFetchFailed",
    }

type Action =
    {
        type : "setSirenInfoFetched",
    }
    |
    {
        type : "setSirenInfoFetchFailed",
    }

function reducer(state:State, action:Action): State {
    switch(action.type){
        case "setSirenInfoFetched" : return {type: "sirenInfoFetched"}
        case "setSirenInfoFetchFailed" : return {type: "sirenInfoFetchFailed"}
    }
}

export function App() {
    const [state, dispatcher] = useReducer(reducer, {type : "fetchingSirenInfo"})

    useEffect(() => {
        // Ensures that the Services module extracts all available Siren information, from the backend.
        services.getBackendSirenInfo().then(() => {
            logger.info("Siren information extracted from the backend.")
            dispatcher({type: "setSirenInfoFetched"})
        }).catch((error) => {
            const errorToLogAndDisplay = "Error while extracting Siren information from the backend: " + error
            logger.error(errorToLogAndDisplay)
            dispatcher({type: "setSirenInfoFetchFailed"})
        });
    }, []);

    if (state.type === "fetchingSirenInfo") {
        return <Loading />
    } else if (state.type === "sirenInfoFetchFailed") {
        return <SomethingWentWrong details={"Error while extracting Siren information from the backend."} />
    } else {
        return <Router />
    }
}

function Router() {
    return (
        <div>
            <NavBar/>
            <Container style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                <AuthnContainer>
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
                </AuthnContainer>
            </Container>
        </div>
    );
}