// Validate if password meets requirements
export function validatePassword(password) {
  // must include uppercase, lowercase, number, special char, min 8 chars
  const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/
  return regex.test(password)
}

// Get password strength for meter
export function getPasswordStrength(password) {
  let score = 0
  if (/.{8,}/.test(password)) score++ // length >= 8
  if (/[A-Z]/.test(password)) score++ // uppercase
  if (/[a-z]/.test(password)) score++ // lowercase
  if (/[0-9]/.test(password)) score++ // number
  if (/[^A-Za-z0-9]/.test(password)) score++ // special char

  if (score <= 2) return { label: 'Weak', color: 'negative', value: 0.2 }
  if (score === 3 || score === 4) return { label: 'Medium', color: 'warning', value: 0.6 }
  if (score === 5) return { label: 'Strong', color: 'positive', value: 1 }
  return { label: '', color: 'grey', value: 0 }
}
