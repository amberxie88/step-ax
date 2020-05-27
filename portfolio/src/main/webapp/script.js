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
