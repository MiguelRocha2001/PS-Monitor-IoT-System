import {useLocation, useNavigate} from "react-router-dom";
import React, {useEffect, useMemo, useRef} from "react";
import Button from "react-bootstrap/Button";
import {MyCard} from "../Commons";

const LoginView = () => {
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

    const handleLoginClick = async () => {
        window.location.href = "http://localhost:9000/oidc-principal"
    }

    return (
        <MyCard title={'Google Authentication'} >

            <Button
                variant="contained"
                onClick={handleLoginClick}
            >
                Sign In
            </Button>
        </MyCard>
    )
};

export default LoginView;