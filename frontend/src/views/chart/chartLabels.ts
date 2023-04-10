import {Period} from "./MyChart";

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

export function mapDataToDayOfMonthLabel(period: Period, date: Date[]): any[] {
    return date.map((d) => {
        return {
            x: d.getDate(),
            y: d.getDate().toString()
        }
    })
}

export function mapDataToMonthLabel(period: Period, date: Date[]): any[] {
    const labels = monthLabels()
    return date.map((d) => {
        return {
            x: d.getMonth(),
            y: labels[d.getMonth()]
        }
    })
}

function mapDataToYearLabel(period: Period, date: Date[]): any[] {
    return date.map((d) => {
        return {
            x: d.getFullYear(),
            y: d.getFullYear().toString()
        }
    })
}

function mapDataToHourLabel(period: Period, date: Date[]): any[] {
    return date.map((d) => {
        return {
            x: d.getHours(),
            y: d.getHours().toString()
        }
    })
}

export function mapToLabel(period: Period, date: Date[]): any[] {
    switch (period) {
        case Period.HOUR:
            return mapDataToHourLabel(period, date)
        case Period.DAY:
            return mapDataToDayOfMonthLabel(period, date)
        case Period.MONTH:
            return mapDataToMonthLabel(period, date)
        case Period.YEAR:
            return mapDataToYearLabel(period, date)
        default:
            throw new Error(`Invalid period: ${period}`)
    }
}