import * as React from 'react'
import {createContext, useContext, useEffect, useState} from 'react'
import {services} from "../../services/services";
import {User} from "../../services/domain";

type ContextType = {
    logged: boolean,
    setLogged: (logged: boolean) => void
}
const LoggedInContext = createContext<ContextType>({
    logged: false,
    setLogged: (logged: boolean) => { }
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [logged, setLogged] = useState<boolean>(false)

    useEffect( () => {
        async function fetchUser () {
            const isLogged = await services.isLoggedIn()
            if (isLogged) {
                console.log("User is logged in already")
                setLogged(true)
            }
        }
        fetchUser()
    }, [])

    return (
        <LoggedInContext.Provider value={{ logged: logged, setLogged: setLogged }}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useCurrentUser() {
    return useContext(LoggedInContext).logged
}

export function useSetUser() {
    return useContext(LoggedInContext).setLogged
}
