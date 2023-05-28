import {Device, SensorData, SensorRecord, User} from "./domain";
import {Services} from "./services";

class UserInternal {
    constructor(
        public user: User,
        public password: string
    ) {}
}

export class FakeServices implements Services {
    private readonly users: UserInternal[] = [
        new UserInternal(new User("1", 'admin@gmail.com', 'admin'), 'admin'),
    ]
    private user: User | null = null

    private readonly email = 'some_email_1@gmail.com'
    private readonly devices: Map<User, Device> = new Map()
    private readonly sensors: string[] = ['ph', 'temperature']

    constructor() {
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
        this.devices.set(this.users[0].user, new Device(this.getNewDeviceId(), this.email))
    }

    async getBackendApiInfo() {
        // Nothing to do
    }

    async googleLogin(): Promise<void> {
        this.user = this.users[0].user
    }

    checkIfUserExists(email: string): Promise<boolean> {
        if(!email) return Promise.resolve(false)
        return Promise.resolve(this.users.find(u => u.user.email === email) !== undefined)
    }

    async createUser(password: string, email: string): Promise<void> {
        const existingUser = this.users.find(u => u.user.email === email)
        if (existingUser) {
            throw new Error('Username already exists')
        }
        const newUser = new UserInternal(new User(this.getNewDeviceId(), email, email), password)
        this.users.push(newUser)
        this.user = newUser.user
    }

    async authenticateUser(email: string, password: string): Promise<void> {
        const user = this.users.find(u => u.user.email === email && u.password === password)
        if (user) {
            this.user = user.user
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
            const id = generateDeviceId()
            if (!Array.from(this.devices.values()).find(d => d.id === id)) {
                return id
            }
        }
    }

    async createDevice(ownerEmail: string): Promise<string> {
        if (this.user) {
            const deviceId = this.getNewDeviceId()
            const device = new Device(deviceId, ownerEmail)
            this.devices.set(this.user, device)
            return deviceId
        }
        throw new Error('Not logged in')
    }

    async getMyDevices(page: number, limit: number): Promise<Device[]> {
        if (this.user) {
            const start = (page - 1) * limit
            const end = start + limit
            return Array.from(this.devices.values()).slice(start, end)
        }
        throw new Error('Not logged in')
    }

    async getUsers(page: number, limit: number): Promise<User[]> {
        const start = (page - 1) * limit
        const end = start + limit
        return this.users.map(u => u.user).slice(start, end)
    }

    async getDevicesByName(page: number, limit: number, name: string): Promise<Device[]> {
        const start = (page - 1) * limit
        const end = start + limit
        return Array.from(this.devices.values()).filter(d => d.email.includes(name)).slice(start, end)
    }

    async getUsersByName(page: number, limit: number, name: string): Promise<User[]> {
        const start = (page - 1) * limit
        const end = start + limit
        return this.users.map(u => u.user).filter(u => u.email.includes(name)).slice(start, end)
    }

    getDeviceCountByName(s: string): Promise<number> {
        return this.getDevicesByName(1, 1000, s).then(devices => devices.length)
    }

    getUserCountByName(s: string): Promise<number> {
        return this.getUsersByName(1, 1000, s).then(users => users.length)
    }

    async getMyDeviceCount(): Promise<number> {
        if (this.user) {
            return this.devices.size
        }
        throw new Error('Not logged in')
    }

    async getUserCount(): Promise<number> {
        return this.users.length
    }

    async getDevice(deviceId: string): Promise<Device> {
        this.devices.forEach((value, key) => {
            if (key === this.user && value.id === deviceId) {
                return value
            }
        })
        throw new Error('Device not found')
    }

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