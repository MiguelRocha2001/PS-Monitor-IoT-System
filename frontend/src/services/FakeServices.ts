import {Device, SensorData, SensorRecord, User} from "./domain";
import {Services} from "./services";

export class FakeServices implements Services {
    private readonly users: User[] = [
        new User('admin@gmail.com', 'admin'),
    ]
    private user: User | null = null

    private readonly email = 'some_email_1@gmail.com'
    private readonly devices: Device[] = []
    private readonly sensors: string[] = ['ph', 'temperature']

    constructor() {
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
        this.devices.push(new Device(this.getNewDeviceId(), this.email))
    }

    async getBackendApiInfo() {
        // Nothing to do
    }

    async googleLogin(): Promise<void> {
        console.log('FakeServices.googleLogin')
        throw new Error('Dont call this method in fake mode')
    }

    checkIfUserExists(email: string): Promise<boolean> {
        if(!email) return Promise.resolve(false)
        return Promise.resolve(this.users.find(u => u.email === email) !== undefined)
    }

    async createUser(password: string, email: string): Promise<void> {
        const existingUser = this.users.find(u => u.email === email)
        if (existingUser) {
            throw new Error('Username already exists')
        }
        const newUser = new User(email, password)
        this.users.push(newUser)
        this.user = newUser
    }

    async authenticateUser(email: string, password: string): Promise<void> {
        const user = this.users.find(u => u.email === email && u.password === password)
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

    async getDevices(page: number, limit: number): Promise<Device[]> {
        const start = (page - 1) * limit
        const end = start + limit
        return this.devices.slice(start, end)
    }

    async getDevicesByName(page: number, limit: number, name: string): Promise<Device[]> {
        const start = (page - 1) * limit
        const end = start + limit
        return this.devices.filter(d => d.id.includes(name)).slice(start, end)
    }

    getDeviceCountByName(s: string): Promise<number> {
        return this.getDevicesByName(1, 1000, s).then(devices => devices.length)
    }

    async getDeviceCount(): Promise<number> {
        return this.devices.length
    }

    async getDevice(deviceId: string): Promise<Device> {
        const device = this.devices.find(d => d.id === deviceId)
        if (device) {
            return device
        } else {
            throw new Error(`Device ${deviceId} not found`)
        }
    }
/* one record per day every day for 5 years
    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        const records: TemperatureRecord[] = [];
        const startDate = new Date('2018-01-01T00:00:00.000Z');
        const endDate = new Date('2023-01-01T00:00:00.000Z');
        const numDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
        const recordsPerDay = 1;
        const temperatureIncreasePerDay = 0.02; // Increase in temperature per day
        let temperature = 20;

        for (let i = 0; i < numDays; i++) {
            const currentDate = new Date(startDate.getTime() + i * (1000 * 60 * 60 * 24));
            for (let j = 0; j < recordsPerDay; j++) {
                records.push(new TemperatureRecord(temperature, currentDate));
            }
            temperature += temperatureIncreasePerDay;
        }

        return new TemperatureData(records);
    }

    async  getPhData(deviceId: string): Promise<PhData> {
        const records: PhRecord[] = [];
        const startDate = new Date('2018-01-01T00:00:00.000Z');
        const endDate = new Date('2023-01-01T00:00:00.000Z');
        const numDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
        const recordsPerDay = 1;
        const phIncreasePerDay = 0.002; // Increase in pH per day
        let pH = 7;

        for (let i = 0; i < numDays; i++) {
            const currentDate = new Date(startDate.getTime() + i * (1000 * 60 * 60 * 24));
            for (let j = 0; j < recordsPerDay; j++) {
                records.push(new PhRecord(pH, currentDate));
            }
            if (i < numDays / 2) {
                pH += phIncreasePerDay;
            } else {
                pH -= phIncreasePerDay;
            }
        }

        return new PhData(records);
    }

*/
//2023 occurences
    async getSensorData(deviceId: string, sensor: string): Promise<SensorData> {
        if (this.sensors.find(s => s === sensor) === undefined)
            throw new Error('Sensor not found')
        const data: SensorRecord[] = [];
        let current = new Date();
        for (let i = 0; i < 50000; i++) {
            const value = (sensor === "ph") ? Math.random() * 14 : Math.random() * 10 + 20;
            data.push(new SensorRecord(value, new Date(current)));
            current = new Date(current.getTime() - 1000 * 60 * 60); // subtract 1 hour
        }
        return new SensorData(sensor, data);
    }

    async verifyCode(email: string, code: string): Promise<boolean> {
        return code === '12345'
    }

    async sendValidationCode(email: string): Promise<string> {
        return '12345'
    }

    async availableSensors(): Promise<string[]> {
        return this.sensors
    }

}