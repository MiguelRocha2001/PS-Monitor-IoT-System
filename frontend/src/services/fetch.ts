import {BackendError, NetworkError} from "./erros";
import {Logger} from "tslog";
import {fromJson, Siren} from "./sirenModule";

const host = 'http://localhost:8080/api'
const CONTENT_TYPE_JSON = 'application/json'

const logger = new Logger({name: "Fetch Module"});
logger.settings.minLevel = 3 // LogLevel: INFO

export type Request = {
    url: string
    method: string
    body?: Body
}

type Body = KeyValuePair[]

export type KeyValuePair = {
    name: string,
    value: string
}

export async function fetchRequest(
    request: Request
): Promise<Response> {
    const headers: any = {
        'Content-Type': CONTENT_TYPE_JSON
    }
    try {
        return  await fetch(toFullUrl(request), {
            method: request.method,
            headers,
            credentials: 'include',
            body: request.body ? buildBody(request.body) : undefined
        })
    } catch (error: any) {
        logger.error("Network Error: ", error)
        return Promise.reject(new NetworkError(error.message))
    }
}

export enum ResponseType {Siren, Any}

/**
 * Makes an API call.
 * @param request Request object containing url, method and body.
 * The url is relative to the API host (host should not be included).
 * @param responseType Expected response format. Default is Siren.
 * @returns Promise of the response. This could be a Siren object or any other object.
 * @returns If the status is 204 (No Content) then the response is null.
 * @returns Promise.reject if the response is not ok.
 */
export async function doFetch(
    request: Request,
    responseType: ResponseType
): Promise<Siren | any | null> {
    if (request && validateRequestMethod(request)) {
        logger.info("sending request to: ", toFullUrl(request))
        // console.log("body: ", request.body ? buildBody(request.body) : undefined)
        const resp = await fetchRequest(request)

        if (resp.status === 204 && responseType === ResponseType.Any) {
            return null
        }

        const data = await getSirenOrProblemOrAny(resp)

        // if response is not ok
        if (!resp.ok) {
            if (data instanceof ProblemJson) {
                logger.error("Response Error: ", data.title)
                // return new BackendError(data.title, resp.statusCode)
                return Promise.reject(new Error(data.title))
            } else {
                logger.error("Response Error: ", resp)
                return Promise.reject(new Error("Unknown error: " + resp.status + " " + resp.statusText))
            }
        }

        // if expected format is not verified
        if (responseType === ResponseType.Siren && !(data instanceof Siren)) {
            throw new Error(`Expected Siren response, got ${typeof data}`)
        } else if (responseType === ResponseType.Any && data instanceof Siren) {
            throw new Error(`Expected any response, got Siren`)
        }

        return data
    }
    return Promise.reject(new Error('Invalid request'))
}

function toFullUrl(request: Request): string {
    return request.url[0] === '/' ? host + request.url : host + '/' + request.url
}

export class ProblemJson {
    title: string
    status: number
    detail: string

    constructor(title: string, status: number, detail: string) {
        this.title = title
        this.status = status
        this.detail = detail
    }
}

export function toBody(obj: any): Body {
    const body: Body = []
    for (const key in obj) {
        body.push({ name: key, value: obj[key] })
    }
    return body
}

/**
 *
 * @param response
 * @throws Error if response status code is 204 (No Content)
 */
export async function getSirenOrProblemOrAny(response: Response): Promise<Siren | any | ProblemJson> {
    if (response.status === 204) throw new Error('No Content')
    if (response.ok) {
        const isSiren = response.headers.get('content-type')?.includes('application/vnd.siren+json');
        if (isSiren) {
            const sirenJson = await response.json()
            return fromJson(sirenJson)
        }
        return await response.json(); // any
    } else {
        const problemJson = await response.json()
        return new ProblemJson(problemJson.title, response.status, problemJson.detail)
    }
}

function validateRequestMethod(request: Request): boolean {
    const method = request.method.toUpperCase()
    return request.url !== undefined && (method === 'GET' || method === 'POST' || method === 'PUT' || method === 'DELETE')
}

function buildBody(fields: KeyValuePair[]): string {
    const body: any = {}
    fields.forEach(field => {
        body[field.name] = field.value
    })
    return JSON.stringify(body)
}