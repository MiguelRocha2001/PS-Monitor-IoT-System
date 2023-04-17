import {PhRecord} from "../../services/domain";
import {Day, Hour, Month, Period, Year} from "./MyChart";

export function toLabels(period: Period) {
    if (period instanceof Hour) {
        return hourLabels() // TODO: fix this later
    } else if (period instanceof Day) {
        return hourLabels()
    } else if (period instanceof Month) {
        return dayLabels()
    } else if (period instanceof Year) {
        return monthLabels()
    } else {
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

function mapRecordToMinuteLabel(period: Hour, records: PhRecord[]): any[] {
    return records.map((record) => {
        if (
            record.date.getFullYear() !== period.day.month.year.year ||
            record.date.getMonth() !== period.day.month.month ||
            record.date.getDate() !== period.day.day ||
            record.date.getHours() !== period.hour
        ) {
            return null
        }
        return {
            x: record.date.getMinutes().toString(),
            y: record.value
        }
    }).filter((record) => record !== null)
}

function mapRecordToHourLabel(period: Day, records: PhRecord[]): any[] {
    return records.map((record) => {
        if (
            record.date.getFullYear() !== period.month.year.year ||
            record.date.getMonth() !== period.month.month ||
            record.date.getDate() !== period.day
        ) {
            return null
        }
        return {
            x: record.date.getHours().toString(),
            y: record.value
        }
    }).filter((record) => record !== null)
}

export function mapRecordToDayOfMonthLabel(period: Month, records: PhRecord[]): any[] {
    return records.map((record) => {
        if (
            record.date.getFullYear() !== period.year.year ||
            record.date.getMonth() !== period.month
        )  {
            return null
        }
        return {
            x: record.date.getDate().toString(),
            y: record.value
        }
    }).filter((record) => record !== null)
}

export function mapRecordToMonthLabel(period: Year, records: PhRecord[]): any[] {
    return records.map((record) => {
        if (record.date.getFullYear() !== period.year)  {
            return null
        }
        else return {
                x: record.date.getMonth().toString(),
                y: record.value
        }
    }).filter((record) => record !== null)
}

function mapRecordToYearLabel(period: Period, records: PhRecord[]): any[] {
    return records.map((record) => {
        return {
            x: record.date.getFullYear().toString(),
            y: record.value
        }
    }).filter((record) => record !== null)
}

export function mapToData(period: Period, record: PhRecord[]): any[] {
    if (period instanceof Hour) {
        return mapRecordToMinuteLabel(period, record)
    } else if (period instanceof Day) {
        return mapRecordToHourLabel(period, record)
    } else if (period instanceof Month) {
        return mapRecordToDayOfMonthLabel(period, record)
    } else if (period instanceof Year) {
        return mapRecordToMonthLabel(period, record)
    } else {
        throw new Error(`Invalid period: ${period}`)
    }
}