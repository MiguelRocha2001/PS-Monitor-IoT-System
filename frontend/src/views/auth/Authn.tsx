import * as React from 'react'
import {createContext, useContext, useEffect, useState} from 'react'
import {services} from "../../services/services";
import {User} from "../../services/domain";


type ContextType = {
    logged: boolean | undefined
    setLogged: (logged: boolean) => void
}
const LoggedInContext = createContext<ContextType>({
    logged: false,
    setLogged: (logged: boolean ) => { }
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [logged, setLogged] = useState<boolean | undefined>(undefined)

    useEffect( () => {
        async function fetchUser () {
            const isLogged = await services.isLoggedIn()
            if (isLogged) {
                console.log("User is logged in")
                setLogged(true)
            } else {
                console.log("User is not logged in")
                setLogged(false)
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

export function useIsLoggedIn() {
    return useContext(LoggedInContext).logged
}

export function useSetIsLoggedIn() {
    return useContext(LoggedInContext).setLogged
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
