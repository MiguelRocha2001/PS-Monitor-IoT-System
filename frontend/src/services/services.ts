import {Device, PhData, TemperatureData, User} from "./domain";
import {RealServices} from "./RealServices";
import {MockServices} from "./FakeServices";

export interface Services {
    getBackendSirenInfo(): Promise<void>
    createUser(username: string, password: string, email: string, mobile: string): Promise<void>
    authenticateUser(username: string, password: string): Promise<void>
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