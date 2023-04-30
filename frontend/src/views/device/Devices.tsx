import {Device} from "../../services/domain";
import React, {useEffect, useState} from "react";
import ListGroup from "react-bootstrap/ListGroup";
import {services} from "../../services/services";
import Card from "react-bootstrap/Card";
import {Stack} from "react-bootstrap";
import {MyLink} from "../Commons";
import {SomethingWentWrong} from "../SomethingWentWrong";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import Pagination from 'react-bootstrap/Pagination';

export function Devices() {
    const setError = useSetError()
    const [devices, setDevices] = useState<Device[]>([])

    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(5)

    useEffect(() => {
        async function fetchDevices() {
            services.getDevices(page, pageSize)
                .then(devices => setDevices(devices))
                .catch(error => setError(error))
        }
        fetchDevices()
    }, [page, pageSize])

    return (
        <ErrorController>
            <PageDisplay onPageChange={(selectedPage: number) => setPage(selectedPage)} />
            <DeviceList devices={devices} />
        </ErrorController>
    )
}

function DeviceList({devices}: { devices: Device[] }) {
    return(
        <Card>
            <Card.Body>
                <Stack gap={2}>
                    <Card.Title>Available Devices</Card.Title>
                    <Stack gap={3}>
                        <ListGroup>
                            {devices.map(device => (
                                <ListGroup.Item key={device.id}>
                                    <MyLink
                                        text={'Info Here'}
                                        to={`/devices/${device.id}`}
                                        center={false}
                                    />
                                    <br/>
                                    {device.id}
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                        <MyLink
                            to="/add-new-device"
                            text={'Add New Device'}
                            color={'DodgerBlue'}
                            bold={true}
                            center={false}
                        />
                    </Stack>
                </Stack>
            </Card.Body>
        </Card>
    )
}

function PageDisplay({onPageChange}: { onPageChange: (page: number) => void }) {
    const [active, setActive] = useState(1)
    const [numbOfPages, setNumbOfPages] = useState(1)

    useEffect(() => {
        async function fetchDeviceCount() {
            const deviceCount = await services.getDeviceCount()
            setNumbOfPages(Math.ceil(deviceCount / 5))
        }
        fetchDeviceCount()
    }, [])

    let items = [];
    for (let number = 1; number <= numbOfPages; number++) {
        items.push(
            <Pagination.Item key={number} active={number === active} onClick={() => {
                onPageChange(number)
                setActive(number)
            }}>
                {number}
            </Pagination.Item>,
        );
    }

    return (
        <Pagination>{items}</Pagination>
    )
}