import * as React from 'react'
import {createContext, useContext, useEffect, useState} from 'react'
import {services} from "../../services/services";
import {Logger} from "tslog";

const logger = new Logger({ name: "Authn" });
type ContextType = {
    logged: boolean | undefined
    setLogged: (logged: boolean) => void
    role: string | undefined
}
const LoggedInContext = createContext<ContextType>({
    logged: false,
    setLogged: (logged: boolean ) => { },
    role: undefined
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [logged, setLogged] = useState<boolean | undefined>(undefined)
    const [role, setRole] = useState<string | undefined>(undefined)

    useEffect( () => {
        async function fetchUser () {
            const isLogged = await services.isLoggedIn()
            if (isLogged) {
                logger.debug("User is logged in")
                setLogged(true)
            } else {
                logger.debug("User is not logged in")
                setLogged(false)
            }
        }
        fetchUser()
    }, [])

    useEffect( () => {
        async function fetchRole () {
            const me = await services.getMe()
            if (me) {
                setRole(me.role.toLowerCase())
            } else {
                setRole(undefined)
            }
        }
        if (logged)
            fetchRole()
        else {
            setRole(undefined)
        }
    }, [logged])

    return (
        <LoggedInContext.Provider value={{ logged: logged, setLogged: setLogged, role: role }}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useIsLoggedIn() {
    return useContext(LoggedInContext).logged
}

export function useSetIsLoggedIn() {
    return useContext(LoggedInContext).setLogged
}

export function useRole() {
    return useContext(LoggedInContext).role
}

/* usando local storage
export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [logged, setLogged] = useState<boolean | undefined>(undefined);

    useEffect(() => {
        async function fetchUser() {
            const isLogged = await services.isLoggedIn();
            if (isLogged) {
                console.log("User is logged in");
                setLogged(true);
                localStorage.setItem("loggedIn", "true");
            } else {
                console.log("User is not logged in");
                setLogged(false);
                localStorage.setItem("loggedIn", "false");
            }
        }
        fetchUser();
    }, []);

    return <>{children}</>;
}

export function useIsLoggedIn() {
    const loggedIn = localStorage.getItem("loggedIn");
    return loggedIn === "true";
}

export function useSetIsLoggedIn() {
    const setLoggedIn = (loggedIn: boolean) => {
        localStorage.setItem("loggedIn", loggedIn ? "true" : "false");
    };
    return setLoggedIn;
}*/
