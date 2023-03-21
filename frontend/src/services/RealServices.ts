import {doFetch, toBody} from "./fetch";
import {Device, PhData, TemperatureData, toDevices, toPhData, toTemperatureData, User} from "./domain";
import {deviceAdded, Services} from "./services";
import {ServerError} from "./erros";
import {Siren, SirenModule} from "./sirenModule";
import {Logger} from "tslog";

const logger = new Logger({name: "Real Services"});
logger.settings.minLevel = 3 // LogLevel: INFO

export class RealServices implements Services {
    private readonly API_HOST = 'http://localhost:8080/'

    async getBackendSirenInfo() {
        function extractSirenInfo(response: Siren) {
            SirenModule.extractCreateUserAction(response.actions)
            SirenModule.extractCreateTokenAction(response.actions)
            SirenModule.extractLogoutAction(response.actions)
            SirenModule.extractIsLoggedInLink(response.links)
        }

        const request = {
            url: `siren-info`,
            method: 'GET',
        }
        const response = await doFetch(request)
        if (response instanceof Siren)
            extractSirenInfo(response)
        else if (response instanceof ServerError)
            throw new Error(`Failed to get backend siren info: ${response.status} ${response.message}`)
        throw new Error(`Failed to get backend siren info: ${response}`)
    }

    /**
     * Creates a new user
     * @param username username
     * @param password password
     */
    async createUser(username: string, password: string) {
        const createUserAction = SirenModule.getCreateUserAction()
        if (!createUserAction) throw new Error('Create user action not found')
        const request = {
            url: this.API_HOST + createUserAction.href,
            method: createUserAction.method,
            body: toBody({username, password})
        }
        const response = await doFetch(request)
        if (response instanceof Siren) {
            logger.info(`User ${username} created`)
            return
        }
        else if (response instanceof ServerError)
            throw new Error(`Failed to create user: ${response.status} ${response.message}`)
        throw new Error(`Failed to create user: ${response}`)
    }

    async authenticateUser(username: string, password: string) {
        const request = {
            url: `${(this.API_HOST)}/users/authenticate`,
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
        const isLoggedInLink = SirenModule.getIsLoggedInLink()
        if (!isLoggedInLink) throw new Error('Is logged in link not found')
        const request = {
            url: this.API_HOST + isLoggedInLink.href,
            method: 'GET'
        }
        const response = await doFetch(request)
        if (response instanceof Siren) {

        }
        else if (response instanceof ServerError)
            throw new Error(`Failed to check if user is logged in: ${response.status} ${response.message}`)
        throw new Error(`Failed to check if user is logged in: ${response}`)
    }

    async getMe(): Promise<User> {
        // TODO: Implement
        throw new Error('Not implemented')
    }

    async addDevice(device: Device) {
        const request = {
            url: `${(this.API_HOST)}/devices`,
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
            url: `${(this.API_HOST)}/devices`,
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
            url: `${(this.API_HOST)}/devices/${deviceId}/ph`,
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
            url: `${(this.API_HOST)}/devices/${deviceId}/temperature`,
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