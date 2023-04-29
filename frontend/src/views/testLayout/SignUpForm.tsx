import React, { useState } from "react";
import "./SignUpForm.css"
import { Link } from 'react-router-dom';

function SignUpForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        // handle sign-in logic here
    };

    return (
        <div className="signup-form">
            <h2>Sign Up</h2>
            <p>Please fill out the following information to create an account:</p>
            <form onSubmit={handleSubmit}>
                <label htmlFor="email">Email:</label>
                <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                <label htmlFor="password">Password:</label>
                <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                <button type="submit">SignUp</button>
            </form>
            <p>Already have an account? <Link to="/auth/login">Sign in here</Link>.</p>
        </div>
    );
}

export default SignUpForm;