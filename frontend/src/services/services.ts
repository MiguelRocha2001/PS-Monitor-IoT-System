import {
    Device,
    PhData,
    PhRecord,
    TemperatureData,
    TemperatureRecord,
    toDevices,
    toPhData,
    toTemperatureData, User
} from "./domain";
import {doFetch} from "../fetch";
import {MockServices} from "./FakeServices";

export interface Services {
    createUser(username: string, password: string): void
    authenticateUser(username: string, password: string): void
    isLoggedIn(): Promise<boolean>
    getMe(): Promise<User>
    addDevice(device: Device): void
    getDevices(): Promise<Device[]>
    getPhData(deviceId: string): Promise<PhData>
    getTemperatureData(deviceId: string): Promise<TemperatureData>
    logout(): Promise<void>
}

export function deviceAdded(device: Device) {
    console.log(`Device added: ${JSON.stringify(device)}`)
}

export const services: Services = new MockServices()