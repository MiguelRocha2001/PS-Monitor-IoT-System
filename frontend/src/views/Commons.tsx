import {Link} from 'react-router-dom'
import React from "react";
import {Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";

export function MyLink({text, to, width, color, bold, center, margin}:
                           {
                               text: string,
                               to: string,
                               width?: string,
                               color?: string,
                               bold?: boolean,
                               center: boolean,
                               margin?: string
                           }) {
    const defaultWidth = 'auto';
    const defaultMargin = '1em';
    return (
        <Link
            to={to}
            style={{
                textAlign: center ? 'center' : 'left',
                margin: 'auto',
                marginLeft: margin ?? 0,
                marginRight: margin ?? 0,
                width: width ?? defaultWidth,
                textDecoration: 'none',
                color: color ?? 'black',
                fontWeight: bold ? 'bold' : 'normal',
            }}>
            {text}
        </Link>
    )
}

export function MyCard({children, title, boldTitle, text, subtitle }:
                           {
                               children?: any,
                               title?: string,
                               boldTitle?: boolean,
                               text?: string[],
                               subtitle?: string
                           }) {
    return (
        <Row className="justify-content-center">
            <Card style={{marginBottom: '3em'}}>
                <Card.Body>
                    <Card.Title>
                        {title && <b style={{fontWeight: boldTitle ? 'bold' : 'normal'}}>{title}</b>}
                    </Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        {subtitle}
                    </Card.Subtitle>
                    {text && text.map((line, index) => (
                        <Card.Text key={index}>
                            {line}
                        </Card.Text>
                    ))}
                    {children}
                </Card.Body>
            </Card>
        </Row>
    );
}