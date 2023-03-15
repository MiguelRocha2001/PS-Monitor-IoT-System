import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {CardGroup, Col, Container, NavLink, Row, Stack} from "react-bootstrap";

function Home() {
    return (
        <Container>
            <Row className="justify-content-center">
                <Card style={{ width: '18rem' }}>
                    <Card.Body>
                        <Card.Title>Home</Card.Title>
                        <Card.Text>
                            This is the home page.
                        </Card.Text>
                        <Stack gap={2}>
                            <NavLink href="/devices"><Button variant="primary">See available devices</Button></NavLink>
                            <NavLink href="/add-new-device"><Button variant="primary">Add New Device</Button></NavLink>
                            <NavLink href="/ph"><Button variant="primary">PH Graph</Button></NavLink>
                            <NavLink href="/temperature"><Button variant="primary">Temperature Graph</Button></NavLink>
                        </Stack>
                    </Card.Body>
                </Card>
            </Row>
        </Container>
    );
}

export default Home;