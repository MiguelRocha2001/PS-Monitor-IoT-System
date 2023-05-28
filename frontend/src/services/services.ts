import {Device, SensorData, User} from "./domain";
import {FakeServices} from "./FakeServices";

/**
 * All functions can return a rejected promise if something goes wrong.
 * The promise will be rejected with a string describing the error.
 */
export interface Services {
    getBackendApiInfo(): Promise<void>
    googleLogin(): Promise<void>
    createUser(password: string, email: string): Promise<void>
    authenticateUser(username: string, password: string): Promise<void>
    isLoggedIn(): Promise<boolean>
    getMe(): Promise<User>

    createDevice(ownerEmail: string): Promise<string>
    getDevices(userId: string, page: number, limit: number): Promise<Device[]>
    getDeviceCount(userId: string): Promise<number>
    getDeviceById(deviceId: string): Promise<Device>

    getSensorData(deviceId: string, sensor: String): Promise<SensorData>
    logout(): Promise<void>
    getDevicesByName(page: number, limit: number, name: string): Promise<Device[]>
    getDeviceCountByName(s: string): Promise<number>
    checkIfUserExists(email: string): Promise<boolean>
    verifyCode(email:string, code: string): Promise<boolean>
    sendValidationCode(email:string): Promise<string>
    availableSensors(deviceId: string): Promise<string[]>

    getUsers(page: number, limit: number): Promise<User[]>
    getUserCount(): Promise<number>
    getUsersByName(page: number, limit: number, name: string): Promise<User[]>
    getUserCountByName(s: string): Promise<number>
}

export const services: Services = new FakeServices()