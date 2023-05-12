import {Device} from "../../services/domain";
import React, {useEffect, useState} from "react";
import {services} from "../../services/services";
import {MyLink} from "../Commons";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronLeft, faChevronRight,faSignOutAlt } from '@fortawesome/free-solid-svg-icons';
import {useNavigate,Navigate} from "react-router-dom";

import './DevicesPage.css'
import Button from "react-bootstrap/Button";
import {useSetIsLoggedIn} from "../auth/Authn";

export function Devices() {
    const setError = useSetError()
    const [devices, setDevices] = useState<Device[]>([])
    const [searchQuery, setSearchQuery] = useState("");


    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(5)
    const [filteredDevices, setFilteredDevices] = useState(0)
    const [totalDevices, setTotalDevices] = useState(0)
    const [loggedOut, setLoggedOut] = useState(false)
    const setIsLoggedIn = useSetIsLoggedIn()


    /*
        useEffect(
            () => {
                if (loggedOut) {
                    const navigate = useNavigate()
                    console.log("logged out")
                    return navigate("/")
                }
            }, [loggedOut]
        )
    */
    useEffect(() => {
        async function fetchNumberOfDevices() {
            services.getDeviceCount()
                .then((number) =>
                    setTotalDevices(number)
                )
                .catch(error => setError(error))
        }
        fetchNumberOfDevices()
    }, [])

    useEffect(() => { //TODO IF I FETCH DEVICE I STORE THEME SO WHEN I CLICK IN THE PREVIOUS BUTTON A NEW REQUEST IS NOT MADE
        async function fetchNumberOfDevices() {
            services.getDeviceCount()
                .then((number)=>
                    setFilteredDevices(number)
                )
                .catch(error => setError(error))
        }
        if(searchQuery === "") fetchNumberOfDevices()

    }, [searchQuery])

    useEffect(() => {
        async function fetchDevices() {
            services.getDevices(page, pageSize)
                .then(devices => {
                    console.log(devices)
                    setDevices(devices);
                })
                .catch(error => setError(error))
        }
        fetchDevices()
    }, [page, pageSize, searchQuery, totalDevices])

    const handleButtonPress = () => {
         services.getDevicesByName(page, pageSize, searchQuery.toUpperCase()).then(
             devices => {setDevices(devices);})
             .then(()=> services.getDeviceCountByName(searchQuery.toUpperCase()))
             .then((devicesSize)=>setFilteredDevices(devicesSize))
             .catch(error => setError(error)
         )
    }

    const navigate = useNavigate();

    async function handleButtonPressed() {
        await services.logout().then(()=> {
            localStorage.removeItem('email')//FIXME use context instead
            setLoggedOut(true)
            setIsLoggedIn(false)
        })
    }

    return (
        <ErrorController>
            <LogoutButton handleButtonPressed={handleButtonPressed}/>
            <DeviceList devices={devices} searchQuery={searchQuery} setSearchQuery={setSearchQuery} handleButtonPress={handleButtonPress} totalDevices={totalDevices}/>
            <Pagination currentPage={page} totalPages={Math.ceil(filteredDevices/pageSize)} onPageChange={(selectedPage: number) => setPage(selectedPage)} />
        </ErrorController>
    )
}

function DeviceList({ devices, searchQuery, setSearchQuery, handleButtonPress,totalDevices }: { devices: Device[], searchQuery: string, setSearchQuery: (searchQuery: string) => void, handleButtonPress: () => void, totalDevices: number }) {
    const lastLineText = devices.length === 0 ? "No devices found." : `Showing ${devices.length} of ${totalDevices} devices.`

    return (
        <div className="card">
            <div className="card-header">
                <h3 id={"logged-as"}> Showing devices associated with <u>{localStorage.getItem('email')}</u></h3>
            </div>
            <div className="card-body">
                {devices.length > 0 ? (
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
                ) : (
                    <p></p>
                )}
                <div className="add-device">
                    <MyLink
                        to="/add-new-device"
                        text="Add New Device"
                        color="#fff"
                        bold={true}
                        center={false}
                    />
                </div>
                <InputTextBox
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    onSearch={handleButtonPress}
                />
                <b id={"last-line"}>{lastLineText}</b>
            </div>
        </div>
    );
}

function Pagination({ currentPage, totalPages, onPageChange }: { currentPage: number, totalPages: number, onPageChange: (pageNumber: number) => void }) {
    return (
        <div className="d-flex justify-content-center mt-4">
            <button className="pagination-btn" disabled={currentPage <= 1} onClick={() => onPageChange(currentPage - 1)}>
                <FontAwesomeIcon icon={faChevronLeft} />
            </button>
            <button className="pagination-btn" disabled={currentPage >= totalPages} onClick={() => onPageChange(currentPage + 1)}>
                <FontAwesomeIcon icon={faChevronRight} />
            </button>
        </div>
    );
}

function InputTextBox({ searchQuery, setSearchQuery, onSearch }: { searchQuery: string, setSearchQuery: (searchQuery: string) => void, onSearch: () => void }) {
    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            onSearch();
        }
    };

    return (
        <div className="input-group mb-3">
            <input
                type="text"
                className="form-control"
                placeholder="Search for device"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={handleKeyPress}
            />
            <button id={"searchButton"} type="button" onClick={onSearch}>Search</button>
        </div>
    );
}

function LogoutButton({handleButtonPressed}: {handleButtonPressed: () => void}) {
   return  <div className={"upper-section"}>
        <div className="button-container">
            <Button variant="outline-primary logout-btn" onClick={handleButtonPressed}>
                <FontAwesomeIcon icon={faSignOutAlt} /> LOGOUT
            </Button>
        </div>
    </div>
}

