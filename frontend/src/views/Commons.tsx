import Button from "react-bootstrap/Button";
import {NavLink} from "react-bootstrap";
import React from "react";

export function MyNavLink({text, href, width}: { text: string, href: string, width?: string }) {
    const defaultWidth = 'auto';
    return (
        <NavLink href={href} style={{width: width ?? defaultWidth }} className="justify-content-center">
            <Button style={{width: '100%'}} variant="primary">{text}</Button>
        </NavLink>
    )
}