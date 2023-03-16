import Card from 'react-bootstrap/Card';
import {Container, Row} from "react-bootstrap";
import {MyChart} from "../chart/MyChart";

function Temperature() {
    return (
        <Container>
            <Row className="justify-content-center">
                <Card>
                    <Card.Body>
                        <Card.Title>Temperature Graph</Card.Title>
                        <Card.Text>
                            Later, this will display the temperature graph.
                        </Card.Text>
                        <Row style={{width: '60%', margin: 'auto', marginTop: '30px'}}>
                            <MyChart />
                        </Row>
                    </Card.Body>
                </Card>
            </Row>
        </Container>
    );
}

export default Temperature;