import React, { useState } from 'react';
import axios from 'axios';
import './CodeEmailVerification.css';
import CodeInput from "./CodeInput";

function CodeVerification() {
    const [isCodeIncorrect, setIsCodeIncorrect] = useState<boolean>(false);


    const shakeInput = isCodeIncorrect ? 'shake' : '';
    function onCodeSubmit(code: string) {
        if(code === '12345') { // number of allowed tries 5, max duration token time is 30 minutes
            setIsCodeIncorrect(false);
            return true;
        }
        setIsCodeIncorrect(true);
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
            <CodeInput onCodeSubmit={onCodeSubmit}/>
        </div>
    );
}

export default CodeVerification;