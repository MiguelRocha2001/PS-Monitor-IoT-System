import {Device} from "../../services/domain";
import React, {useEffect, useState} from "react";
import {services} from "../../services/services";
import {MyLink} from "../Commons";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import Pagination from 'react-bootstrap/Pagination';
import './DevicesPage.css'

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
            <DeviceList devices={devices} />
            <PageDisplay onPageChange={(selectedPage: number) => setPage(selectedPage)} />
        </ErrorController>
    )
}

function DeviceList({ devices }: { devices: Device[] }) {
    return (
        <div className="card">
            <div className="card-header">
                <h3>Devices Currently Active</h3>
            </div>
            <div className="card-body">
                <ul className="list-group">
                    {devices.map((device) => (
                        <li key={device.id} className="list-group-item">
                            <div className="list-item-info">
                                <MyLink
                                    to={`/devices/${device.id}`}
                                    text={device.id}
                                    center={false}
                                />
                            </div>
                        </li>
                    ))}
                </ul>
                <div className="add-device">
                    <MyLink
                        to="/add-new-device"
                        text="Add New Device"
                        color="#fff"
                        bold={true}
                        center={false}
                    />
                </div>
            </div>
        </div>
    );
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
            <Pagination.Item id={"pagination-elem"} key={number} active={number === active} onClick={() => {
                onPageChange(number)
                setActive(number)
            }}>
                {number}
            </Pagination.Item>,
        );
    }

    return (
        <div className="pagination-container">
            <Pagination>{items}</Pagination>
        </div>
    )
}