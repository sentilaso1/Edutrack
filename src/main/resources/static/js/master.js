function formatNumberWithSpaces(value) {
    if (typeof value !== 'number' || isNaN(value)) return '';

    const absValue = Math.abs(value);

     // Replace default commas with spaces
    return absValue.toLocaleString('en-US', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2,
        useGrouping: true,
    }).replace(/,/g, ' ');
}

function formatNumberWithSign(value) {
    if (typeof value !== 'number' || isNaN(value)) return '';

    const sign = value >= 0 ? '+' : '-';
    const absValue = Math.abs(value);

    const parts = absValue.toLocaleString('en-US', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2,
        useGrouping: true,
    }).replace(/,/g, ' '); // Replace default commas with spaces

    return sign + parts;
}
