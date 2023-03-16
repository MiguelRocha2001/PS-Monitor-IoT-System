import * as React from "react"
import {useState} from "react"
import {Navigate, useHref, useLocation, useNavigate} from "react-router-dom"
import {useCurrentUser, useSetUser} from "./Authn"
import {Logger} from "tslog";
import {services} from "../../services/services";
import {User} from "../../services/domain";
import Form from "react-bootstrap/esm/Form"
import Button from "react-bootstrap/Button";


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

    if (user) {
        return <Navigate to="/me" replace={true}/>
    }

    if(redirect) {
        return <Navigate to={redirect} replace={true}/>
    }
    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        if(name === "username") {
            if( ev.currentTarget.value.length > 20)  {
                setCheckUsername("Username must be less less than 20 characters")
                ev.currentTarget.value = ev.currentTarget.value.substring(0, 20)
            } else setCheckUsername("")
        }
        setInputs({ ...inputs, [name]: ev.currentTarget.value })
        setError(undefined)
    }

    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        setIsSubmitting(true)
        const username = inputs.username
        const password = inputs.password
        if (action === "login") {
            logger.info("Logging in")
            authenticate(username, password)
                .then((result) => {
                    setIsSubmitting(false)
                    if (result instanceof Error) {
                        setError(result.message)
                    } else {
                        setUser(new User(username, password)) // TODO: Change this to the user object
                        setRedirect(location.state?.source?.pathname || "/me")
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
                        setSuccessSignUp("User created successfully, you can now sign in")
                        setError("")
                        navigate("/sign-in")
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
        <Form>
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Email address</Form.Label>
                <Form.Control type="email" placeholder="Enter username" />
                <Form.Text className="text-muted">
                    We'll never share your email with anyone else.
                </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicPassword">
                <Form.Label>Password</Form.Label>
                <Form.Control type="password" placeholder="Password" />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formBasicCheckbox">
                <Form.Check type="checkbox" label="Check me out" />
            </Form.Group>
            <Button variant="primary" type="submit">
                Submit
            </Button>
        </Form>
    )
}