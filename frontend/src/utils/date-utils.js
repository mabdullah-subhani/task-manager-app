// src/utils/date-utils.js

/**
 * Format datetime-local value ("yyyy-MM-ddTHH:mm")
 * into backend-friendly format ("yyyy-MM-ddTHH:mm:ss.SSS").
 *
 * @param {string|null} dateTime
 * @returns {string|null}
 */
export const formatDueDate = (dateTime) => {
  if (!dateTime) return null
  return dateTime + ':00.000' // adds seconds + milliseconds
}

/**
 * Convert backend format ("yyyy-MM-ddTHH:mm:ss.SSS")
 * to datetime-local format ("yyyy-MM-ddTHH:mm").
 *
 * @param {string|null} backendDate
 * @returns {string|null}
 */
export const toDateTimeLocal = (backendDate) => {
  if (!backendDate) return null
  return backendDate.substring(0, 16) // trim to yyyy-MM-ddTHH:mm
}
