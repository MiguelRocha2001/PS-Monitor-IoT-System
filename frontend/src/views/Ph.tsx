import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {Col, Container, NavLink, Row} from "react-bootstrap";
import {MyChart} from "../chart/MyChart";

function Ph() {
    return (
        <Card>
            <Card.Body>
                <Card.Title>PH Graph</Card.Title>
                <Card.Text>
                    Later, this will display the PH graph.
                    <MyChart />
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default Ph;