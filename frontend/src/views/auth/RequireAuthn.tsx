import * as React from 'react'
import {Navigate, useLocation} from 'react-router-dom'
import {useIsLoggedIn} from './Authn'
import {Logger} from "tslog";

const logger = new Logger({ name: "RequireAuthn" });

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    const isLoggedIn = useIsLoggedIn()
    const location = useLocation()

    if (isLoggedIn === undefined) {
        logger.debug("Waiting for authentication information")
        return <></>
    } else if (isLoggedIn) {
        logger.debug("User is logged in")
        return <>{children}</>
    } else {
        logger.debug("User is not logged in")
        return <Navigate to="/auth/login" state={{source: location.pathname}} replace={true}/>
    }
}