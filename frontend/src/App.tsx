import React, {useEffect, useReducer} from "react";
import {Route, Routes} from 'react-router-dom'
//import NewDevice from "./views/device/NewDevice";
import {Container} from "react-bootstrap";
//import {Devices} from "./views/device/Devices";
import {services} from "./services/services";
import {SomethingWentWrong} from "./views/SomethingWentWrong";
import {Logger} from "tslog";
import {Loading} from "./views/Loading";
//import {DeviceInfo} from "./views/device/DeviceInfo";
import {DeviceCreated} from "./views/testLayout/deviceWasCreated";
import {AuthnContainer} from "./views/auth/Authn";
import {RequireAuthn} from "./views/auth/RequireAuthn";
import {ErrorContainer} from "./views/error/ErrorContainer";
import FrontPage from "./views/testLayout/FrontPage";
import SignUpForm from "./views/testLayout/SignUpForm";
import SignInForm from "./views/testLayout/SignInForm";
import NotFound from "./views/testLayout/404";
import {Devices} from "./views/testLayout/DevicesPage";
import {DeviceInfo} from "./views/testLayout/DeviceInformation";
import NewDevice from "./views/testLayout/AddNewDevice";
import {Users} from "./views/testLayout/UsersPage";
import {Home} from "./views/testLayout/Home";
import {DeviceLogs} from "./views/testLayout/DeviceLogs";
import {ErrorController} from "./views/error/ErrorController";
import {RequireAdminAndNotSelf} from "./views/auth/RequireAdminAndNotSelf";

//import './App.css';

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
    switch(action.type) {
        case "setSirenInfoFetched" : return {type: "sirenInfoFetched"}
        case "setSirenInfoFetchFailed" : return {type: "sirenInfoFetchFailed"}
    }
}

export function App() {
    const [state, dispatcher] = useReducer(reducer, {type : "fetchingSirenInfo"})

    useEffect(() => {
        // Ensures that the Services module extracts all available Siren information, from the backend.
        services.getBackendApiInfo().then(() => {
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
            <ErrorContainer>
                <AuthnContainer>
                    <Container>
                        <Routes>
                            <Route path='/' element={
                                <ErrorController>
                                    <FrontPage />
                                </ErrorController>}
                            />
                            <Route path='/auth/register' element={
                                <ErrorController>
                                    <SignUpForm />
                                </ErrorController>}
                            />
                            <Route path='/auth/login' element={
                                <ErrorController>
                                    <SignInForm />
                                </ErrorController>}
                            />
                            <Route path='/home' element={
                                <RequireAuthn
                                    children={
                                    <ErrorController>
                                        <Home />
                                    </ErrorController>}
                                />}
                            />
                            <Route path='/users' element={
                                <ErrorController>
                                    <RequireAuthn children={
                                        <Users />
                                    }/>
                                </ErrorController>}
                            />
                            <Route path='/users/:userId/devices' element={
                                <ErrorController>
                                    <RequireAuthn children={
                                        <RequireAdminAndNotSelf children={
                                            <Devices />
                                        }/>
                                    }/>
                                </ErrorController>}
                            />
                            <Route path='/users/:userId/devices/:deviceId' element={
                                <ErrorController>
                                    <RequireAuthn children={
                                        <DeviceInfo />
                                    }/>
                                </ErrorController>}
                            />
                            <Route path='/users/:userId/devices/:deviceId/logs' element={
                                <ErrorController>
                                    <RequireAuthn children={
                                        <DeviceLogs />
                                    }/>
                                </ErrorController>
                            }
                            />
                            <Route path='/add-new-device' element={
                                <ErrorController>
                                    <RequireAuthn children={
                                        <NewDevice />} />
                                </ErrorController>}
                            />
                            <Route path='/device-created/:deviceId' element={
                                <ErrorController>
                                    <RequireAuthn
                                        children={<DeviceCreated />} />
                                </ErrorController>}
                            />
                            <Route path='*' element={<NotFound/>}/>
                        </Routes>
                    </Container>
                </AuthnContainer>
            </ErrorContainer>
        </div>
    );
}