function validateForm() {
    const dateField = document.getElementById('date');
    const startHrField = document.getElementById('startHr');
    const endHrField = document.getElementById('endHr');
    const dateError = document.getElementById('date-error');
    const startHrError = document.getElementById('startHr-error');
    const endHrError = document.getElementById('endHr-error');

    let isValid = true;

    // Clear previous error messages
    dateError.textContent = '';
    startHrError.textContent = '';
    endHrError.textContent = '';

    // Validate date 
    const dateValue = dateField.value;
    if (!dateValue) {
        dateError.textContent = 'Please select a date.';
        isValid = false;
    } else {
        const today = new Date();
        const selectedDate = new Date(dateValue);

        today.setHours(0, 0, 0, 0); // Set time to the start of today
        if (selectedDate < today) {
            dateError.textContent = 'Preferred date cannot be in the past.';
            isValid = false;
        }
    }

    // Validate time fields
    const startHrValue = startHrField.value;
    const endHrValue = endHrField.value;

    if (!startHrValue) {
        startHrError.textContent = 'Please select a start time.';
        isValid = false;
    }

    if (!endHrValue) {
        endHrError.textContent = 'Please select an end time.';
        isValid = false;
    }

    if (startHrValue && endHrValue) {
        const [startHours, startMinutes] = startHrValue.split(':').map(Number);
        const [endHours, endMinutes] = endHrValue.split(':').map(Number);

        const startTime = new Date();
        startTime.setHours(startHours, startMinutes, 0, 0);

        const endTime = new Date();
        endTime.setHours(endHours, endMinutes, 0, 0);

        if (endTime <= startTime) {
            endHrError.textContent = 'End time must be later than start time.';
            isValid = false;
        }
    }

    return isValid;
}