import * as React from 'react'
import {createContext, useContext, useEffect, useState} from 'react'
import {services} from "../../services/services";
import {User} from "../../services/domain";

type ContextType = {
    user: User | undefined,
    setUser: (v: User | undefined) => void
}
const LoggedInContext = createContext<ContextType>({
    user: undefined,
    setUser: () => { },
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | undefined>(undefined)

    useEffect( () => {
        async function fetchUser () {
            const isLogged = await services.isLoggedIn()
            if (isLogged) {
                console.log("User is logged in, fetching user information.")
                const user = await services.getMe()
                setUser(user)
            }
        }
        fetchUser()
    }, [])

    return (
        <LoggedInContext.Provider value={{ user: user, setUser: setUser }}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useCurrentUser() {
    return useContext(LoggedInContext).user
}

export function useSetUser() {
    return useContext(LoggedInContext).setUser
}
