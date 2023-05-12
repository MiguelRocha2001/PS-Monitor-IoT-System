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
            SirenModule.extractGetDeviceCountLink(response.links)
            SirenModule.extractGetDeviceLink(response.links)
            SirenModule.extractGetSensorDataLink(response.links)
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
    async createUser(password: string, email: string): Promise<void> {
        logger.info(`Creating user ${email}`)
        const createUserAction = SirenModule.getCreateUserAction()
        if (!createUserAction) {
            const msg = 'Create user action not found'
            logger.error(msg)
            throw new Error(msg)
        }
        const request = {
            url: createUserAction.href,
            method: createUserAction.method,
            body: toBody({password, email})
        }
        await doFetch(request, ResponseType.Siren)
        logger.info(`User ${email} created`)
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

    async getDevices(page: number, limit: number): Promise<Device[]> {
        const getDevicesLink = SirenModule.getGetDevicesLink().href
        const getDeviceLinkAfterParams = getDevicesLink + '?page=' + page + '&limit=' + limit
        if (!getDevicesLink) throw new Error('Get devices link not found')
        const request = {
            url: getDeviceLinkAfterParams,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toDevices(response.properties)
    }

    getDevicesByName(page: number, limit: number, name: string): Promise<Device[]> {
      throw new Error("Method not implemented.");//todo
    }

    getDeviceCountByName(s: string): Promise<number> {
        throw new Error("Method not implemented.");//todo
    }

    checkIfUserExists(email: string): Promise<boolean> {
        throw new Error("Method not implemented.");//todo
    }

    verifyCode(code: string): Promise<boolean> {
        throw new Error("Method not implemented.");//todo
    }

    async getDeviceCount(): Promise<number> {
        const getDeviceCountLink = SirenModule.getGetDeviceCountLink()
        if (!getDeviceCountLink) throw new Error('Get devices link not found')
        const request = {
            url: getDeviceCountLink.href,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.deviceCount
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
        const getSensorDataLink = SirenModule.getGetSensorDataLink()
        if (!getSensorDataLink) throw new Error('Get sensor data link not found')

        const urlWithId = getSensorDataLink.href.replace(':device_id', deviceId)
        const urlWithIdAndSensorName = urlWithId + '?sensor-name=ph'
        const request = {
            url: urlWithIdAndSensorName,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toPhData(response.properties)
    }

    async getTemperatureData(deviceId: string): Promise<TemperatureData> {
        const getSensorDataLink = SirenModule.getGetSensorDataLink()
        if (!getSensorDataLink) throw new Error('Get sensor data link not found')

        const urlWithId = getSensorDataLink.href.replace(':device_id', deviceId)
        const urlWithIdAndSensorName = urlWithId + '?sensor-name=temperature'
        const request = {
            url: urlWithIdAndSensorName,
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