function validateForm() {
    var petName = document.getElementById("pet_name").value.trim();
    var petType = document.getElementById("pet_type").value;
    var petBreed = document.getElementById("pet_breed").value.trim();
    var petAge = document.getElementById("pet_age").value.trim();
    var petWeight = document.getElementById("pet_weight").value.trim();
    var petGender = document.getElementById("pet_gender").value;

    if (petName === "") {
        alert("Please enter the pet name");
        return false;
    }

    if (petType === "") {
        alert("Please select the pet type");
        return false;
    }

    if (petBreed === "") {
        alert("Please enter the pet breed");
        return false;
    }

    if (petAge === "" || petAge <= 0) {
        alert("Please enter a valid pet age (greater than zero)");
        return false;
    }

    if (petWeight === "" || petWeight <= 0) {
        alert("Please enter a valid pet weight (greater than zero)");
        return false;
    }

    if (petGender === "") {
        alert("Please select the pet gender");
        return false;
    }

    return true;
}

$(document).ready(function() {
    $('#pet_name').on('input', function () {
        var name = $(this).val().trim();
        if (name === "") {
            $('#nameError').text('Please enter the pet name').show();
            $('#submit-button').prop('disabled', true);
        } else {
            $('#nameError').text('').hide();
            $('#submit-button').prop('disabled', false);
        }
    });

    $('#pet_breed').on('input', function () {
        var breed = $(this).val().trim();
        if (breed === "") {
            $('#breedError').text('Please enter the pet breed').show();
            $('#submit-button').prop('disabled', true);
        } else {
            $('#breedError').text('').hide();
            $('#submit-button').prop('disabled', false);
        }
    });

    $('#pet_age').on('input', function () {
        var age = $(this).val();
        if (age <= 0) {
            $('#ageError').text('Please enter a valid age (greater than zero)').show();
            $('#submit-button').prop('disabled', true);
        } else {
            $('#ageError').text('').hide();
            $('#submit-button').prop('disabled', false);
        }
    });

    $('#pet_weight').on('input', function () {
        var weight = $(this).val();
        if (weight <= 0) {
            $('#weightError').text('Please enter a valid weight (greater than zero)').show();
            $('#submit-button').prop('disabled', true);
        } else {
            $('#weightError').text('').hide();
            $('#submit-button').prop('disabled', false);
        }
    });

    // Ensure the submit button is enabled/disabled correctly when the page loads
    $('input').trigger('input');
});