import {Device, PhData, PhRecord, TemperatureData, TemperatureRecord, User} from "./domain";
import {Services} from "./services";

export class FakeServices implements Services {
    private readonly users: User[] = []
    private user: User | null = null

    private readonly email = 'some_email_1@gmail.com'
    private readonly devices: Device[] = []

    constructor() {
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
    }

    async getBackendSirenInfo() {
        // Nothing to do
    }

    async googleLogin(): Promise<void> {
        throw new Error('Dont call this method in fake mode')
    }

    async createUser(username: string, password: string, email: string, mobile: string): Promise<void> {
        const existingUser = this.users.find(u => u.username === username)
        if (existingUser) {
            throw new Error('Username already exists')
        }
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

    getNewDeviceId(): string {
        function generateDeviceId(): string {
            const alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
            const seed = new Date().getHours();
            let id = '';

            // Generate a 5-character string using the alphabet
            for (let i = 0; i < 8; i++) {
                const randomIndex = Math.floor(Math.random() * alphabet.length);
                id += alphabet[randomIndex];
            }

            // Add the seed value to the end of the string
            id += seed.toString();

            return id;
        }

        while (true) { // loops while there is a device with the same id
            const deviceId = generateDeviceId()
            if (!this.devices.find(d => d.id === deviceId)) {
                return deviceId
            }
        }
    }

    async createDevice(ownerEmail: string): Promise<string> {
        const deviceId = this.getNewDeviceId()
        const device = new Device(deviceId, ownerEmail)
        this.devices.push(device)
        return deviceId
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
        return new PhData([
            new PhRecord(7.0, new Date('2019-01-01T00:00:00.000Z')),
            new PhRecord(7.1, new Date('2019-01-02T01:00:00.000Z')),
            new PhRecord(7.2, new Date('2019-01-03T02:00:00.000Z')),
            new PhRecord(7.3, new Date('2019-01-04T03:00:00.000Z')),
            new PhRecord(7.4, new Date('2019-01-05T04:00:00.000Z')),
            new PhRecord(7.5, new Date('2019-01-06T05:00:00.000Z')),
            new PhRecord(7.6, new Date('2019-01-07T06:00:00.000Z')),
            new PhRecord(7.7, new Date('2019-01-08T07:00:00.000Z')),
            new PhRecord(7.8, new Date('2019-01-09T08:00:00.000Z')),
            new PhRecord(7.9, new Date('2019-01-10T09:00:00.000Z')),
            new PhRecord(8.0, new Date('2019-01-11T10:00:00.000Z')),
            new PhRecord(8.1, new Date('2019-01-12T11:00:00.000Z'))
        ])
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        return new TemperatureData( [
            new TemperatureRecord(20.0, new Date('2019-01-01T00:00:00.000Z')),
            new TemperatureRecord(20.1, new Date('2019-01-02T01:00:00.000Z')),
            new TemperatureRecord(20.2, new Date('2019-01-03T02:00:00.000Z')),
            new TemperatureRecord(20.3, new Date('2019-01-04T03:00:00.000Z')),
            new TemperatureRecord(20.4, new Date('2019-01-05T04:00:00.000Z')),
            new TemperatureRecord(20.5, new Date('2019-01-06T05:00:00.000Z')),
            new TemperatureRecord(20.6, new Date('2019-01-07T06:00:00.000Z')),
            new TemperatureRecord(20.7, new Date('2019-01-08T07:00:00.000Z')),
            new TemperatureRecord(20.8, new Date('2019-01-09T08:00:00.000Z')),
            new TemperatureRecord(20.9, new Date('2019-01-10T09:00:00.000Z')),
            new TemperatureRecord(21.0, new Date('2019-01-11T10:00:00.000Z')),
            new TemperatureRecord(21.1, new Date('2019-01-12T11:00:00.000Z'))
        ])
    }
}