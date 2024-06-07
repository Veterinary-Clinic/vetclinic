$(document).ready(function() {
  $('.signup-form').submit(function(event) {
    event.preventDefault(); // Prevent the default form submission
    
    // Retrieve input field values
    var name = $('input[name="name"]').val().trim();
    var email = $('input[name="email"]').val().trim();
    var password = $('input[name="password"]').val().trim();
    var phonenumber = $('input[name="phonenumber"]').val().trim();
    var gender = $('select[name="gender"]').val();

    // Simple validation for name
    if (name === "") {
      alert("Please enter your name");
      return false;
    }

    // Email validation
    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      alert("Please enter a valid email address");
      return false;
    }

    // Password validation (can be customized further)
    if (password.length < 6) {
      alert("Password must be at least 6 characters long");
      return false;
    }

    // Phone number validation (assuming it should contain only numbers and be a certain length)
    var phoneRegex = /^[0-9]{10}$/; // Assuming phone number should be 10 digits
    if (!phoneRegex.test(phonenumber)) {
      alert("Please enter a valid 10-digit phone number");
      return false;
    }

    // Gender validation
    if (gender === "") {
      alert("Please select your gender");
      return false;
    }

    // If all validations pass, submit the form
    this.submit();
  });
});
