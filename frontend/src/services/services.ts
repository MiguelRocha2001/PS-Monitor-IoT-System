import {Device, SensorData, User} from "./domain";
import {FakeServices} from "./FakeServices";
import {RealServices} from "./RealServices";

/**
 * All functions can return a rejected promise if something goes wrong.
 * The promise will be rejected with a string describing the error.
 */
export interface Services {
    googleLogin(): Promise<void>
    getBackendSirenInfo(): Promise<void>
    createUser(password: string, email: string): Promise<void>
    authenticateUser(username: string, password: string): Promise<void>
    isLoggedIn(): Promise<boolean>
    getMe(): Promise<User>
    createDevice(ownerEmail: string): Promise<string>
    getDevices(page: number, limit: number): Promise<Device[]>
    getDeviceCount(): Promise<number>
    getDevice(deviceId: string): Promise<Device>
    getSensorData(deviceId: string, sensor: String): Promise<SensorData>
    logout(): Promise<void>
    getDevicesByName(page: number, limit: number, name: string): Promise<Device[]>
    getDeviceCountByName(s: string): Promise<number>
    checkIfUserExists(email: string): Promise<boolean>
    verifyCode(email:string, code: string): Promise<boolean>
    sendValidationCode(email:string): Promise<string>
    availableSensors(deviceId: string): Promise<string[]>
}

export const services: Services = new FakeServices()