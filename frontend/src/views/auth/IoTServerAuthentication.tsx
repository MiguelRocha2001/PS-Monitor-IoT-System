import * as React from "react"
import {useState} from "react"
import {Navigate, useLocation, useNavigate} from "react-router-dom"
import {useIsLoggedIn, useSetIsLoggedIn} from "./Authn"
import {Logger} from "tslog";
import {services} from "../../services/services";
import {User} from "../../services/domain";
import Form from "react-bootstrap/esm/Form"
import Button from "react-bootstrap/Button";
import {MyCard} from "../Commons";
import {Stack} from "react-bootstrap";


const logger = new Logger({ name: "Authentication" });

export async function authenticate(email: string, password: string): Promise<void | Error> {
    try {
        return services.authenticateUser(email, password);
    } catch (e) {
        return new Error("Something went wrong");
    }
}

export async function createUser(password: string, email: string): Promise<void | Error> {
    try {
        return await services.createUser(password, email);
    } catch (e) {
        return new Error("Something went wrong");
    }
}

export type Action = "login" | "register"

export function IoTServerAuthentication({title, action}: { title: string, action: Action}) {
    const [inputs, setInputs] = useState({
        username: "",
        password: "",
        email: "",
        mobile: ""
    })
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState<string | undefined>(undefined)
    const [checkUsername, setCheckUsername] = useState("")
    const [successSignUp, setSuccessSignUp] = useState("")
    const [redirect, setRedirect] = useState<string | undefined>(undefined)
    const setIsLoggedIn = useSetIsLoggedIn()
    const user = useIsLoggedIn()
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
        const email = inputs.email
        const mobile = inputs.mobile
        if (action === "login") {
            authenticate(username, password)
                .then((result) => {
                    setIsSubmitting(false)
                    if (result instanceof Error) {
                        setError(result.message)
                    } else {
                        setIsLoggedIn(true)
                        setRedirect("/")
                    }
                })
                .catch(error => {
                    logger.error('Login: ', error)
                    setIsSubmitting(false)
                    setError("Invalid username or password")
                })
        } else {
            createUser(password, email)
                .then((result) => {
                    setIsSubmitting(false)
                    if(result instanceof Error) {
                        setError(result.message)
                    } else {
                        setSuccessSignUp("User created successfully!")
                        setIsLoggedIn(false)
                        setError("")
                        setRedirect('/auth/user-created')
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

    const emailComponent = action === "login" ? <></> : (
        <Form.Group className="mb-3" controlId="formBasicPassword">
            <Form.Label>Email</Form.Label>
            <Form.Control type="email" placeholder="Email" onChange={(ev) => {
                handleChange("email", ev.target.value)
            }} />
        </Form.Group>
    )

    return (
        <MyCard title={title} text={["Insert credentials under..."]}>
            <Stack gap={2}>
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

                    {emailComponent}

                    <Button variant="primary" style={{width: '10%'}} onClick={handleSubmit}>
                        Submit
                    </Button>
                </Form>
                <Button variant="secondary" style={{width: '10%'}} onClick={() => {
                    if (action === "login")
                        navigate("/auth/register")
                    else
                        navigate("/auth/login")
                }}>
                    Switch
                </Button>
            </Stack>
        </MyCard>
    )
}