import React, {useEffect, useState} from "react";
import "./HomePageStyle.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";
import {useIsLoggedIn, useRole} from "../auth/Authn";
import {Navigate} from 'react-router-dom';
import {Loading} from "../Loading";

function FrontPage() {
    const isLoggedIn = useIsLoggedIn()
    const [redirect, setRedirect] = useState<string | undefined>(undefined)

    const handleLoginClick = () => {
        setRedirect("/auth/login")
    };

    const handleSignUpClick = () => {
        setRedirect("/auth/register")
    };

    useEffect(() => {
        if (isLoggedIn === true) {
            setRedirect("/home")
        }
    }, [isLoggedIn])

    if (redirect) {
        return <Navigate to={redirect} replace={true}/>
    } else if (isLoggedIn === undefined) {
        return <Loading/>
    } else {
        return (
            <div className="homepage">
                <h1>Industrial IoT Solutions</h1>
                <p>We provide the best services for your needs.</p>
                <div className="button-container">
                    <button onClick={handleLoginClick} className="login-button">Log in</button>
                    <button onClick={handleSignUpClick} className="signup-button">Sign up</button>
                </div>
                <a href="https://github.com/MiguelRocha2001/PS-Monitor-IoT-System" target="_blank"
                   rel="noopener noreferrer">
                    <FontAwesomeIcon icon={faGithub} className="github-icon"/>
                </a>
            </div>
        );
    }
}

export default FrontPage;