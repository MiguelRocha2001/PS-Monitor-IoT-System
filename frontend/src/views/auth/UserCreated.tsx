import React from "react";
import {MyCard, MyLink} from "../Commons";
import {Row} from "react-bootstrap";
import './userCreated.css'


export function UserCreated() {
    return (
        <div className={'user-created'}>
            <Row className="justify-content-center">
                <MyCard
                    title={'Welcome to our IoT device system'}
                    subtitle={'You can now login'}
                >
                    <MyLink text={'Click Here'} to={'/auth/login'} center={false} />
                </MyCard>
            </Row>
        </div>
    );
}