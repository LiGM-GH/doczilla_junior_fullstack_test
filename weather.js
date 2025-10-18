var weather = null;

function call_last() {
  const curtime = new Date();
  const pattern = curtime.getFullYear().toString() + "-" + (curtime.getMonth() + 1).toString() + "-" + curtime.getDate().toString() + "T" + curtime.getHours().toString() + ":00";
  const left = weather["hourly"]["time"];
  const right = weather["hourly"]["temperature_2m"];

  var start = 0;
  for (var i = 0; i < left.length && i < right.length && i < 24 + start; i++) {
    if (left[i] == pattern) {
      start = i;
      break;
    }

    console.log(left[i].toString() + " is not " + pattern.toString());
  }

  var max = right[start];
  var min = right[start];
  for (var i = start; i < left.length && i < right.length && i < 24 + start; i++) {
    if (max < right[i]) {
      max = right[i];
    }

    if (min > right[i]) {
      min = right[i]
    }
  }

  if (Math.abs(min) > Math.abs(max)) {
    max = Math.abs(min);
  }

  for (var i = start; i < left.length && i < right.length && i < 24 + start; i++) {
    const p = document.getElementById("p-" + (i - start).toString());
    console.log("P is " + p.id);
    p.innerText = left[i].split("T")[1];
    if (right[i] > 0.0) {
      const red = document.getElementById("red-" + (i - start).toString());
      console.log("Red is " + red.id);
      red.textContent = right[i];
      red.style.height = (right[i] / max * 100).toString() + "%";
    } else {
      const blue = document.getElementById("blue-" + (i - start).toString());
      console.log("Red is " + blue.id);
      blue.textContent = right[i];
      blue.style.height = (-right[i] / max * 100).toString() + "%";
    }
  }
}
