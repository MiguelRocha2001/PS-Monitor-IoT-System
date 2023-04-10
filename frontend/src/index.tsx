import React, {useEffect} from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import 'bootstrap/dist/css/bootstrap.min.css';
import {AuthProvider} from "./auth/auth";
import {BrowserRouter} from "react-router-dom";
import {StillInProgressAlert} from "./views/StillInProgressAlert";
import {Col} from "react-bootstrap";
import {App} from "./App";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
    <React.StrictMode>
        <AuthProvider>
            <BrowserRouter >
                <Col style={{width: '90%', margin: 'auto', marginTop: '30px'}}>
                    <StillInProgressAlert />
                    <App />
                </Col>
            </BrowserRouter>
        </AuthProvider>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

function Test() {
    const [show, setShow] = React.useState<boolean>(false);

    useEffect(() => {
        setTimeout(() => {
            setShow(true);
        }, 1000);
    }, []);

    if (show) {
        return <p>Test</p>
    } else {
        return <p>Not yet</p>
    }
}