import {MyCard} from "../Commons";
import React from "react";
import {useParams} from "react-router-dom";
import {Action, IoTServerAuthentication} from "./IoTServerAuthentication";
import Login from "./GoogleLogin";
import {Alert, Col, Stack} from "react-bootstrap";

export function Authentication() {
    const {action} = useParams<string>()
    if (action === "login" || action === "register") {
        return (
            <MyCard title={'Authentication'} boldTitle={true} children={
                <Col>
                    <IoTServerAuthentication title={`IoT Server ${action}`} action={action}/>
                    <Login/>
                </Col>
            }/>
        );
    } else {
        return <Alert variant="danger">Invalid action</Alert>
    }
}