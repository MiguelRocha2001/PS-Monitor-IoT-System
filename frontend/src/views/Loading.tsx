import {useEffect, useState} from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faSpinner} from '@fortawesome/free-solid-svg-icons';
import './loading.css';

export function Loading() {
    const [showError, setShowError] = useState(false);

    useEffect(() => {
        const timeout = setTimeout(() => {
            setShowError(true);
        }, 15000);

        return () => clearTimeout(timeout);
    }, []);

    if (showError) {
        return <div className="center-page" >Something went wrong, please try again later.</div>;
    }

    return (
        <div className="center-page">
            <FontAwesomeIcon  className="spinner" icon={faSpinner} spin />
            <p>Loading...</p>
        </div>
    );
}
