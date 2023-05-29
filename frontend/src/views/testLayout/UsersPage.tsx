import {User} from "../../services/domain";
import React, {useEffect, useState} from "react";
import {services} from "../../services/services";
import {MyLink} from "../Commons";
import {useSetError} from "../error/ErrorContainer";
import {ErrorController} from "../error/ErrorController";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft, faChevronRight, faSignOutAlt} from '@fortawesome/free-solid-svg-icons';
import {useNavigate} from "react-router-dom";

import './DevicesPage.css'
import Button from "react-bootstrap/Button";
import {useSetIsLoggedIn} from "../auth/Authn";

export function Users() {
    const setError = useSetError()
    const [users, setUsers] = useState<User[]>([])
    const [searchQuery, setSearchQuery] = useState("");


    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(() => {
        return window.innerWidth <= 767 ? 3 : 5; // Adjust the breakpoint as needed
    });
    const [filteredDevices, setFilteredUsers] = useState(0)
    const [totalUsers, setTotalUsers] = useState(0)
    const [loggedOut, setLoggedOut] = useState(false)
    const setIsLoggedIn = useSetIsLoggedIn()


    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth <= 767) { // Adjust the breakpoint as needed
                setPageSize(3);
            } else {
                setPageSize(5);
            }
        };

        window.addEventListener('resize', handleResize);

        // Cleanup the event listener when the component unmounts
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);

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
        async function fetchNumberOfUsers() {
            services.getUserCount(undefined)
                .then((number) => setTotalUsers(number))
                .catch(error => setError(error.message))
        }
        fetchNumberOfUsers()
    }, [])

    useEffect(() => { //TODO IF I FETCH DEVICE I STORE THEME SO WHEN I CLICK IN THE PREVIOUS BUTTON A NEW REQUEST IS NOT MADE
        async function fetchNumberOfUsers() {
            services.getUserCount(undefined)
                .then((number) => setFilteredUsers(number))
                .catch(error => setError(error.message))
        }
        if(searchQuery === "") fetchNumberOfUsers()

    }, [searchQuery])

    useEffect(() => {
        async function updateUsers() {
            services.getUsers(page, pageSize, undefined)
                .then(users => setUsers(users))
                .catch(error => setError(error.message))
        }
        updateUsers()
    }, [page, pageSize, searchQuery, totalUsers])

    const handleButtonPress = () => {
        if(searchQuery === "") return
        console.log(searchQuery)
        services.getUsers(page, pageSize, searchQuery.toUpperCase())
            .then(users => {setUsers(users)})
            .then(()=> services.getUserCount(searchQuery.toUpperCase()))
            .then((devicesSize)=>setFilteredUsers(devicesSize))
            .catch(error => setError(error.message))
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
        <div className={"Users"}>
            <ErrorController>
                <LogoutButton handleButtonPressed={handleButtonPressed}/>
                <UserList users={users} searchQuery={searchQuery} setSearchQuery={setSearchQuery} handleButtonPress={handleButtonPress} totalUsers={totalUsers}/>
                <Pagination currentPage={page} totalPages={Math.ceil(filteredDevices/pageSize)} onPageChange={(selectedPage: number) => setPage(selectedPage)} />
            </ErrorController>
        </div>
    )
}

function UserList({ users, searchQuery, setSearchQuery, handleButtonPress, totalUsers }: { users: User[], searchQuery: string, setSearchQuery: (searchQuery: string) => void, handleButtonPress: () => void, totalUsers: number }) {
    const lastLineText = users.length === 0 ? "No Users found." : `Showing ${users.length} of ${totalUsers} users.`

    return (
        <div className="card">
            <div className="card-header">
                <h3 id={"logged-as"}> Showing all users</h3>
            </div>
            <div className="card-body">
                {users.length > 0 ? (
                    <ul className="list-group">
                        {users.map((user) => (
                            <li key={user.email} className="list-group-item">
                                <div className="list-item-info">
                                    <MyLink
                                        to={`/users/${user.id}/devices`}
                                        text={user.email}
                                        center={false}
                                    />
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p></p>
                )}
                <b id={"last-line"}>{lastLineText}</b>
            </div>
            <InputTextBox
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
                onSearch={handleButtonPress}
            />
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
                placeholder="Search for User"
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

