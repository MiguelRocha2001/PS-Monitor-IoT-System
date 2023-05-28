import {doFetch, ResponseType, toBody} from "./fetch";
import {Device, SensorData, toDevice, toDevices, toSensorData, User} from "./domain";
import {Services} from "./services";
import {Siren, SirenModule} from "./sirenModule";
import {Logger} from "tslog";


const logger = new Logger({name: "Real Services"});
logger.settings.minLevel = 3 // LogLevel: INFO

export class RealServices implements Services {
    async getBackendApiInfo() {
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
            SirenModule.extractGetIsEmailAlreadyRegisteredLink(response.links)
            SirenModule.extractGetVerificationCodeAction(response.actions)
            SirenModule.extractGetVerifyCodeLink(response.links)
            SirenModule.extractGetDevicesByIDLink(response.links)
            SirenModule.extractCountDevicesByIDLink(response.links)
            SirenModule.extractAvailableDeviceSensorsLink(response.links)
        }

        const request = {
            url: `siren-info`,
            method: 'GET',
        }
        const response = await doFetch(request, ResponseType.Siren)
        extractSirenInfo(response)
    }

    async googleLogin(): Promise<void> {
        window.location.href = "http://localhost:9000/oidc-principal"
        /*
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
         */
    }

     generateRandomString(length: number): string {
        const allowedChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'; // Define the characters allowed in the random string
        let randomString = '';
        for (let i = 0; i < length; i++) {
            const randomIndex = Math.floor(Math.random() * allowedChars.length);
            randomString += allowedChars.charAt(randomIndex);
        }
        return randomString;
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
        const username:string = this.generateRandomString(8)
        const request = {
            url: createUserAction.href,
            method: createUserAction.method,
            body: toBody({username,password, email})
        }
        await doFetch(request, ResponseType.Siren)
        logger.info(`User ${email} created`)
    }

    async authenticateUser(email: string, password: string): Promise<void> {
        logger.info(`Creating user ${email}`)
        const action = SirenModule.getCreateTokenAction()
        const request = {
            url: action.href,
            method: action.method,
            body: toBody({email, password})
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
        return new User(userFromResponse.id, userFromResponse.email, userFromResponse.role)
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

    async getDevices(
        userId: string,
        page: number,
        limit: number,
        email: string | undefined,
        deviceIdChunk: string | undefined
    ): Promise<Device[]> {
        const getDevicesLink = SirenModule.getGetDevicesLink().href
        let link = getDevicesLink.replace(':userId', userId) + '?page=' + page + '&limit=' + limit
        if (email) {
            link = link.concat('&email=' + email)
        } else if (deviceIdChunk) {
            link = link.concat('&deviceIdChunk=' + deviceIdChunk)
        }
        if (!getDevicesLink) throw new Error('Get devices link not found')
        const request = {
            url: link,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toDevices(response.properties)
    }

    async checkIfUserExists(email: string): Promise<boolean> {
        const isEmailAlreadyRegisteredLink = SirenModule.getIsEmailAlreadyRegisteredLink()
        if (!isEmailAlreadyRegisteredLink) throw new Error('Is email already registered link not found')
        const linkWithEmail = isEmailAlreadyRegisteredLink.href.replace(':email', email)
        const request = {
            url: linkWithEmail,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.exists
    }

    async verifyCode(email:string, code: string): Promise<boolean> {
        const verifyCodeLink = SirenModule.getVerifyCodeLink()
        if (!verifyCodeLink) throw new Error('Verify code link not found')
        const linkWithEmail = verifyCodeLink + "?email=${email}&code=${code}"
        const request = {
            url: linkWithEmail,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.valid
    }

    async sendValidationCode(email: string): Promise<string> {
        const emailCode = SirenModule.getAddAndSendEmailCode()
        if (!emailCode) throw new Error('Is email already registered link not found')
        const request = {
            url: emailCode.href,
            method: emailCode.method,
            body: toBody({email: email})
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.code
    }

    async getUserDeviceCount(userId: string): Promise<number> {
        const getDeviceCountLink = SirenModule.getGetDeviceCountLink()
        if (!getDeviceCountLink) throw new Error('Get devices link not found')
        const afterReplace = getDeviceCountLink.href.replace(':userId', userId)
        const request = {
            url: afterReplace,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.deviceCount
    }

    async getDeviceById(deviceId: string): Promise<Device> {
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

    async getSensorData(deviceId: string, sensor: string): Promise<SensorData> {
        const getSensorDataLink = SirenModule.getGetSensorDataLink()
        if (!getSensorDataLink) throw new Error('Get sensor data link not found')

        const urlWithId = getSensorDataLink.href.replace(':device_id', deviceId)
        const urlWithIdAndSensorName = urlWithId + '?sensor-name=' + sensor
        const request = {
            url: urlWithIdAndSensorName,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return toSensorData(response.properties)
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

    async availableSensors(deviceId: string): Promise<string[]> {
        const availableDeviceSensorsLink = SirenModule.availableDeviceSensorsLink()
        if (!availableDeviceSensorsLink) throw new Error('Available device sensors link not found')
        const urlWithId = availableDeviceSensorsLink.href.replace(':device_id', deviceId)
        const request = {
            url: urlWithId,
            method: 'GET'
        }
        const response = await doFetch(request, ResponseType.Siren)
        return response.properties.types//.map((type: string) => type.replace('_', ' ')) // FIXME: not working
    }

    getUserCount(): Promise<number> {
        throw new Error("Method not implemented.");
    }

    getUserCountByName(s: string): Promise<number> {
        throw new Error("Method not implemented.");
    }

    getUsers(page: number, limit: number): Promise<User[]> {
        throw new Error("Method not implemented.");
    }

    getUsersByName(page: number, limit: number, name: string): Promise<User[]> {
        throw new Error("Method not implemented.");
    }
}