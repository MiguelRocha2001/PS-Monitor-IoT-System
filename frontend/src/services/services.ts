import {Device, PhData, TemperatureData, User} from "./domain";
import {RealServices} from "./RealServices";
import {FakeServices} from "./FakeServices";

export interface Services {
    googleLogin(): Promise<void>
    getBackendSirenInfo(): Promise<void>
    createUser(username: string, password: string, email: string, mobile: string): Promise<void>
    authenticateUser(username: string, password: string): Promise<void>
    isLoggedIn(): Promise<boolean>
    getMe(): Promise<User>
    createDevice(ownerEmail: string): Promise<string>
    getDevices(page: number, limit: number): Promise<Device[]>
    getDevice(deviceId: string): Promise<Device>
    getPhData(deviceId: string): Promise<PhData>
    getTemperatureData(deviceId: string): Promise<TemperatureData>
    logout(): Promise<void>
}

export const services: Services = new FakeServices()