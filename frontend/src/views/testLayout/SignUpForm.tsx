import React, { useState } from "react";
import "./SignUpForm.css"
import { Link } from 'react-router-dom';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGoogle} from "@fortawesome/free-brands-svg-icons";
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import CodeInput from "./CodeInput";
import CodeEmailVerification from "./CodeEmailVerification";
import {createUser} from "../auth/IoTServerAuthentication";
import {Logger} from "tslog";
import {services} from "../../services/services";

const logger = new Logger({ name: "Authentication" });

/*show the password in plain text
*
*                     <button type="button" onClick={()=>setIsPasswordVisible(!isPasswordVisible)}> icon = {isPasswordVisible ? <FontAwesomeIcon icon={faEyeSlash} /> : <FontAwesomeIcon icon={faEye} />}
                    </button>
* */

function SignUpForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [sendCodeToEmail, setSendCodeToEmail] = useState(false);
    const [isBadInputEmail, setIsBadInputEmail] = useState(false);
    const [hasMinChars, setHasMinChars] = useState(false);
    const [hasDigit, setHasDigit] = useState(false);
    const [hasUpperCase, setHasUpperCase] = useState(false);
    const [isBadInputPassword, setIsBadInputPassword] = useState(false);
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);


    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if(!isValidEmail(email)){
            setIsBadInputEmail(true)
            setErrorMessage("Invalid email")
            return;
        }

        if (!isValidPassword(password)) {
            setIsBadInputPassword(true)
            setErrorMessage("Password does not meet requirements")
            return;
        }
        services.checkIfUserExists(email)
            .then((result) => {
                if (!result) {
                    setErrorMessage('');
                    setSendCodeToEmail(true);
                } else {
                    setErrorMessage('Email already exists');
                }

            })
    }
    return (
        sendCodeToEmail ? <CodeEmailVerification email={email} password={password}/> :
            <div className="signup-form">
                <h2>Sign Up</h2>
                <p>Please fill out the following information to create an account:</p>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="email">Email:</label>
                    <input className={isBadInputEmail ? "bad-input" : ""} type="text" id="email" value={email}
                           onChange={(e) => setEmail(e.target.value)}/>
                    <label htmlFor="password">Password:</label>

                    <input
                        className={isBadInputPassword ? "bad-input" : ""}
                        type={isPasswordVisible ? "text" : "password"}
                        value={password}
                        onChange={(e) =>{
                            inputValidation(e.target.value, setHasMinChars, setHasDigit, setHasUpperCase);
                            setPassword(e.target.value)
                        }}
                        required
                    />
                    <div className="password-description">
                        <div className={hasMinChars ? "password-requirement met" : "password-requirement"}>
                            At least 8 characters
                        </div>
                        <div className={hasDigit ? "password-requirement met" : "password-requirement"}>
                            At least one digit
                        </div>
                        <div className={hasUpperCase ? "password-requirement met" : "password-requirement"}>
                            At least one uppercase letter
                        </div>
                    </div>
                    <p id="error-message">{errorMessage}</p>
                    <button type="submit">Sign Up</button>
                    <button className="google-button" onClick={() => console.log('Google login clicked')}>
                        <FontAwesomeIcon icon={faGoogle} className="google-icon"/>
                        Sign up with Google
                    </button>
                </form>
                <p>Already have an account? <Link to="/auth/login">Sign in here</Link>.</p>
            </div>
    );
}
function isValidPassword(password:string) {
    const passwordRegex = /^(?=.*\d)(?=.*[A-Z]).{8,}$/;
    return passwordRegex.test(password);
}

function inputValidation(value: string, setHasMinChars: React.Dispatch<React.SetStateAction<boolean>>, setHasDigit: React.Dispatch<React.SetStateAction<boolean>>, setHasUpperCase: React.Dispatch<React.SetStateAction<boolean>>) {
    if(value.length >= 8) {
        setHasMinChars(true)
    } else {
        setHasMinChars(false)
    }
    if(value.match(/[A-Z]/)) {
        setHasUpperCase(true)
    } else {
        setHasUpperCase(false)
    }
    if(value.match(/[0-9]/)) {
        setHasDigit(true)
    } else {
        setHasDigit(false)
    }
}

function isValidEmail(email:string) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

export default SignUpForm;