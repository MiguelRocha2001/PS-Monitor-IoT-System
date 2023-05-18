import {PhRecord} from "../../services/domain";
import {TimeUnit} from "./MyChart";

export function toLabels(start: Date, end: Date, timeUnit: TimeUnit): string[] {
    if (timeUnit === "hour") {
        return hourLabels(start, end)
    }
    if (timeUnit === "day") {
        return dayLabels(start, end)
    }
    if (timeUnit === "month") {
        return monthLabels(start, end)
    }
    if (timeUnit === "year") {
        return yearLabels(start, end)
    }
    throw new Error(`Unsupported time unit ${timeUnit}`)
}

export function hourLabels(startDate: Date, endDate: Date): string[] {
    const labels: string[] = []
    labels.push(startDate.getHours().toString())

    let nextHour = new Date(startDate.getTime() + 1000 * 60 * 60) // add one hour
    while (nextHour.getTime() <= endDate.getTime()) {
        labels.push(nextHour.getHours().toString())
        nextHour = new Date(nextHour.getTime() + 1000 * 60 * 60) // add one hour
    }
    return labels
}

// TODO: use moment.js
export function dayLabels(startDate: Date, endDate: Date): string[] {
    const labels: number[] = []
    labels.push(startDate.getDate())

    let nextDay = new Date(startDate.getTime() + 1000 * 60 * 60 * 24) // add one day
    while (nextDay.getTime() <= endDate.getTime()) {
        labels.push(nextDay.getDate())
        nextDay = new Date(nextDay.getTime() + 1000 * 60 * 60 * 24) // add one day
    }
    return labels.map((day) => day.toString())
}


export function monthLabels(startDate: Date, endDate: Date): string[] {
    const labels: string[] = []
    labels.push(startDate.toLocaleString('default', {month: 'short'}))

    let nextMonth = new Date(startDate.getTime())
    nextMonth.setMonth(nextMonth.getMonth() + 1)
    while (nextMonth.getTime() <= endDate.getTime()) {
        labels.push(nextMonth.toLocaleString('default', {month: 'short'}))
        nextMonth.setMonth(nextMonth.getMonth() + 1)
    }
    return labels
}

export function yearLabels(startDate: Date, endDate: Date): string[] {
    const labels: number[] = []
    labels.push(startDate.getFullYear())

    let nextYear = new Date(startDate.getTime())
    nextYear.setFullYear(nextYear.getFullYear() + 1)
    while (nextYear.getTime() <= endDate.getTime()) {
        labels.push(nextYear.getFullYear())
        nextYear.setFullYear(nextYear.getFullYear() + 1)
    }
    return labels.map((year) => year.toString())
}

export function mapRecordToHourLabel(start: Date, end: Date, records: PhRecord[]): any[] {
    const filtered = filterByDate(start, end, records)
        .map((record) => {
            return {
                x: record.date.getHours().toString(),
                y: record.value
            };
        });
    return toAverage(filtered);
}

function mapRecordToDayLabel(start: Date, end: Date, records: PhRecord[]): any[] {
    const filtered = filterByDate(start, end, records)
        .map((record) => {
            return {
                x: record.date.getDate().toString(),
                y: record.value
            };
        });
    return toAverage(filtered);
}

function mapRecordToMonthLabel(start: Date, end: Date, records: PhRecord[]): any[] {
    const filtered = filterByDate(start, end, records)
        .map((record) => {
            return {
                x: record.date.getMonth().toString(),
                y: record.value
            };
        });
    return toAverage(filtered);
}

function mapRecordToYearLabel(start: Date, end: Date, records: PhRecord[]): any[] {
    const filtered = filterByDate(start, end, records)
        .map((record) => {
            return {
                x: record.date.getFullYear().toString(),
                y: record.value
            };
        });
    return toAverage(filtered);
}

function filterByDate(start: Date, end: Date, records: PhRecord[]): PhRecord[] {
    return records.filter((record) => {
        return record.date.getTime() >= start.getTime() && record.date.getTime() <= end.getTime();
    });
}

type Records = {x: string, y: number}[]
function toAverage(records: Records): any[] {
    const map = new Map<string, Set<number>>();
    records.forEach((record) => {
        if (map.has(record.x)) {
            const value = map.get(record.x);
            if (value !== undefined) {
                map.set(record.x, value.add(record.y));
            }
        } else {
            map.set(record.x, new Set<number>([record.y]));
        }
    });
    const result: any[] = [];
    map.forEach((value, key) => {
        result.push({
            x: key,
            y: Array.from(value).reduce((a, b) => a + b, 0) / value.size
        });
    });
    return result;
}

export function mapToData(start: Date, end: Date, timeUnit: TimeUnit, record: PhRecord[]): any[] {
    if (timeUnit === "hour") {
        return mapRecordToHourLabel(start, end, record)
    }
    if (timeUnit === "day") {
        return mapRecordToDayLabel(start, end, record)
    }
    if (timeUnit === "month") {
        return mapRecordToMonthLabel(start, end, record)
    }
    if (timeUnit === "year") {
        return mapRecordToYearLabel(start, end, record)
    }
    throw new Error(`Unsupported time unit ${timeUnit}`)
}