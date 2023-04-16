import * as React from 'react'
import {Navigate, useLocation} from 'react-router-dom'
import {useIsLoggedIn} from './Authn'
import {Logger} from "tslog";
import {Loading} from "../Loading";

const logger = new Logger({ name: "RequireAuthn" });

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    const isLoggedIn = useIsLoggedIn()
    const location = useLocation()

    if (isLoggedIn === undefined) {
        return <></>
    } else if (isLoggedIn) {
        return <>{children}</>
    } else {
        logger.debug("Redirecting to login page")
        return <Navigate to="/auth/login" state={{source: location.pathname}} replace={true}/>
    }
}