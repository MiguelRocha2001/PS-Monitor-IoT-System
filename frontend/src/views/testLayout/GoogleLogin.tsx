import {useLocation, useNavigate,Navigator} from "react-router-dom";
import React, {useEffect, useMemo, useRef} from "react";
import Button from "react-bootstrap/Button";
import {MyCard} from "../Commons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGoogle} from "@fortawesome/free-brands-svg-icons";



interface GoogleLoginButtonProps {
    text: string;
}

export const GoogleLoginButton = ({text}: GoogleLoginButtonProps) => {
    const {search} = useLocation();
    const navigate = useNavigate();
    // const {progress, signInError, isAuthenticated, handleSignIn, signIn} = useAuth();
    const signInRef = useRef(false);

    const authCode = useMemo(() => {
        let searchParams = new URLSearchParams(search);
        return searchParams.get("code");
    }, []);

    useEffect(() => {
        if (!authCode) {
            return;
        }

        // A ref is used to prevent calling signin twice due to subsequent mounting in strict mode
        if (signInRef.current) {
            return;
        }
        signInRef.current = true;
        // signIn(authCode).finally(() => signInRef.current = false);
    }, [authCode]);

    /*
    useEffect(() => {
        if (isAuthenticated) {
            navigate("/");
        }
    }, [isAuthenticated]);
     */

    const handleLoginClick = () => {
        window.location.href = "http://localhost:9000/oidc-principal"
    }

    function handleLoginClick2 (){
        const navigate = useNavigate();
        navigate("http://localhost:9000/oidc-principal")
    }

    return (
        <button className="google-button" onClick={handleLoginClick}>
            <FontAwesomeIcon icon={faGoogle} className="google-icon" />
            {text} with Google
        </button>
    )
};

