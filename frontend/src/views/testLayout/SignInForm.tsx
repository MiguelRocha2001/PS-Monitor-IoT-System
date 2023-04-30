import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './SignInForm.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGoogle } from '@fortawesome/free-brands-svg-icons';

function SignInForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');


    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        // Simulating a check for the password
        console.log("got here")
        if (password !== 'pass') {
            setErrorMessage('Incorrect Credentials');
        } else {
            setErrorMessage('');
            // handle successful sign-in logic here
        }
    };

    return (
        <div className="signin-form">
            <h2>Sign In</h2>
            <p>Please fill out the following information to log in your account:</p>
            <form onSubmit={handleSubmit}>
                <label htmlFor="email">Email:</label>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                <label htmlFor="password">Password:</label>
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                <p id="error-message">{errorMessage}</p>
                <button type="submit">Sign In</button>
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