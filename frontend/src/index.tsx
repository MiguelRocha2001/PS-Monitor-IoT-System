import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import 'bootstrap/dist/css/bootstrap.min.css';
import {services} from "./services/services";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

// Ensures that the Services module extracts all available Siren information, from the backend.
services.getBackendSirenInfo().then(() => {
    console.log("Siren information extracted from the backend.")
    root.render(
      <React.StrictMode>
        <App />
      </React.StrictMode>
    );
});

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
