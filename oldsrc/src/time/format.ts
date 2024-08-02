import {DateTime} from 'luxon';

export function formatWorldTime(worldTime: DateTime): string {
    return worldTime.toLocaleString({...DateTime.TIME_24_SIMPLE});
}
