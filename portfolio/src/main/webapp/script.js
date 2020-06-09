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
    array.forEach(item => {
        const liElement = document.createElement('li');
        liElement.innerText = item;
        element.appendChild(liElement);
    });
}

/**
 * Fetches the current state of the comment section and builds the UI.
 * NEW: Also accounts for the number of comments that the user wants to.
 */
function getCommentSection(numComments) {
  const historyEl = document.getElementById('comment-history');

  // invalid case
  if (numComments < -1) {
      return;
  }
  
  // -1 means display all comments
  if (numComments == -1) {
    // clean out all previous elements
    while (historyEl.childElementCount > 0) {
        historyEl.lastChild.remove();
    }

    fetch('/comment-section').then(response => response.json()).then((comments) => {
    const historyEl = document.getElementById('comment-history');
    comments.comments.forEach((c) => {
        historyEl.appendChild(createListElement(c));
    });
    });
  } // need to display more elements than currently are shown
  else if (numComments > historyEl.childElementCount) {
      // clean out all previous elements
      while (historyEl.childElementCount > 0) {
          historyEl.lastChild.remove();
      }

      // fetch all comments
      fetch('/comment-section').then(response => response.json()).then((comments) => {
      const historyEl = document.getElementById('comment-history');
      comments.comments.forEach((c) => {
        historyEl.appendChild(createListElement(c));
    });

    // remove the necessary amount of comments (0 if there are fewer than desired to be displayed)
    const numberToRemove = Math.max(0, historyEl.childElementCount - numComments);
    for (i = 0; i < numberToRemove; i++) {
        historyEl.firstChild.remove();
    }
    });
  } // if the user desires fewer (or same number of) comments
  else { 
    // remove as necessary
    for (i = 0; i < historyEl.childElementCount - numComments + 1; i++) {
        historyEl.firstChild.remove();
    }
  } 
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/**
 * Deletes all comments in datastore by calling the relevant servlet.
 */
 /*
function deleteCommentSection() {
  const request = new Request('/delete-comment',{method: 'POST'});
  fetch('/delete-comment', {method: 'POST', body: '{"foo": "bar"}'}).then(response => {
        if (!response.ok){
            throw new Error('Network response was not ok.');
        }
    }).catch((err) => {
    	console.log(err);
    })
  fetch(request).then(response => response.json()).then((comments) => {
      console.log('eyoo');
  });
  console.log("hellooo");
}
*/

/** 
 * This function is run every time the page reloads.
 * First, it gets the comment section (with no comment limit)
*/
function reloadPage() {
    getCommentSection(-1);
    toggle_comment_visibility();
    fetchBlobstoreUrlAndShowForm();
    getImageUploads();
}

function toggle_comment_visibility() {
    fetch('/login').then(response => response.json()).then((comments) => {
      loginEl = document.getElementById("login");
      addCommentEl = document.getElementById("leave-comment-form");
      if (comments.loginStatus == 'true') {
        addCommentEl.style.display = 'block';
        document.getElementById("login-warning").innerHTML = comments.loginHTML;
      } 
      else if (comments.loginStatus == 'false') {
        addCommentEl.style.display = 'none';
        addCommentEl.appendChild(document.createTextNode(comments.loginHTML));
        document.getElementById("login-warning").innerHTML = comments.loginHTML;
      }
    });
}

function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('my-form');
        messageForm.action = imageUploadUrl;
        console.log('in here');
        //messageForm.classList.remove('hidden');
      });
}

/**
 * gets images
 */
function getImageUploads() {
    // fetch all comments
    fetch('/my-form-handler').then((response) => response.json()).then((imgs) => {
      const imgEl = document.getElementById('img-placeholder');
      imgEl.innerHTML = "";
      imgs.images.forEach((img) => {
        addImage(imgEl, img);
    })});
  
}

function addImage(element, img) {
    var imageCaptionDiv = document.createElement("div");
    imageCaptionDiv.classList.add("input-img-caption-div");
    element.append(imageCaptionDiv);

    var imageDiv = document.createElement("div");
    imageDiv.classList.add("input-img-div");
    imageCaptionDiv.append(imageDiv);

    var imageEl = document.createElement("img");
    imageEl.src = img.url;
    imageEl.classList.add("input-img");
    console.log("adding img");
    imageDiv.appendChild(imageEl);

    var captionEl = document.createElement("p");
    captionEl.innerText = img.message;
    imageCaptionDiv.appendChild(captionEl);
}



function requestTranslation(comment) {
    const text = comment.innerText;
    const languageCode = document.getElementById('language').value;

    const params = new URLSearchParams();
    params.append('text', text);
    params.append('languageCode', languageCode);

    fetch('/translate', {
        method: 'POST',
        body: params
    }).then(response => response.text())
    .then((translatedMessage) => {
        comment.innerText = translatedMessage;
    });
}

function translateComments() {
    const commentList = document.getElementById('comment-history');
    var children = commentList.children;
    for (var i = 0; i < children.length; i++) {
        var comment = children[i];
        requestTranslation(comment);
    }
}