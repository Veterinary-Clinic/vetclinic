// File: src/main/resources/static/js/doctor/loginValidation.js

$(document).ready(function () {
  $('#email').on('input', function () {
    var email = $(this).val();

    // Regular expression for email validation
    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(email)) {
      $('#emailError').text('Please enter a valid email');
      $('#sign').prop('disabled', true);
    } else {
      $('#emailError').text('');
      $('#sign').prop('disabled', false);
    }
  });

  $('#password').on('input', function () {
    var password = $(this).val();
    if (password.length < 8) {
      $('#passwordError').text('Password must be at least 8 characters long');
      $('#sign').prop('disabled', true);
    } else {
      $('#passwordError').text('');
      $('#sign').prop('disabled', false);
    }
  });
});
