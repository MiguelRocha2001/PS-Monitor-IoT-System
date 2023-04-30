import React, { useState } from 'react';
import axios from 'axios';
import './CodeEmailVerification.css';
import CodeInput from "./CodeInput";

type OwnerDetails = {
    email: string;
};

function CodeVerification({ email }: OwnerDetails) {
    const [isCodeIncorrect, setIsCodeIncorrect] = useState<boolean>(false);
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [isCodeSent, setIsCodeSent] = useState<string>('');

    function handleResendCodeClick() {
        // TODO: implement the logic for resending the code,the resent code needs to be different from the previous one
        setIsCodeSent("Code resent to email"+ email)
        console.log('Resending code...');
    }

    function onCodeSubmit(code: string) {
        if(code === '12345') { //TODO number of allowed tries 5, max duration token time is 30 minutes, if this happens change the error message to "Your code has expired, please request a new one"
            setIsCodeIncorrect(false);
            return true;
        }
        setIsCodeIncorrect(true);
        setErrorMessage('Incorrect code, please try again');
        setIsCodeSent('')
        return false;

      //  window.alert(code);
        /*
        axios.post('http://localhost:8080/api/v1/verify', {//api path to verify //todo
            code: code,
        }).then((response) => {
            console.log(response);
            setIsCodeIncorrect(false);
            return true;
        }).catch((error) => {
            console.log(error);
            setIsCodeIncorrect(true);
            return false;
        });
        return false;
         */
    }

    return (
        <div className="code-verification">
            <h2>A code was sent to your email</h2>
            <p>Please enter the 5-digit code to verify your account:</p>
            <p id="errorMessage" >{errorMessage}</p>
            <CodeInput onCodeSubmit={onCodeSubmit}/>
            <button className="resend-code-button" onClick={handleResendCodeClick}>Resend Code</button>
            <p id={"isCodeSent"}>{isCodeSent}</p>
        </div>
    );
}

export default CodeVerification;