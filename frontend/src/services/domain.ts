export class Device {
    constructor(public id: number) {}
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
    if (typeof id !== 'number') {
        throw new Error(`Invalid id: ${id}`)
    }
    return new Device(id)
}