import {NetworkError, ServerError} from "./services/erros";
import {Logger} from "tslog";
import Any = jasmine.Any;
import {Siren} from "./services/sirenModule";

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

export async function fetchRequest(request: Request): Promise<Response> {
    return await fetch(host + request.url, {
        method: request.method,
        body: request.body ? buildBody(request.body) : undefined,
        headers: {
            'Content-Type': CONTENT_TYPE_JSON,
        },
        credentials: 'include'
    })
}

export async function doFetch(request: Request): Promise<Siren | undefined | ServerError> {
    if (request && validateRequestMethod(request)) {
        logger.info("sending request to: ", host + request.url)
        // console.log("body: ", request.body ? buildBody(request.body) : undefined)
        try {
            const resp = await fetchRequest(request)
            const data = await getSirenOrProblemOrUndefined(resp)

            if (data instanceof ProblemJson) {
                logger.error("Response Error: ", data.title)
                return new ServerError(data.title, resp.status)
            }
            return data
        } catch (error: any) {
            logger.error("Network Error: ", error)
            return Promise.reject(new NetworkError(error.message))
        }
    }
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

export async function getSirenOrProblemOrUndefined(response: Response): Promise<Siren | ProblemJson | undefined> {
    if (response.ok) {
        const isSiren = response.headers.get('content-type')?.includes('application/vnd.siren+json');
        if (isSiren) {
            const sirenJson = await response.json()
            return buildSirenFromJson(sirenJson)
        }
        return isSiren ? await response.json() : null;
    } else {
        const problemJson = await response.json()
        return new ProblemJson(problemJson.title, response.status, problemJson.detail)
    }

    function buildSirenFromJson(json: any): Siren {
        return json as Siren
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
