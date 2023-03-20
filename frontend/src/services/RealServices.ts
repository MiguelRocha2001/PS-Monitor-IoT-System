import {doFetch} from "../fetch";
import {Device, PhData, TemperatureData, toDevices, toPhData, toTemperatureData, User} from "./domain";
import {deviceAdded, Services} from "./services";

export class RealServices implements Services {
    private readonly API_URL = 'http://localhost:8080'

    async createUser(username: string, password: string) {
        const response = await doFetch(`${(this.API_URL)}/users`, 'POST', {username, password})
        if (response.status === 201) {
            return
        } else {
            throw new Error(`Failed to create user: ${response.status} ${response.statusText}`)
        }
    }

    async authenticateUser(username: string, password: string) {
        const response = await doFetch(`${(this.API_URL)}/users/authenticate`, 'POST', {username, password})
        if (response.status === 200) {
            return
        } else {
            throw new Error(`Failed to authenticate user: ${response.status} ${response.statusText}`)
        }
    }

    async isLoggedIn(): Promise<boolean> {
        // TODO: Implement
        throw new Error('Not implemented')
    }

    async getMe(): Promise<User> {
        // TODO: Implement
        throw new Error('Not implemented')
    }

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

    async getPhData(deviceId: string): Promise<PhData> {
        const response = await doFetch(`${(this.API_URL)}/devices/${deviceId}/ph`, 'GET')
        if (response.status === 200) {
            const json = response.json()
            return toPhData(json)
        } else {
            throw new Error(`Failed to get ph data: ${response.status} ${response.statusText}`)
        }
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        const response = await doFetch(`${(this.API_URL)}/devices/${deviceId}/temperature`, 'GET')
        if (response.status === 200) {
            const json = response.json()
            return toTemperatureData(json)
        } else {
            throw new Error(`Failed to get temperature data: ${response.status} ${response.statusText}`)
        }
    }

    logout(): Promise<void> {
        return Promise.resolve(undefined);
    }
}