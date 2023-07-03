import React, {useEffect, useState} from "react";
import {Navigate} from 'react-router-dom';
import {Loading} from "../Loading";
import {useRole} from "../auth/Authn";

/**
 * Checks if user is admin.
 * If admin, show all users.
 * If not admin, show only the user's own devices.
 * @constructor
 */
export function Home() {
    const [redirect, setRedirect] = useState<string | undefined>(undefined)
    const role = useRole()

    console.log("role is " + role)

    useEffect(() => {
        if (role?.toLowerCase() === "admin") {
            setRedirect("/users")
        } else if (role?.toLowerCase() === "client") {
            setRedirect("/users/self/devices") // user id is 'self'
        }
    }, [role])

    if(redirect)
        return <Navigate to={redirect} replace={true}/>
    else
        return <Loading/>
}