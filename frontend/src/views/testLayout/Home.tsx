import React, {useEffect, useState} from "react";
import {Navigate} from 'react-router-dom';
import {services} from "../../services/services";
import {Loading} from "../Loading";

/**
 * Checks if user is admin.
 * If admin, show all users.
 * If not admin, show only the user's own devices.
 * @constructor
 */
export function Home() {
    const [redirect, setRedirect] = useState<string | undefined>(undefined)

    useEffect(() => {
        async function getMe() {
            const me = await services.getMe()
            if (me.role === "admin") {
                setRedirect("/users")
            } else {
                setRedirect("/users/my/devices") // user id is 'my'
            }
        }
        getMe()
    }, [])

    if(redirect)
        return <Navigate to={redirect} replace={true}/>
    else
        return <Loading/>
}