import * as React from 'react'
import {Navigate, useLocation} from 'react-router-dom'
import {useCurrentUser} from './Authn'
import {Logger} from "tslog";

const logger = new Logger({ name: "RequireAuthn" });

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    const currentUser = useCurrentUser()
    const location = useLocation()

    if (currentUser) {
        return <>{children}</>
    } else {
        logger.debug("Redirecting to login page")
        return <Navigate to="/sign-in" state={{source: location.pathname}} replace={true}/>
    }
}