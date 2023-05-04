import React, { useState } from "react";
import "./SignUpForm.css"
import { Link } from 'react-router-dom';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGoogle} from "@fortawesome/free-brands-svg-icons";
import CodeInput from "./CodeInput";
import CodeEmailVerification from "./CodeEmailVerification";

function SignUpForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('')
    const [errorMessage, setErrorMessage] = useState('');
    const [sendCodeToEmail, setSendCodeToEmail] = useState(false);


    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if(password.length < 8){
            setErrorMessage('Password must be at least 8 characters') //teste estupido para ver se funciona
            return;
        }
        if(password !== repeatPassword){
            setErrorMessage('Passwords do not match')
            return;
        }
        setErrorMessage('');
        setSendCodeToEmail(true)
        // handle sign-in logic here
        //check if email is unique
    };

    return (
        sendCodeToEmail ? <CodeEmailVerification email={email} /> :
            <div className="signup-form">
                <h2>Sign Up</h2>
                <p>Please fill out the following information to create an account:</p>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="email">Email:</label>
                    <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        minLength={8}
                        pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,}"
                        required
                    />
                    <label htmlFor="repeat-password">Repeat Password:</label>
                    <input
                        type="password"
                        value={repeatPassword}
                        onChange={(e) => setRepeatPassword(e.target.value)}
                        required
                    />
                    <p className="password-description">
                        Password must contain at least 8 characters including at least one digit, one uppercase letter, one lowercase letter, and one special character.
                    </p>
                    <p id="error-message">{errorMessage}</p>
                    <button type="submit">Sign Up</button>
                    <button className="google-button" onClick={() => console.log('Google login clicked')}>
                        <FontAwesomeIcon icon={faGoogle} className="google-icon" />
                        Sign up with Google
                    </button>
                </form>
                <p>Already have an account? <Link to="/auth/login">Sign in here</Link>.</p>
            </div>
    );
}

export default SignUpForm;