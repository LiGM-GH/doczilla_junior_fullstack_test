var form = null;
var prevHeading = null;

window.onload = function() {
  registerForm();
};

function registerForm() {
  form = document.getElementById("form");
  if (form == null || !(form instanceof HTMLFormElement)) {
    console.log("Couldn't find form");
    return;
  }

  form.addEventListener("submit", (event) => {
    event.preventDefault();

    (async () => {
      decorateUnsure();

      const result = await requestUpload();

      if (result == null) {
        decorateSad();
      } else {
        decorateHappy(result);
      }
    })();
  });
}

async function requestUpload() {
  const fileinput = document.getElementById("file-input");

  if (fileinput == null || !(fileinput instanceof HTMLInputElement)) {
    console.log("Couldn't find fileinput");
    return null;
  }

  const formData = new FormData();

  formData.append("file", fileinput.files[0]);

  var result = null;

  try {
    result = await fetch("/fileserv", {
      method: "POST",
      body: formData,
    });
  } catch (error) {
    console.log("ERROR HAPPENED: " + error.message);
    return null;
  }

  if (result == null) {
    return null;
  }

  if (!result.ok) {
    return null;
  }

  const text = await result.text();
  return text;
}

function decorateHappy(result) {
  const heading = document.getElementById("heading");
  prevHeading = heading.innerText;
  heading.innerText = window.location.href + "/" + result;
  heading.className = "happy-heading";
}

function decorateSad() {
  const heading = document.getElementById("heading");
  heading.className = "sad-heading";
}

function decorateUnsure() {
  const heading = document.getElementById("heading");
  heading.innerText = prevHeading;
  heading.className = "sad-heading";
}
