import {Device, toDevices} from "./domain";
import {doFetch} from "../fetch";
import exp from "constants";

interface Services {
    addDevice(device: Device): void
    getDevices(): Promise<Device[]>
}

export class RealServices implements Services {
    private readonly API_URL = 'http://localhost:8080'

    async addDevice(device: Device) {
        const response = await doFetch(`${(this.API_URL)}/devices`, 'POST', device)
        if (response.status === 201) {
            deviceAdded(device)
            return
        } else {
            throw new Error(`Failed to add device: ${response.status} ${response.statusText}`)
        }
    }

    async getDevices(): Promise<Device[]> {
        const response = await doFetch(`${(this.API_URL)}/devices`, 'GET')
        if (response.status === 200) {
            const json = response.json()
            return toDevices(json)
        } else {
            throw new Error(`Failed to get devices: ${response.status} ${response.statusText}`)
        }
    }
}

export class MockServices implements Services {
    private readonly devices: Device[] = [
        new Device(1),
        new Device(2),
        new Device(3)
    ]
    async addDevice(device: Device) {
        this.devices.push(device)
        deviceAdded(device)
    }

    async getDevices(): Promise<Device[]> {
        return this.devices
    }
}

function deviceAdded(device: Device) {
    console.log(`Device added: ${JSON.stringify(device)}`)
}

export const services: Services = new MockServices()