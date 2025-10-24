/**
 * Format currency value
 * @param value - The numeric value to format
 * @param currency - Currency code (default: BRL)
 */
export const formatCurrency = (value: number, currency: string = 'BRL'): string => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency,
  }).format(value);
};

/**
 * Format date to Brazilian format
 * @param date - Date string or Date object
 */
export const formatDate = (date: string | Date): string => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  return new Intl.DateTimeFormat('pt-BR').format(dateObj);
};

/**
 * Format date to ISO string for API
 * @param date - Date object
 */
export const formatDateToISO = (date: Date): string => {
  return date.toISOString();
};
