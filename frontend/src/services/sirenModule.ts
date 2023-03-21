import {Logger} from "tslog";
import exp from "constants";

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

let CREATE_USER_ACTION: Action
let CREATE_TOKEN_ACTION: Action
let LOGOUT_ACTION: Action

function extractCreateUserAction(actions: any[]) {
    CREATE_USER_ACTION = extractAction(actions, "create-user")
}

function extractCreateTokenAction(actions: any[]) {
    CREATE_TOKEN_ACTION = extractAction(actions, "create-token")
}

function extractLogoutAction(actions: any[]) {
    LOGOUT_ACTION = extractAction(actions, "logout")
}


function extractLink(linksArg: Link[], rel: string): string {
    for (let i = 0; i < linksArg.length; i++) {
        const link = linksArg[i]
        for (let j = 0; j < link.rel.length; j++) {
            if (link.rel[j] === rel) {
                return link.href
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
    extractCreateUserAction,
    extractCreateTokenAction,
    extractLogoutAction,
    validateFields
}