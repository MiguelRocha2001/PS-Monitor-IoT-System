import React from 'react';
import './404.css';

function NotFound() {
    return (
        <div className="not-found-container">
            <h1 className="not-found-title">404</h1>
            <p className="not-found-text">Page not found</p>
            <a href="/" className="not-found-link">Go back to homepage</a>
        </div>
    );
}

export default NotFound;
