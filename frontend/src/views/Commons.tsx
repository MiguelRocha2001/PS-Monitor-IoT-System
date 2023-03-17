import {Link} from 'react-router-dom'
import React from "react";
import {Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";

export function MyLink({text, to, width, color, bold}: { text: string, to: string, width?: string, color?: string, bold?: boolean }) {
    const defaultWidth = 'auto';
    return (
        <Link
            to={to}
            style={{
                textAlign: 'center',
                margin: 'auto',
                marginLeft: '1em',
                marginRight: '1em',
                width: width ?? defaultWidth,
                textDecoration: 'none',
                color: color ?? 'black',
                fontWeight: bold ? 'bold' : 'normal',
            }}>
            {text}
        </Link>
    )
}

export function MyCard({children, title, text}: { children: any, title?: string, text?: string }) {
    return (
        <Row className="justify-content-center">
            <Card>
                <Card.Body>
                    <Card.Title>
                        {title}
                    </Card.Title>
                    <Card.Text>
                        {text}
                    </Card.Text>
                    {children}
                </Card.Body>
            </Card>
        </Row>
    );
}