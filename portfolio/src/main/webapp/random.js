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
 * This function is run every time the page reloads.
 * First, it gets the comment section (with no comment limit)
*/
function reloadPage() {
    getCommentSection(-1);
    toggle_comment_visibility();
    fetchBlobstoreUrlAndShowForm();
    getImageUploads();
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
async function getCommentSection(numComments) {
  var query = '/comment-section?numComments=' + numComments;
  const res = await fetch(query);
  const comments = await res.json();
  const commentDiv = document.getElementById('comment-history');
  commentDiv.innerHTML = '';
  comments.forEach((c) => {
      createComment(commentDiv, c);
  });
  getReplies();
}

function createComment(historyEl, c) {
    var date = new Date(c.timestamp);
    var commentThreadDiv = document.createElement("div");
    commentThreadDiv.classList.add("comment-thread-div");
    historyEl.append(commentThreadDiv);

    var commentDiv = document.createElement("div");
    commentDiv.classList.add("single-comment-div");
    commentThreadDiv.append(commentDiv);

    var content = document.createElement("p");
    content.classList.add("single-comment-username");
    content.innerText = c.username;
    commentDiv.append(content);

    var content = document.createElement("p");
    content.classList.add("single-comment-content");
    content.innerText = c.content;
    commentDiv.append(content);

    var metaDiv = document.createElement("div");
    metaDiv.classList.add("single-comment-meta-div");
    commentDiv.append(metaDiv); // make this flex later ? 

    var email = document.createElement("p");
    email.classList.add("single-comment-meta-text");
    email.innerText = c.email;
    metaDiv.append(email);

    var time = document.createElement("p");
    time.classList.add("single-comment-meta-text");
    time.innerText = date;
    metaDiv.append(time);

    var replyLink = document.createElement("a");
    replyLink.classList.add("single-comment-meta-text");
    replyLink.innerText = 'Reply';
    replyLink.href = "#";
    replyLink.onclick = function(){openReply(c.id)};
    metaDiv.append(replyLink);

    var replyDiv = document.createElement("div");
    replyDiv.classList.add("single-comment-reply-div");
    replyDiv.id = 'reply-' + c.id;
    commentDiv.append(replyDiv);

    var threadDiv = document.createElement("div");
    threadDiv.classList.add("single-comment-thread-div");
    threadDiv.id = 'thread-' + c.id;
    commentDiv.append(threadDiv);
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
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
        requestTranslation(comment.firstElementChild.children[1]);
    }
}

function openReply(input) {
    addCommentEl = document.getElementById("leave-comment-form");
    replyDiv = document.getElementById("reply-" + input)
    if(addCommentEl.style.display == 'none') {
        alert("Please log in before replying");
    } else {
        console.log('okee cool');
        var leaveComment = document.getElementById('leave-comment-form');
        var clone = leaveComment.cloneNode(true);
        clone.id = 'leave-comment-form-' + input;
        var testerNode = document.getElementById('good');
        createForm(replyDiv, input);
        //replyDiv.append(clone);
    }
}

function createForm(replyDiv, input) {
    var f = document.createElement("form");
    f.setAttribute('method',"post");
    f.setAttribute('action',"/reply-section?id=" + input);

    var heading = document.createElement("h3");
    heading.innerText = "Reply";

    var name = document.createElement("p");
    name.innerText = "Name:";

    var i = document.createElement("input"); //input element, text
    i.setAttribute('type',"text");
    i.setAttribute('name',"name-input");
    i.setAttribute('value',"Name");

    var commentField = document.createElement("p");
    commentField.innerText = "Comment field:";

    var commenti = document.createElement("input"); //input element, text
    commenti.setAttribute('type',"text");
    commenti.setAttribute('name',"comment-input");
    commenti.setAttribute('value',"Leave me a comment!");

    var s = document.createElement("input"); //input element, Submit button
    s.setAttribute('type',"submit");

    f.appendChild(heading);
    f.appendChild(name);
    f.appendChild(i);
    f.appendChild(commentField);
    f.appendChild(commenti);
    f.appendChild(s);

    replyDiv.appendChild(f);
}

/**
 * Fetches all replies and attaches them to the corresponding comment. 
 */
function getReplies() {
  fetch('/reply-section').then(response => response.json()).then((replies) => {
    replies.forEach((r) => {
        createReply(r);
    });
  });
}

function createReply(r) {
    var threadDiv = document.getElementById('thread-' + r.parentID);
    var date = new Date(r.timestamp);

    if (threadDiv == null) {
        return;
    }

    var commentDiv = document.createElement("div");
    commentDiv.classList.add("single-comment-div");
    threadDiv.append(commentDiv);

    var content = document.createElement("p");
    content.classList.add("single-comment-username");
    content.innerText = r.username;
    commentDiv.append(content);

    var content = document.createElement("p");
    content.classList.add("single-comment-content");
    content.innerText = r.content;
    commentDiv.append(content);

    var metaDiv = document.createElement("div");
    metaDiv.classList.add("single-comment-meta-div");
    commentDiv.append(metaDiv); // make this flex later ? 

    var email = document.createElement("p");
    email.classList.add("single-comment-meta-text");
    email.innerText = r.email;
    metaDiv.append(email);

    var time = document.createElement("p");
    time.classList.add("single-comment-meta-text");
    time.innerText = date;
    metaDiv.append(time);
}