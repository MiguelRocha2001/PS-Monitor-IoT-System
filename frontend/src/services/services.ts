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

interface Services {
    createUser(username: string, password: string): void
    authenticateUser(username: string, password: string): void
    isLoggedIn(): Promise<boolean>
    getMe(): Promise<User>
    addDevice(device: Device): void
    getDevices(): Promise<Device[]>
    getPhData(deviceId: string): Promise<PhData>
    getTemperatureData(deviceId: string): Promise<TemperatureData>
}

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
}

export class MockServices implements Services {
    private readonly users: User[] = []
    private user: User | null = null

    private readonly devices: Device[] = [
        new Device('e76996c8-c469-440c-bc2a-82eabbc3ca99'),
        new Device('50947fb9-0367-41d2-a095-4d26fdc7a7f2'),
        new Device('cd152448-04b2-473e-86b4-50521e30fb27')
    ]

    async createUser(username: string, password: string) {
        this.users.push(new User(username, password))
    }

    async authenticateUser(username: string, password: string) {
        const user = this.users.find(u => u.username === username && u.password === password)
        if (user) {
            this.user = user
        } else {
            throw new Error('Invalid username or password')
        }
    }

    async isLoggedIn(): Promise<boolean> {
        return Promise.resolve(this.user !== null)
    }

    async getMe(): Promise<User> {
        if (this.user) {
            return this.user
        } else {
            throw new Error('Not logged in')
        }
    }

    async addDevice(device: Device) {
        this.devices.push(device)
        deviceAdded(device)
    }

    async getDevices(): Promise<Device[]> {
        return this.devices
    }

    async getPhData(deviceId: string): Promise<PhData> {
        return new PhData(deviceId, [
            new PhRecord(7.0, 1),
            new PhRecord(7.1, 2),
            new PhRecord(7.2, 3),
            new PhRecord(7.3, 4),
            new PhRecord(7.4, 5),
            new PhRecord(7.5, 6),
            new PhRecord(7.6, 7),
            new PhRecord(7.7, 8),
            new PhRecord(7.8, 9),
            new PhRecord(7.9, 10),
            new PhRecord(8.0, 11),
            new PhRecord(8.1, 12)
        ])
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        return new TemperatureData(deviceId, [
            new TemperatureRecord(20.0, 1),
            new TemperatureRecord(20.1, 2),
            new TemperatureRecord(20.2, 3),
            new TemperatureRecord(20.3, 4),
            new TemperatureRecord(20.4, 5),
            new TemperatureRecord(20.5, 6),
            new TemperatureRecord(20.6, 7),
            new TemperatureRecord(20.7, 8),
            new TemperatureRecord(20.8, 9),
            new TemperatureRecord(20.9, 10),
            new TemperatureRecord(21.0, 11),
            new TemperatureRecord(21.1, 12)
        ])
    }
}

function deviceAdded(device: Device) {
    console.log(`Device added: ${JSON.stringify(device)}`)
}

export const services: Services = new MockServices()