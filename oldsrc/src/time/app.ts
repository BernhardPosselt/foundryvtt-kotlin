export function formatHours(seconds: number, isNegative = false): string {
    const padZero = (num: number): string => `${num}`.padStart(2, '0');
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return `${isNegative ? '-' : ''}${padZero(hours)}:${padZero(minutes)}`;
}
