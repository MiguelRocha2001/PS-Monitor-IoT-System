import {BackendError, NetworkError} from "./erros";
import {Logger} from "tslog";
import {fromJson, Siren} from "./sirenModule";

const host = 'http://localhost:8080'
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

export async function fetchRequest(request: Request): Promise<Response> {
    const idToken = localStorage.getItem('idToken')
    if (!idToken) {
        throw new Error('No token found')
    }

    return await fetch(toFullUrl(request), {
        method: request.method,
        headers: {
            'Content-Type': CONTENT_TYPE_JSON,
            'Authorization': `Bearer ${idToken}`
        },
        credentials: 'include',
        body: request.body ? buildBody(request.body) : undefined
    })
}

/**
 * Makes an API call.
 * @param request Request object containing url, method and body.
 * The url is relative to the API host (host should not be included).
 */
export async function doFetch(request: Request): Promise<Siren | BackendError> {
    if (request && validateRequestMethod(request)) {
        logger.info("sending request to: ", toFullUrl(request))
        // console.log("body: ", request.body ? buildBody(request.body) : undefined)
        try {
            const resp = await fetchRequest(request)
            const data = await getSirenOrProblemOrUndefined(resp)

            if (data instanceof ProblemJson) {
                logger.error("Response Error: ", data.title)
                return new BackendError(data.title, resp.status)
            }
            return data
        } catch (error: any) {
            logger.error("Network Error: ", error)
            return Promise.reject(new NetworkError(error.message))
        }
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

export async function getSirenOrProblemOrUndefined(response: Response): Promise<Siren | ProblemJson> {
    if (response.ok) {
        const isSiren = response.headers.get('content-type')?.includes('application/vnd.siren+json');
        if (isSiren) {
            const sirenJson = await response.json()
            return fromJson(sirenJson)
        }
        return isSiren ? await response.json() : null;
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
