import {Alert} from "react-bootstrap";
import React from "react";

export function StillInProgressAlert() {
    return (
        <Alert variant="success">
            <Alert.Heading>Hey, nice to see you</Alert.Heading>
            <p>
                Be advised that this is still a work in progress.
                The contents of this website are not final.
            </p>
        </Alert>
    );
}