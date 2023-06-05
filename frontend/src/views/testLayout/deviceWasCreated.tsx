import React from "react";
import {Link, useParams} from "react-router-dom";
import {MyCard} from "../Commons";
import "./devicewascreated.css";
import {Button} from "react-bootstrap";

export function DeviceCreated() {
    const { deviceId } = useParams<string>();
    const [copied, setCopied] = React.useState(false);

    function copyToClipboard() {
        if (!navigator.clipboard) {
            alert("Your browser does not support copying to clipboard");
            return;
        }
        if(!deviceId)return;

        navigator.clipboard.writeText(deviceId).then(() => setCopied(true));
    }

    return (
        <div className="d-flex flex-column align-items-center">
            <MyCard
                title="Device Successfully Created"
                subtitle={`Device ID: ${deviceId}`}
                text={[
                    "You can now start sending data from your device by using the device ID provided above.",
                    "Thank you for using our service and we look forward to receiving your data!",
                ]}
            />
            <div className="d-flex justify-content-center align-items-center mt-3">
                <Button onClick={copyToClipboard}>
                    {copied ? "Copied!" : "Copy Id to Clipboard"}
                </Button>
                <Link to="/home" className="ml-3">
                    <Button variant="secondary">Go to Devices</Button>
                </Link>
            </div>
        </div>
    );
}
