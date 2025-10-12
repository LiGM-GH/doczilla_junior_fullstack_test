var form = null;

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

      if (result != true) {
        decorateSad();
      } else {
        decorateHappy();
      }
    })();
  });
}

async function requestUpload() {
  const fileinput = document.getElementById("file-input");

  if (fileinput == null || !(fileinput instanceof HTMLInputElement)) {
    console.log("Couldn't find fileinput");
    return;
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

  return true;
}

function decorateHappy() {
  const heading = document.getElementById("heading");
  heading.className = "happy-heading"
}

function decorateSad() {
  const heading = document.getElementById("heading");
  heading.className = "sad-heading"
}

function decorateUnsure() {
  const heading = document.getElementById("heading");
  heading.className = "sad-heading"
}
