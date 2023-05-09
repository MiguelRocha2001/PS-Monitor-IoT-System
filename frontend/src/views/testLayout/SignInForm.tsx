import React, {useState} from 'react';
import {Link, Navigate} from 'react-router-dom';
import './SignInForm.css';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faGoogle} from '@fortawesome/free-brands-svg-icons';
import {authenticate} from "../auth/IoTServerAuthentication";
import {Logger} from "tslog";
import {useSetIsLoggedIn} from "../auth/Authn";

const logger = new Logger({ name: "Authentication" });

function SignInForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [redirect, setRedirect] = useState<string | undefined>(undefined)
    const setIsLoggedIn = useSetIsLoggedIn()
    const [isBadInput, setIsBadInput] = useState(false)


    if(redirect)
        return <Navigate to={redirect} replace={true}/>


    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        if(email === "" || password === "") {
            setErrorMessage("Please fill out all fields")
            setIsBadInput(true)
            return
        }
        authenticate(email, password)
            .then((result) => {
                if (result instanceof Error) {
                    setErrorMessage(result.message)
                } else {
                    setIsLoggedIn(true)
                    setRedirect("/")
                }
            })
            .catch(error => {
                logger.error('Login: ', error)
                setErrorMessage("Invalid username or password")
            })
    }

    return (
        <div className="signin-form">
            <h2>Sign In</h2>
            <p>Please fill out the following information to log in your account:</p>
            <form onSubmit={handleSubmit}>
                <label htmlFor="email">Email:</label>
                <input className={isBadInput ? "bad-input" : ""} id={"email"} type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                <label htmlFor="password">Password:</label>
                <input className={isBadInput ? "bad-input" : ""} id={"password"} type="password" value={password} onChange={(e) => {setPassword(e.target.value)}} />
                <p id="error-message">{errorMessage}</p>
                <button type="submit">Sign In </button>
                <button className="google-button" onClick={() => console.log('Google login clicked')}>
                    <FontAwesomeIcon icon={faGoogle} className="google-icon" />
                    Sign in with Google
                </button>
            </form>
            <p>Don't have an account? <Link to="/auth/register">Sign up here</Link>.</p>
        </div>
    );
}

export default SignInForm;