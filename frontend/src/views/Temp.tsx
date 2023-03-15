import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {Col, Container, NavLink, Row} from "react-bootstrap";
import {MyChart} from "../chart/MyChart";

function Temp() {
    return (
        <Container>
            <Row className="justify-content-center">
                <Card>
                    <Card.Body>
                        <Card.Title>Temperature Graph</Card.Title>
                        <Card.Text>
                            Later, this will display the temperature graph.
                        </Card.Text>
                        <MyChart />
                    </Card.Body>
                </Card>
            </Row>
        </Container>
    );
}

export default Temp;