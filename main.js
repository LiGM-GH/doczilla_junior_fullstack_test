const reader = new FileReader();

const form = null;

document.onload = function() {
  registerForm();
}

function registerForm() {
  form = document.getElementById("form");
  if (form == null || !(form instanceof HTMLFormElement)) {
    console.log("Couldn't find form");
    return;
  }

  form.addEventListener("submit", (event) => {
    event.preventDefault();

    (async () => {
      const result = await requestUpload(formData)

      if (result != true) {
        decorateHappy();
      } else {
        decorateSad();
      }
    })();
  });
}

function upload(input) {
  if (input.files && input.files[0]) {
    reader.readAsDataURL(input.files[0]);
  }
}

async function requestUpload(formData) {
  const formData = new FormData();
  formData.append("file", reader.result);

  const result = await fetch("/upload", {
    method: "GET",
    body: formData,
  });

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
