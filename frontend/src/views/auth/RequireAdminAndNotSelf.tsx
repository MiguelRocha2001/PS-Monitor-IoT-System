import * as React from 'react'
import {Navigate, useLocation} from 'react-router-dom'
import {useIsLoggedIn, useRole} from './Authn'
import {Logger} from "tslog";
import {useNavigate, useParams} from 'react-router-dom';
import {useEffect} from "react";
import {useSetError} from "../error/ErrorContainer";
import {Loading} from "../Loading";

const logger = new Logger({ name: "RequireAuthn" });

export function RequireAdminAndNotSelf({ children }: { children: React.ReactNode }): React.ReactElement {
    const { userId } = useParams<string>()
    const location = useLocation()
    const role = useRole()
    const setError = useSetError()
    const isLoggedIn = useIsLoggedIn()

    useEffect(() => {
        if (role === "admin" && userId === "self") {
            console.log("Admins dont have devices")
            setError(new Error("Admins dont have devices"))
        }
    }, [role])

    if (role && !(role === "admin" && userId === "self")) {
        logger.debug("User is logged in")
        return <>{children}</>
    } else if (isLoggedIn === undefined) {
        logger.debug("User is not logged in")
        return <Navigate to="/auth/login" state={{source: location.pathname}} replace={true}/>
    } else {
        return <Loading/>
    }
}