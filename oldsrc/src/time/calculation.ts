import {DateTime} from 'luxon';


export function getWorldTime(game: Game): DateTime {
    const value = game.settings.get('pf2e', 'worldClock.worldCreatedOn') as string;
    return DateTime.fromISO(value)
        .toUTC()
        .plus({seconds: game.time.worldTime});
}


export function getTimeOfDayPercent(time: DateTime): number {
    const elapsedSeconds = time.second + time.minute * 60 + time.hour * 3600;
    return elapsedSeconds / (36 * 24);
}

export function isDayOrNight(time: DateTime): 'day' | 'night' {
    if (time.hour >= 6 && time.hour < 18) {
        return 'day';
    } else {
        return 'night';
    }
}
