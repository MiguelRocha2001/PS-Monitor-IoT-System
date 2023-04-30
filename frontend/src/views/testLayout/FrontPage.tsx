import React from "react";
import "./HomePageStyle.css";
import { useNavigate } from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";

function FrontPage() {

    const history = useNavigate();

    const handleLoginClick = () => {
        history('/auth/login');
    };

    const handleSignUpClick = () => {
        history('/auth/register');
    };

    return (
        <div className="homepage">
            <h1>Industrial IoT Solutions</h1>
            <p>We provide the best services for your needs.</p>
            <div className="button-container">
                <button onClick={handleLoginClick} className="login-button">Log in</button>
                <button onClick={handleSignUpClick} className="signup-button">Sign up</button>
            </div>
            <a href="https://github.com/MiguelRocha2001/PS-Monitor-IoT-System" target="_blank" rel="noopener noreferrer">
                <FontAwesomeIcon icon={faGithub} className="github-icon" />
            </a>
        </div>
    );
}

export default FrontPage;