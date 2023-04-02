import {Device, PhData, PhRecord, TemperatureData, TemperatureRecord, User} from "./domain";
import {deviceAdded, Services} from "./services";

export class MockServices implements Services {
    private readonly users: User[] = []
    private user: User | null = null

    private readonly email = 'some_email_1@gmail.com'
    private readonly mobile = 1234567890

    private readonly devices: Device[] = [
        new Device('e76996c8-c469-440c-bc2a-82eabbc3ca99', this.email, this.mobile),
        new Device('50947fb9-0367-41d2-a095-4d26fdc7a7f2', this.email, this.mobile),
        new Device('cd152448-04b2-473e-86b4-50521e30fb27', this.email, this.mobile)
    ]

    async getBackendSirenInfo() {
        // Nothing to do
    }

    async createUser(username: string, password: string, email: string, mobile: string): Promise<void> {
        const newUser = new User(username, password)
        this.users.push(newUser)
        this.user = newUser
    }

    async authenticateUser(username: string, password: string): Promise<void> {
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

    async getDevice(deviceId: string): Promise<Device> {
        const device = this.devices.find(d => d.id === deviceId)
        if (device) {
            return device
        } else {
            throw new Error(`Device ${deviceId} not found`)
        }
    }

    async getPhData(deviceId: string): Promise<PhData> {
        return new PhData(deviceId, [
            new PhRecord(7.0, new Date('2019-01-01T00:00:00.000Z')),
            new PhRecord(7.1, new Date('2019-01-01T01:00:00.000Z')),
            new PhRecord(7.2, new Date('2019-01-01T02:00:00.000Z')),
            new PhRecord(7.3, new Date('2019-01-01T03:00:00.000Z')),
            new PhRecord(7.4, new Date('2019-01-01T04:00:00.000Z')),
            new PhRecord(7.5, new Date('2019-01-01T05:00:00.000Z')),
            new PhRecord(7.6, new Date('2019-01-01T06:00:00.000Z')),
            new PhRecord(7.7, new Date('2019-01-01T07:00:00.000Z')),
            new PhRecord(7.8, new Date('2019-01-01T08:00:00.000Z')),
            new PhRecord(7.9, new Date('2019-01-01T09:00:00.000Z')),
            new PhRecord(8.0, new Date('2019-01-01T10:00:00.000Z')),
            new PhRecord(8.1, new Date('2019-01-01T11:00:00.000Z'))
        ])
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        return new TemperatureData(deviceId, [
            new TemperatureRecord(20.0, new Date('2019-01-01T00:00:00.000Z')),
            new TemperatureRecord(20.1, new Date('2019-01-01T01:00:00.000Z')),
            new TemperatureRecord(20.2, new Date('2019-01-01T02:00:00.000Z')),
            new TemperatureRecord(20.3, new Date('2019-01-01T03:00:00.000Z')),
            new TemperatureRecord(20.4, new Date('2019-01-01T04:00:00.000Z')),
            new TemperatureRecord(20.5, new Date('2019-01-01T05:00:00.000Z')),
            new TemperatureRecord(20.6, new Date('2019-01-01T06:00:00.000Z')),
            new TemperatureRecord(20.7, new Date('2019-01-01T07:00:00.000Z')),
            new TemperatureRecord(20.8, new Date('2019-01-01T08:00:00.000Z')),
            new TemperatureRecord(20.9, new Date('2019-01-01T09:00:00.000Z')),
            new TemperatureRecord(21.0, new Date('2019-01-01T10:00:00.000Z')),
            new TemperatureRecord(21.1, new Date('2019-01-01T11:00:00.000Z'))
        ])
    }
}