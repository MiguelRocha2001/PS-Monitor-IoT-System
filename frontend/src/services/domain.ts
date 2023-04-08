import {Runtime} from "inspector";

export class User {
    constructor(
        public username: string,
        public password: string
    ) {}
}

export class Device {
    constructor(public id: string, public email: string) {}
}

export function toDevices(json: any): Device[] {
    const devices = json.devices
    if (!Array.isArray(devices)) {
        throw new Error(`Invalid devices: ${devices}`)
    }
    return devices.map(toDevice)
}

export function toDevice(propertiesJson: any): Device {
    const id = propertiesJson.id
    if (typeof id !== 'string') {
        throw new Error(`Invalid id: ${id}`)
    }
    return new Device(id, propertiesJson.email)
}

export class PhRecord {
    constructor(
        public value: number,
        public date: Date
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
    const date = new Date(timestamp * 1000)
    return new PhRecord(value, date)
}

export class PhData {
    constructor(
        public records: PhRecord[]
    ) {}
}

export function toPhData(json: any): PhData {
    console.log(`toPhData: ${JSON.stringify(json)}`)
    const records = json.records
    if (!Array.isArray(records)) {
        throw new Error(`Invalid records: ${records}`)
    }
    return new PhData(records.map(toPhRecord))
}

export class TemperatureRecord {
    constructor(
        public value: number,
        public date: Date
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
    const date = new Date(timestamp)
    return new TemperatureRecord(value, date)
}

export class TemperatureData {
    constructor(
        public records: TemperatureRecord[]
    ) {}
}

export function toTemperatureData(json: any): TemperatureData {
    const records = json.records
    if (!Array.isArray(records)) {
        throw new Error(`Invalid records: ${records}`)
    }
    return new TemperatureData(records.map(toTemperatureRecord))
}