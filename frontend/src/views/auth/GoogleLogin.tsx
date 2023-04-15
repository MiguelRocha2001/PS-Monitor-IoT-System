import {useLocation, useNavigate} from "react-router-dom";
import React, {useEffect, useMemo, useRef} from "react";
import {useAuth} from "../../auth/auth";
import Button from "react-bootstrap/Button";
import {Alert} from "react-bootstrap";
import {MyCard} from "../Commons";
import {services} from "../../services/services";
import {authConfig} from "../../config";

const LoginView = () => {
    const {search} = useLocation();
    const navigate = useNavigate();
    const {progress, signInError, isAuthenticated, handleSignIn, signIn} = useAuth();
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
        signIn(authCode).finally(() => signInRef.current = false);
    }, [authCode]);

    useEffect(() => {
        if (isAuthenticated) {
            navigate("/");
        }
    }, [isAuthenticated]);

    const handleLoginClick = async () => {
        window.location.href = "http://localhost:9000/oidc-principal"
    }

    return (
        <MyCard title={'Google Authentication'} >
            {progress && (
                <Alert variant="info">
                    Signing in...
                </Alert>
            )}

            {!!signInError && (
                <Alert>
                    {signInError}
                </Alert>
            )}

            <Button
                variant="contained"
                onClick={handleLoginClick}
                disabled={progress}
            >
                Sign In
            </Button>
        </MyCard>
    )
};

export default LoginView;