import * as React from 'react'
import {Logger} from "tslog";
import {useError} from "./ErrorContainer";
import {SomethingWentWrong} from "../SomethingWentWrong";

const logger = new Logger({ name: "ErrorController" });

export function ErrorController({ children }: { children: React.ReactNode }): React.ReactElement {
    const error = useError()
    // const location = useLocation()

    if (error === undefined) {
        return <>{children}</>
    } else {
        logger.error("Error: " + error.message)
        return (<SomethingWentWrong details={error.message} />);
    }
}