import {createContext, useContext, useEffect, useState} from "react";
import {authConfig} from "../config";
import axios from "axios";

export class TokenResponse {
    constructor(
        public access_token: string,
        public id_token: string,
        public scope: string
    ) {}
}

const AuthContext = createContext({});

export const AuthProvider = ({children} : {children: any}) => { // TODO: change param children type
    // const {accessToken, setAccessToken, removeAccessToken} = useAccessToken();
    const [idToken, setIdToken] = useState<string | undefined>(undefined);
    const [isAuthenticated, setAuthenticated] = useState(false);
    const [tokenFetching, setTokenFetching] = useState(false);
    const [tokenError, setTokenError]: [string | undefined, any] = useState();

    const handleSignIn = () => {
        let redirectUrl = new URL(authConfig.authorizeUrl);
        redirectUrl.searchParams.set("scope", authConfig.scope);
        redirectUrl.searchParams.set("redirect_uri", authConfig.redirectUrl);
        redirectUrl.searchParams.set("client_id", authConfig.clientId);
        redirectUrl.searchParams.set("response_type", "code");
        window.location.href = redirectUrl.toString();
    };

    const signIn = async (authCode: string) => {
        setTokenFetching(true);
        setTokenError(null);
        let params = new URLSearchParams();
        params.set("client_id", authConfig.clientId);
        params.set("client_secret", authConfig.clientSecret);
        params.set("code", authCode);
        params.set("grant_type", "authorization_code");
        params.set("redirect_uri", authConfig.redirectUrl);
        try {
            let response = await axios.post(
                authConfig.tokenUrl,
                params.toString(),
                {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    }
                }
            );
            let tokenResponse: TokenResponse = response.data;
            // setAccessToken(tokenResponse.access_token);

            // console.log(tokenResponse.id_token);
            console.log(tokenResponse.access_token)

            setIdToken(tokenResponse.id_token);
            // TODO Validate Token
            setAuthenticated(true);
        } catch (e: any) {
            console.log("Failed to fetch token", e.response?.data);
            setTokenError("Failed to obtain a token with provided auth code");
        } finally {
            setTokenFetching(false);
        }
    };

    const signOut = () => {
        // removeAccessToken();
        setIdToken(undefined);
        setAuthenticated(false);
    }

    useEffect(() => {
        if (idToken) {
            setAuthenticated(true);
        }
    }, []);

    return (
        <AuthContext.Provider
            value={{
                idToken: idToken,
                isAuthenticated,
                progress: tokenFetching,
                signInError: tokenError,
                handleSignIn,
                signIn,
                signOut
            }}
        >
            {children}
        </AuthContext.Provider>
    )
};

class AuthContextType {
    constructor(
        public idToken: string,
        public isAuthenticated: boolean,
        public progress: boolean,
        public signInError: string | null,
        public handleSignIn: () => void,
        public signIn: (authCode: string) => Promise<void>,
        public signOut: () => void
    ) {}
}

export const useAuth = (): AuthContextType => useContext(AuthContext) as AuthContextType;

export const useAccessToken = () => {
    const [accessToken, setAccessToken] = useState(localStorage.getItem("access_token"));

    const saveAccessToken = (token: string) => {
        localStorage.setItem("access_token", token);
        setAccessToken(token);
    }

    const removeAccessToken = () => {
        localStorage.removeItem("access_token");
        setAccessToken(null);
    }

    return {
        accessToken: accessToken,
        setAccessToken: saveAccessToken,
        removeAccessToken: removeAccessToken
    };
};

export const useIdToken = () => {
    const [idToken, setIdToken] = useState(localStorage.getItem("id_token"));

    const saveIdToken = (token: string) => {
        localStorage.setItem("id_token", token);
        setIdToken(token);
    }

    const removeIdToken = () => {
        localStorage.removeItem("id_token");
        setIdToken(null);
    }

    return {
        idToken: idToken,
        setIdToken: saveIdToken,
        removeIdToken: removeIdToken
    };
};
