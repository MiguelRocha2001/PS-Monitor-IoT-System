import {Logger} from "tslog";

/**
 * This module implements the Siren specification.
 * It also functions as a Repo for Siren information (Links and Actions).
 *
 *
 */

const logger = new Logger({ name: "Siren" });

export class Siren {
    constructor(
        public class_: string,
        public properties: any,
        public links: Link[],
        public entities: Entity[],
        public actions: Action[]
    ) {}
}

export class Link {
    constructor(
        public rel: string[],
        public href: string,
        public title?: string,
        public type?: string
    ) {}
}

export class Action {
    constructor(
        public name: string,
        public title: string,
        public method: string,
        public href: string,
        public type: string,
        public fields: Field[]
    ) {}
}

export class Field {
    constructor(
        public name: string,
        public type: string,
        public value: string
    ) {}
}

export class Entity {
    constructor(
        public class_: string[],
        public properties: any,
        public entities: Entity[],
        public links: Link[],
        public actions: Action[],
        public title: string
    ) {}
}

export function fromJson(json: any): Siren {
    return new Siren(
        json.class,
        json.properties,
        json.links,
        json.entities,
        json.actions
    );
}

let GOOGLE_LOGIN_LINK: Link
let CREATE_USER_ACTION: Action
let CREATE_TOKEN_ACTION: Action
let LOGOUT_ACTION: Action
let IS_LOGGED_IN_LINK: Link
let GET_ME_LINK: Link
let ADD_DEVICE_ACTION: Action
let GET_DEVICES_LINK: Link
let GET_DEVICE_COUNT_LINK: Link
let GET_DEVICE_LINK: Link
let GET_SENSOR_DATA_LINK: Link
let GET_DEVICES_BY_NAME_LINK: Link


function getGoogleLoginLink(): Link {
    return GOOGLE_LOGIN_LINK
}

function getCreateUserAction(): Action {
    return CREATE_USER_ACTION
}

function getCreateTokenAction(): Action {
    return CREATE_TOKEN_ACTION
}

function getLogoutAction(): Action {
    return LOGOUT_ACTION
}

function getIsLoggedInLink(): Link {
    return IS_LOGGED_IN_LINK
}

function getGetMeLink(): Link {
    return GET_ME_LINK
}

function getAddDeviceAction(): Action {
    return ADD_DEVICE_ACTION
}

function getGetDevicesLink(): Link {
    return GET_DEVICES_LINK
}

function getGetDeviceCountLink(): Link {
    return GET_DEVICE_COUNT_LINK
}

function getGetDeviceLink(): Link {
    return GET_DEVICE_LINK
}

function getGetSensorDataLink(): Link {
    return GET_SENSOR_DATA_LINK
}


function extractGoogleLoginLink(links: Link[]) {
    GOOGLE_LOGIN_LINK = extractLink(links, "google-login")
}

function extractCreateUserAction(actions: any[]) {
    CREATE_USER_ACTION = extractAction(actions, "create-user")
}

function extractCreateTokenAction(actions: any[]) {
    CREATE_TOKEN_ACTION = extractAction(actions, "login")
}

function extractLogoutAction(actions: any[]) {
    LOGOUT_ACTION = extractAction(actions, "logout")
}

function extractIsLoggedInLink(links: Link[]) {
    IS_LOGGED_IN_LINK = extractLink(links, "is-logged-in")
}

function extractGetMeLink(links: Link[]) {
     GET_ME_LINK = extractLink(links, "users-me")
}

function extractAddDeviceAction(actions: any[]) {
    ADD_DEVICE_ACTION = extractAction(actions, "create-device")
}

function extractGetDevicesLink(links: Link[]) {
    GET_DEVICES_LINK = extractLink(links, "devices")
}

function extractGetDeviceCountLink(links: Link[]) {
    GET_DEVICE_COUNT_LINK = extractLink(links, "device-count")
}

function extractGetDeviceLink(links: Link[]) {
    GET_DEVICE_LINK = extractLink(links, "device-by-id")
}

function extractGetSensorDataLink(links: Link[]) {
    GET_SENSOR_DATA_LINK = extractLink(links, "sensor-data")
}

function getDevicesByNameLink(links: Link[]): Link {
    return GET_DEVICES_BY_NAME_LINK = extractLink(links, "devices-by-name")
}

function extractLink(linksArg: Link[], rel: string): Link {
    for (let i = 0; i < linksArg.length; i++) {
        const link = linksArg[i]
        for (let j = 0; j < link.rel.length; j++) {
            if (link.rel[j] === rel) {
                return link
            }
        }
    }
    throw new Error("extractLink: link not found: " + rel)
}

function extractAction(actions: any[], name: string): Action {
    for (let i = 0; i < actions.length; i++) {
        const action = actions[i]
        if (action.name === name) {
            return action
        }
    }
    throw new Error("extractAction: action not found: " + name)
}

/**
 * Validates if all necessary fields, in [action], are present in [fields].
 */
 function validateFields(obj: any, action: Action): boolean {
    const keys = Object.keys(obj)
    for (let i = 0; i < action.fields.length; i++) {
        const field = action.fields[i]
        if (!keys.includes(field.name)) {
            logger.error("validateFields: missing required field: ", field.name)
            return false
        }
    }
    return true
}



export const SirenModule = {
    getGoogleLoginLink,
    extractGoogleLoginLink,
    getCreateTokenAction,
    getCreateUserAction,
    getLogoutAction,
    getIsLoggedInLink,
    extractCreateUserAction,
    extractCreateTokenAction,
    extractLogoutAction,
    extractIsLoggedInLink,
    extractGetMeLink,
    extractAddDeviceAction,
    getGetMeLink,
    getAddDeviceAction,
    getGetDevicesLink,
    getGetDeviceCountLink,
    extractGetDeviceCountLink,
    extractGetDevicesLink,
    getGetDeviceLink,
    extractGetDeviceLink,
    getGetSensorDataLink,
    extractGetSensorDataLink,
    validateFields,
    getDevicesByNameLink,
}