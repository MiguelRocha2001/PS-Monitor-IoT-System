import {Period} from "./MyChart";
import {PhRecord} from "../../services/domain";

export function toLabels(period: Period) {
    switch (period) {
        case Period.HOUR:
            return hourLabels()
        case Period.DAY:
            return dayLabels()
        case Period.MONTH:
            return monthLabels()
        case Period.YEAR:
            return yearLabels()
        default:
            throw new Error(`Invalid period: ${period}`)
    }
}

export function weekDayLabels(): string[] {
    return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
}

export function hourLabels(): string[] {
    return ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']
}

// TODO: use moment.js
export function dayLabels(): string[] {
    return ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31']
}

export function monthLabels(): string[] {
    return ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
}


// TODO: use moment.js
export function yearLabels(): string[] {
    return ['2018', '2019', '2020', '2021', '2022', '2023', '2024', '2025', '2026', '2027', '2028', '2029', '2030']
}

export function mapRecordToDayOfMonthLabel(period: Period, records: PhRecord[]): any[] {
    return records.map((record) => {
        return {
            x: record.date.getDay().toString(),
            y: record.value
        }
    })
}

export function mapRecordToMonthLabel(period: Period, records: PhRecord[]): any[] {
    return records.map((record) => {
        return {
            x: record.date.getMonth().toString(),
            y: record.value
        }
    })
}

function mapRecordToYearLabel(period: Period, records: PhRecord[]): any[] {
    return records.map((record) => {
        return {
            x: record.date.getFullYear().toString(),
            y: record.value
        }
    })
}

function mapRecordToHourLabel(period: Period, records: PhRecord[]): any[] {
    return records.map((record) => {
        return {
            x: record.date.getHours().toString(),
            y: record.value
        }
    })
}

export function mapToLabel(period: Period, record: PhRecord[]): any[] {
    switch (period) {
        case Period.HOUR:
            return mapRecordToHourLabel(period, record)
        case Period.DAY:
            return mapRecordToDayOfMonthLabel(period, record)
        case Period.MONTH:
            return mapRecordToMonthLabel(period, record)
        case Period.YEAR:
            return mapRecordToYearLabel(period, record)
        default:
            throw new Error(`Invalid period: ${period}`)
    }
}