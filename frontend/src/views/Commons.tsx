import {Link} from 'react-router-dom'
import React from "react";

export function MyLink({text, to, width, color, bold}: { text: string, to: string, width?: string, color?: string, bold?: boolean }) {
    const defaultWidth = 'auto';
    return (
        <Link
            to={to}
            style={{
                width: width ?? defaultWidth,
                textDecoration: 'none',
                color: color ?? 'black',
                fontWeight: bold ? 'bold' : 'normal',
            }}>
            {text}
        </Link>
    )
}