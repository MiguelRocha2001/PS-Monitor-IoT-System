import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom"
import axios from 'axios';
import './CodeEmailVerification.css';
import CodeInput from "./CodeInput";
import {services} from "../../services/services";
import {createUser} from "../auth/IoTServerAuthentication";
import {useSetIsLoggedIn} from "../auth/Authn";
import {Logger} from "tslog";
const logger = new Logger({ name: "Authentication" });


type OwnerDetails = {
    email: string;
    password: string;
};

function CodeVerification({ email,password }: OwnerDetails) {
    const [isCodeIncorrect, setIsCodeIncorrect] = useState<boolean>(false);
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [isCodeSent, setIsCodeSent] = useState<string>('');
    const [coolDownCode, setCoolDownCode] = useState<boolean>(false);
    const navigate = useNavigate()
    const setIsLoggedIn = useSetIsLoggedIn()

    function handleResendCodeClick() {
        // Disable the resend code button
        setCoolDownCode(true);

        // TODO: implement the logic for resending the code,the resent code needs to be different from the previous one
        setIsCodeSent("Code sent")
        console.log('Resending code...');
    }

    function onCodeSubmit(code: string) {
        if(code === '12345') { //TODO number of allowed tries 5, max duration token time is 30 minutes, if this happens change the error message to "Your code has expired, please request a new one"
            setIsCodeIncorrect(false);
            createUser(password, email)
                .then((result) => {
                        setIsLoggedIn(false)
                        navigate('/auth/user-created')
                })
                .catch(error => {
                    logger.error('Create user: ', error)
                })
            return true;
        }else {
            setIsCodeIncorrect(true);
            setErrorMessage('Incorrect code, please try again');
            setIsCodeSent('')
        }


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
            <h2>A code was sent to <b>{email}</b></h2>
            <p>Please enter the 5-digit code to verify your account:</p>
            <p id="errorMessage" >{errorMessage}</p>
            <CodeInput/>
            {(!coolDownCode) && (<button className="resend-code-button" onClick={handleResendCodeClick}>Resend Code</button>)}
            {(coolDownCode) && (<p id={"isCodeSent"}>{isCodeSent}</p>)}
        </div>
    );


}



export default CodeVerification;