$(document).ready(function() {
    $('.login-form').submit(function(event) {
      event.preventDefault(); // Prevent the default form submission
      
      // Retrieve input field values
      var name = $('input[name="name"]').val().trim();
      var password = $('input[name="password"]').val().trim();
  
      // Simple validation for name
      if (name === "") {
        alert("Please enter your name");
        return false;
      }
  
      // Password validation
      if (password === "") {
        alert("Please enter your password");
        return false;
      }
  
      // If all validations pass, submit the form
      this.submit();
    });
  });
  