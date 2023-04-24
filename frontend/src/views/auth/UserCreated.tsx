import React from "react";
import {MyCard, MyLink} from "../Commons";
import {Row} from "react-bootstrap";


export function UserCreated() {
    return (
        <Row className="justify-content-center">
            <MyCard
                title={'User Created'}
                text={['You can now login']}
            >
                <MyLink text={'Click Here'} to={'/auth/login'} center={false} />
            </MyCard>
        </Row>
    );
}