export class User {
    constructor(
        public id: string,
        public email: string,
        public role: string
    ) {}
}

export class Device {
    constructor(public id: string, public alertEmail: string) {}
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

export class SensorRecord {
    constructor(
        public value: number,
        public date: Date
    ) {}
}

function toSensorRecord(json: any): SensorRecord {
    const value = json.value
    if (typeof value !== 'number') {
        throw new Error(`Invalid value: ${value}`)
    }
    const timestamp = json.timestamp
    if (typeof timestamp !== 'number') {
        throw new Error(`Invalid timestamp: ${timestamp}`)
    }
    const date = new Date(timestamp * 1000)
    return new SensorRecord(value, date)
}

export class SensorData {
    constructor(
        public type: string,
        public records: SensorRecord[]
    ) {}
}

export function toSensorData(json: any): SensorData {
    const records = json.records
    if (!Array.isArray(records)) {
        throw new Error(`Invalid records: ${records}`)
    }
    return new SensorData(
        json.type,
        records.map(toSensorRecord)
    )
}