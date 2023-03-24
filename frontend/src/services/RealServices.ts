import {doFetch, toBody} from "./fetch";
import {Device, PhData, TemperatureData, toDevices, toPhData, toTemperatureData, User} from "./domain";
import {deviceAdded, Services} from "./services";
import {BackendError} from "./erros";
import {Siren, SirenModule} from "./sirenModule";
import {Logger} from "tslog";


const logger = new Logger({name: "Real Services"});
logger.settings.minLevel = 3 // LogLevel: INFO

export class RealServices implements Services {
    private readonly API_HOST = 'http://localhost:8080'

    async getBackendSirenInfo() {
        function extractSirenInfo(response: Siren) {
            SirenModule.extractCreateUserAction(response.actions)
            SirenModule.extractCreateTokenAction(response.actions)
            SirenModule.extractLogoutAction(response.actions)
            SirenModule.extractIsLoggedInLink(response.links)
            SirenModule.extractGetMeLink(response.links)
        }

        const request = {
            url: `siren-info`,
            method: 'GET',
        }
        const response = await doFetch(request)
        if (response instanceof Siren) {
            console.log(response.actions[0].method)
            extractSirenInfo(response)
            return
        } else
            throw new Error(`Failed to get backend siren info: ${response.status} ${response.message}`)
    }

    /**
     * Creates a new user
     * @param username username
     * @param password password
     * @param email email
     * @param mobile mobile
     */
    async createUser(username: string, password: string, email: string, mobile: string): Promise<void> {
        logger.info(`Creating user ${username}`)
        const createUserAction = SirenModule.getCreateUserAction()
        if (!createUserAction) {
            const msg = 'Create user action not found'
            logger.error(msg)
            throw new Error(msg)
        }
        const request = {
            url: createUserAction.href,
            method: createUserAction.method,
            body: toBody({username, password, email, mobile})
        }
        console.log(createUserAction)
        try {
            const response = await doFetch(request)
            if (response instanceof Siren) {
                logger.info(`User ${username} created`)
                return
            }
        } catch (e) {
            logger.error(`Failed to create user: ${e}`)
        }
    }

    // TODO: fix this
    async authenticateUser(username: string, password: string) {
        logger.info(`Creating user ${username}`)
        const action = SirenModule.getCreateTokenAction()
        const request = {
            url: action.href,
            method: action.method,
            body: toBody({username, password})
        }
        const response = await doFetch(request)
        if (response instanceof Siren)
            return
        else
            throw new Error(`Failed to authenticate user: ${response.status} ${response.message}`)
    }

    async isLoggedIn(): Promise<boolean> {
        const isLoggedInLink = SirenModule.getIsLoggedInLink()
        if (!isLoggedInLink) throw new Error('Is logged in link not found')
        const request = {
            url: isLoggedInLink.href,
            method: 'GET'
        }
        const response = await doFetch(request)
        if (response instanceof Siren) {
            return response.properties.isLoggedIn
        }
        else if (response instanceof BackendError) {
            throw new Error(`Failed to check if user is logged in: ${response.status} ${response.message}`)
        }
        throw new Error(`Failed to check if user is logged in: ${response}`)
    }

    async getMe(): Promise<User> {
        const getMeLink = SirenModule.getGetMeLink()
        if (!getMeLink) throw new Error('Get me link not found')
        const request = {
            url: getMeLink.href,
            method: 'GET'
        }
        const response = await doFetch(request)
        if (response instanceof Siren) {
            const userFromResponse = response.properties
            return new User(userFromResponse.username, userFromResponse.password)
        }
        else if (response instanceof BackendError)
            throw new Error(`Failed to get me: ${response.status} ${response.message}`)
        throw new Error(`Failed to get me: ${response}`)
    }

    async addDevice(device: Device) {
        const request = {
            url: `${(this.API_HOST)}/devices`,
            method: 'POST',
            body: toBody(device)
        }
        const response = await doFetch(request)
        if (response instanceof BackendError)
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
        if (response instanceof BackendError)
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
        if (response instanceof BackendError)
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
        if (response instanceof BackendError)
            throw new Error(`Failed to get temperature data: ${response.status} ${response.message}`)
        else
            return toTemperatureData(response)
    }

    logout(): Promise<void> {
        return Promise.resolve(undefined);
    }
}