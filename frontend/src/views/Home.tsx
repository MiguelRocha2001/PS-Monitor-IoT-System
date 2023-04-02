import Card from 'react-bootstrap/Card';
import {Row} from "react-bootstrap";
import React from "react";
import {MyCard} from "./Commons";

function Home() {
    return (
        <Row className="justify-content-center">
            <MyCard
                title={'Home'}
                text={'Welcome to Industrial IoT Solutions! In here you can see the available devices, the PH and temperature data.'}
            />
        </Row>
    );
}

export default Home;