
function validatepetForm() {
    var form = document.getElementById("addPetForm");
    var name = form.elements["name"].value;
    var type = form.elements["type"].value;
    var breed = form.elements["breed"].value;
    var age = form.elements["age"].value;
    var weight = form.elements["weight"].value;
    var gender = form.elements["gender"].value;
  
    if (!name || !type || !breed || !age || !weight || !gender) {
      alert("Please fill out all fields.");
      return;
    }
  
  
    if (!isValidPhoneNumber(phone)) {
      alert("Please enter a valid phone number.");
      return;
    }
  
    form.submit();
  }
  
  
  function isValidPhoneNumber(phone) {
    var phoneRegex = /^[0-9]{10}$/;
    return phoneRegex.test(phone);
  }
  
  function isValidAge(age) {
    return age.length == 2;
  }
  