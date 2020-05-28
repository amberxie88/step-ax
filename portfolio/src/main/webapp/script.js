// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['The Truman Show', 'Inception', 'Interstellar', 'Contact', 'Eternal Sunshine of the Spotless Mind', 'Ex Machina', 'Wall-E', 'Hidden Figures', 'Memento'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function addCaption(id, txt) {
  const txtContainer = document.getElementById(id);
  txtContainer.innerText = txt;
  txtContainer.style.background = "rgba(100, 149, 237, 0.7)";
  txtContainer.style.borderRadius = "10px";
}

function remCaption(id) {
  const txtContainer = document.getElementById(id);
  txtContainer.innerText = "";
  txtContainer.style.backgroundColor = "rgba(0,0,0,0)";
}

/**
 * Copied from walkthrough code:
 * Another way to use fetch is by using the async and await keywords. This
 * allows you to use the return values directly instead of going through
 * Promises.
 */
async function getHelloAsync() {
  fetch('/data').then(response => response.json()).then((stats) => {
      const helloElement = document.getElementById('hello-container');
      helloElement.innerHTML = '';
    createList(helloElement, stats.colors);

  });
}

function createList(element, array) {
    console.log("Hello");
    console.log(array);
    console.log(array[0]);
    console.log(array.length);

    array.forEach(item => {
        const liElement = document.createElement('li');
        liElement.innerText = item;
        element.appendChild(liElement);
    });
}
