import React from "react";
import "./HomePageStyle.css";
import { useNavigate } from "react-router-dom";

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
        </div>
    );
}

export default FrontPage;