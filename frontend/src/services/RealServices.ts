import {doFetch, fetchRequest, ResponseType, toBody} from "./fetch";
import {Device, PhData, TemperatureData, toDevice, toDevices, toPhData, toTemperatureData, User} from "./domain";
import {Services} from "./services";
import {Siren, SirenModule} from "./sirenModule";
import {Logger} from "tslog";


const logger = new Logger({name: "Real Services"});
logger.settings.minLevel = 3 // LogLevel: INFO

export class RealServices implements Services {
    async getBackendSirenInfo() {
        function extractSirenInfo(response: Siren) {
            SirenModule.extractGoogleLoginLink(response.links)
            SirenModule.extractCreateUserAction(response.actions)
            SirenModule.extractCreateTokenAction(response.actions)
            SirenModule.extractLogoutAction(response.actions)
            SirenModule.extractIsLoggedInLink(response.links)
            SirenModule.extractGetMeLink(response.links)
            SirenModule.extractAddDeviceAction(response.actions)
            SirenModule.extractGetDevicesLink(response.links)
            SirenModule.extractGetDeviceLink(response.links)
            SirenModule.extractGetPhDataLink(response.links)
            SirenModule.extractGetTemperatureDataLink(response.links)
        }

        const request = {
            url: `siren-info`,
            method: 'GET',
        }
        const response = await doFetch(request, ResponseType.Siren)
        extractSirenInfo(response)
    }

    async googleLogin(): Promise<void> {
        const googleLoginLink = SirenModule.getGoogleLoginLink()
        if (!googleLoginLink) {
            const msg = 'Google login link not found'
            logger.error(msg)
            throw new Error(msg)
        }
        const request = {
            url: googleLoginLink.href,
            method: 'GET'
        }
        try {
            await fetchRequest(request)
        } catch (e) {
            logger.error(`Failed to login with google: ${e}`)
        }
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
        await doFetch(request, ResponseType.Siren)
        logger.info(`User ${username} created`)
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
        await doFetch(request, ResponseType.Siren)
    }

    async isLoggedIn(): Promise<boolean> {
        const isLoggedInLink = SirenModule.getIsLoggedInLink()
        if (!isLoggedInLink) throw new Error('Is logged in link not found')
        const request = {
            url: isLoggedInLink.href,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.isLoggedIn
    }

    async getMe(): Promise<User> {
        const getMeLink = SirenModule.getGetMeLink()
        if (!getMeLink) throw new Error('Get me link not found')
        const request = {
            url: getMeLink.href,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        const userFromResponse = response.properties
        return new User(userFromResponse.username, userFromResponse.password)
    }

    async createDevice(ownerEmail: string): Promise<string> {
        const addDeviceAction = SirenModule.getAddDeviceAction()
        if (!addDeviceAction) throw new Error('Add device action not found')
        const request = {
            url: addDeviceAction.href,
            method: addDeviceAction.method,
            body: toBody({email: ownerEmail})
        }
        const response = await doFetch(request, ResponseType.Siren)
        const deviceId = response.properties.deviceId

        console.log(`Device added with id ${deviceId}`)
        if (deviceId) return deviceId
        else throw new Error(`Device added, but no device id found`)
    }

    async getDevices(): Promise<Device[]> {
        const getDevicesLink = SirenModule.getGetDevicesLink()
        if (!getDevicesLink) throw new Error('Get devices link not found')
        const request = {
            url: getDevicesLink.href,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toDevices(response.properties)
    }

    async getDevice(deviceId: string): Promise<Device> {
        const getDeviceLink = SirenModule.getGetDeviceLink()
        if (!getDeviceLink) throw new Error('Get devices link not found')

        const urlWithId = getDeviceLink.href.replace(':device_id', deviceId)
        const request = {
            url: urlWithId,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toDevice(response.properties)
    }

    async getPhData(deviceId: string): Promise<PhData> {
        const getPhDataLink = SirenModule.getGetPhDataLink()
        if (!getPhDataLink) throw new Error('Get ph data link not found')

        const urlWithId = getPhDataLink.href.replace(':device_id', deviceId)
        const request = {
            url: urlWithId,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toPhData(response.properties)
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        const getTemperatureDataLink = SirenModule.getGetTemperatureDataLink()
        if (!getTemperatureDataLink) throw new Error('Get temperature data link not found')

        const urlWithId = getTemperatureDataLink.href.replace(':device_id', deviceId)
        const request = {
            url: urlWithId,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toTemperatureData(response.properties)
    }

    async logout(): Promise<void> {
        const logoutAction = SirenModule.getLogoutAction()
        if (!logoutAction) throw new Error('Logout action not found')
        const request = {
            url: logoutAction.href,
            method: logoutAction.method
        }
        await doFetch(request, ResponseType.Any)
    }
}