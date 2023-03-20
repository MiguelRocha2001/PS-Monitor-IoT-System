import {Device, PhData, PhRecord, TemperatureData, TemperatureRecord, User} from "./domain";
import {deviceAdded, Services} from "./services";

export class MockServices implements Services {
    private readonly users: User[] = []
    private user: User | null = null

    private readonly devices: Device[] = [
        new Device('e76996c8-c469-440c-bc2a-82eabbc3ca99'),
        new Device('50947fb9-0367-41d2-a095-4d26fdc7a7f2'),
        new Device('cd152448-04b2-473e-86b4-50521e30fb27')
    ]

    async createUser(username: string, password: string) {
        const newUser = new User(username, password)
        this.users.push(newUser)
        this.user = newUser
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

    async logout(): Promise<void> {
        this.user = null
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