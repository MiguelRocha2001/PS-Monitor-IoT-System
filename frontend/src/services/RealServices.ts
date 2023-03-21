import {doFetch, toBody} from "../fetch";
import {Device, PhData, TemperatureData, toDevices, toPhData, toTemperatureData, User} from "./domain";
import {deviceAdded, Services} from "./services";
import {Siren, SirenModule} from "./sirenModule";
import {ServerError} from "./erros";

export class RealServices implements Services {
    private readonly API_URL = 'http://localhost:8080/siren-info'

    async getBackendSirenInfo() {
        function extractSirenInfo(response: Siren) {
            SirenModule.extractCreateUserAction(response.actions)
            SirenModule.extractCreateTokenAction(response.actions)
            SirenModule.extractLogoutAction(response.actions)
        }

        const request = {
            url: `${(this.API_URL)}/`,
            method: 'GET',
        }
        const response = await doFetch(request)
        if (response instanceof Siren)
            extractSirenInfo(response)
        else if (response instanceof ServerError)
            throw new Error(`Failed to get backend siren info: ${response.status} ${response.message}`)
        throw new Error(`Failed to get backend siren info: ${response}`)
    }

    async createUser(username: string, password: string) {
        const request = {
            url: `${(this.API_URL)}/users`,
            method: 'POST',
            body: toBody({username, password})
        }
        const response = await doFetch(request)
        if (response instanceof Siren) return
        else if (response instanceof ServerError)
            throw new Error(`Failed to create user: ${response.status} ${response.message}`)
        throw new Error(`Failed to create user: ${response}`)
    }

    async authenticateUser(username: string, password: string) {
        const request = {
            url: `${(this.API_URL)}/users/authenticate`,
            method: 'POST',
            body: toBody({username, password})
        }
        const response = await doFetch(request)
        if (response instanceof Siren) return
        else if (response instanceof ServerError)
            throw new Error(`Failed to authenticate user: ${response.status} ${response.message}`)
        throw new Error(`Failed to authenticate user: ${response}`)
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
        const request = {
            url: `${(this.API_URL)}/devices`,
            method: 'POST',
            body: toBody(device)
        }
        const response = await doFetch(request)
        if (response instanceof ServerError)
            throw new Error(`Failed to add device: ${response.status} ${response.message}`)
        else {
            return deviceAdded(device)
        }
    }

    async getDevices(): Promise<Device[]> {
        const request = {
            url: `${(this.API_URL)}/devices`,
            method: 'GET',
        }
        const response = await doFetch(request)
        if (response instanceof ServerError)
            throw new Error(`Failed to get devices: ${response.status} ${response.message}`)
        else
            return toDevices(response)
    }

    async getPhData(deviceId: string): Promise<PhData> {
        const request = {
            url: `${(this.API_URL)}/devices/${deviceId}/ph`,
            method: 'GET'
        }
        const response = await doFetch(request)
        if (response instanceof ServerError)
            throw new Error(`Failed to get ph data: ${response.status} ${response.message}`)
        else
            return toPhData(response)
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        const request = {
            url: `${(this.API_URL)}/devices/${deviceId}/temperature`,
            method: 'GET'
        }
        const response = await doFetch(request)
        if (response instanceof ServerError)
            throw new Error(`Failed to get temperature data: ${response.status} ${response.message}`)
        else
            return toTemperatureData(response)
    }

    logout(): Promise<void> {
        return Promise.resolve(undefined);
    }
}