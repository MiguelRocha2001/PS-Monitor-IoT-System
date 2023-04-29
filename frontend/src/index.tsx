import React, {useEffect} from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import 'bootstrap/dist/css/bootstrap.min.css';
import {BrowserRouter} from "react-router-dom";
import {StillInProgressAlert} from "./views/StillInProgressAlert";
import {Col} from "react-bootstrap";
import {App} from "./App";
import Home from "./views/Home";
import FrontPage from "./views/testLayout/FrontPage";
import SignInForm from "./views/testLayout/SignInForm";
import SignUpForm from "./views/testLayout/SignUpForm";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
    <React.StrictMode>
        <BrowserRouter >
            <SignInForm/>
        </BrowserRouter>
    </React.StrictMode>
);
//<StillInProgressAlert /> yes, it is still in progress
// <Col style={{width: '90%', margin: 'auto', marginTop: '30px'}}><App /></Col>
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