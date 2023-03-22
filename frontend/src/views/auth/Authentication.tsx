import * as React from "react"
import {useState} from "react"
import {Navigate, useHref, useLocation, useNavigate} from "react-router-dom"
import {useCurrentUser, useSetUser} from "./Authn"
import {Logger} from "tslog";
import {services} from "../../services/services";
import {User} from "../../services/domain";
import Form from "react-bootstrap/esm/Form"
import Button from "react-bootstrap/Button";
import Card from "react-bootstrap/Card";
import {Row} from "react-bootstrap";
import {MyCard} from "../Commons";


const logger = new Logger({ name: "Authentication" });

export async function authenticate(username: string, password: string): Promise<void | Error> {
    try {
        return services.authenticateUser(username, password);
    } catch (e) {
        return new Error("Something went wrong");
    }
}

export async function createUser(username: string, password: string): Promise<void | Error> {
    try {
        return await services.createUser(username, password);
    } catch (e) {
        return new Error("Something went wrong");
    }
}

type Action = "login" | "register"

export function Authentication({title, action}: { title: string, action: Action}) {
    const [inputs, setInputs] = useState({
        username: "",
        password: "",
    })
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState<string | undefined>(undefined)
    const [checkUsername, setCheckUsername] = useState("")
    const [successSignUp, setSuccessSignUp] = useState("")
    const [redirect, setRedirect] = useState<string | undefined>(undefined)
    const setUser = useSetUser()
    const user = useCurrentUser()
    const navigate = useNavigate()
    const location = useLocation()

    if (user)
        return <Navigate to="/" replace={true}/>

    if(redirect)
        return <Navigate to={redirect} replace={true}/>

    function handleChange(name: string, value: string) {
        if(name === "username") {
            if( value.length > 20)  {
                setCheckUsername("Username must be less less than 20 characters")
                value = value.substring(0, 20)
            } else setCheckUsername("")
        }
        setInputs({ ...inputs, [name]: value })
        setError(undefined)
    }

    function handleSubmit() {
        setIsSubmitting(true)
        const username = inputs.username
        const password = inputs.password
        if (action === "login") {
            authenticate(username, password)
                .then((result) => {
                    setIsSubmitting(false)
                    if (result instanceof Error) {
                        setError(result.message)
                    } else {
                        setUser(new User(username, password))
                        setRedirect("/")
                    }
                })
                .catch(error => {
                    logger.error('Login: ', error)
                    setIsSubmitting(false)
                    setError("Invalid username or password")
                })
        } else {
            createUser(username, password)
                .then((result) => {
                    setIsSubmitting(false)
                    if(result instanceof Error)
                        setError(result.message)
                    else {
                        setSuccessSignUp("User created successfully!")
                        setUser(new User(username, password))
                        setError("")
                        navigate("/")
                    }
                    // setRedirect(location.state?.source?.pathname || "/sign-in") // fixme - results in endless loop
                })
                .catch(error => {
                    logger.error('Create user: ', error)
                    setIsSubmitting(false)
                    setError("Unfortunately, this username already exists")
                })
        }
    }

    return (
        <MyCard title={title} text={"Insert credentials under..."}>
            <Form>
                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Username</Form.Label>
                    <Form.Control type="name" placeholder="Enter username" onChange={(ev) => {
                        handleChange("username", ev.target.value)
                    }} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>Password</Form.Label>
                    <Form.Control type="password" placeholder="Password" onChange={(ev) => {
                        handleChange("password", ev.target.value)
                    }} />
                </Form.Group>

                <Button variant="primary" onClick={handleSubmit}>
                    Submit
                </Button>
            </Form>
        </MyCard>
    )
}