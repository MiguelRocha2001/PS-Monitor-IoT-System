import Button from "react-bootstrap/Button";
import {NavLink} from "react-bootstrap";
import {Link} from 'react-router-dom'
import React from "react";

export function MyNavLink({text, href, width}: { text: string, href: string, width?: string }) {
    const defaultWidth = 'auto';
    return (
        <Link to={href} style={{width: width ?? defaultWidth}}>
            <Button style={{width: '100%'}} variant="primary">
                {text}
            </Button>
        </Link>
    )
}