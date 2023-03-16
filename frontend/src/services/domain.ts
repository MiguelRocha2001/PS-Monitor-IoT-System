export class User {
    constructor(
        public username: string,
        public password: string
    ) {}
}

export class Device {
    constructor(public id: string) {}
}

export function toDevices(json: any): Device[] {
    const devices = json.devices
    if (!Array.isArray(devices)) {
        throw new Error(`Invalid devices: ${devices}`)
    }
    return devices.map(toDevice)
}

function toDevice(json: any): Device {
    const id = json.id
    if (typeof id !== 'string') {
        throw new Error(`Invalid id: ${id}`)
    }
    return new Device(id)
}

export class PhRecord {
    constructor(
        public value: number,
        public timestamp: number
    ) {}
}

function toPhRecord(json: any): PhRecord {
    const value = json.value
    if (typeof value !== 'number') {
        throw new Error(`Invalid value: ${value}`)
    }
    const timestamp = json.timestamp
    if (typeof timestamp !== 'number') {
        throw new Error(`Invalid timestamp: ${timestamp}`)
    }
    return new PhRecord(value, timestamp)
}

export class PhData {
    constructor(
        public deviceId: string,
        public records: PhRecord[]
    ) {}
}

export function toPhData(json: any): PhData {
    const deviceId = json.deviceId
    if (typeof deviceId !== 'string') {
        throw new Error(`Invalid deviceId: ${deviceId}`)
    }
    const records = json.records
    if (!Array.isArray(records)) {
        throw new Error(`Invalid records: ${records}`)
    }
    return new PhData(deviceId, records.map(toPhRecord))
}

export class TemperatureRecord {
    constructor(
        public value: number,
        public timestamp: number
    ) {}
}

function toTemperatureRecord(json: any): TemperatureRecord {
    const value = json.value
    if (typeof value !== 'number') {
        throw new Error(`Invalid value: ${value}`)
    }
    const timestamp = json.timestamp
    if (typeof timestamp !== 'number') {
        throw new Error(`Invalid timestamp: ${timestamp}`)
    }
    return new TemperatureRecord(value, timestamp)
}

export class TemperatureData {
    constructor(
        public deviceId: string,
        public records: TemperatureRecord[]
    ) {}
}

export function toTemperatureData(json: any): TemperatureData {
    const deviceId = json.deviceId
    if (typeof deviceId !== 'string') {
        throw new Error(`Invalid deviceId: ${deviceId}`)
    }
    const records = json.records
    if (!Array.isArray(records)) {
        throw new Error(`Invalid records: ${records}`)
    }
    return new TemperatureData(deviceId, records.map(toTemperatureRecord))
}