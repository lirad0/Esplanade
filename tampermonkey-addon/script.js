// ==UserScript==
// @name         Button for returning to home
// @namespace    http://tampermonkey.net/
// @version      2026-03-28
// @description  Button
// @author       You
// @match        *://*/*
// @exclude      http://localhost:4321/*
// @run-at       document-idle
// @grant GM_addElement
// ==/UserScript==

// This script is for the button on top to return to the dashboard

GM_addElement('style', {
    textContent: `
    #tv-dashy {
      display: none;
      z-index: 9000;
      height: 70px;
      width: 70px;
      position: absolute;
      top: calc(9% - 70px);
      left: calc(50% - 70px);
    }
  
    #tv-dashy.visible {
      display: block;
    }
  
    .parent {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  #tv-dashy a {
    display: block;
    background-color: white;
    color: white;
    border: none;
    padding: 5px;
    font-size: 31px;
    height: 70px;
    width: 70px;
    box-shadow: 0 2px 4px darkslategray;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  #tv-dashy a:hover {
   background-color: gray;
  }
  
  #tv-dashy a:active {
    box-shadow: 0 0 2px darkslategray;
    transform: translateY(2px);
  }
  
  #tv-dashy a {
    margin-bottom: 10px;
  }
  
  #tv-dashy a {
    border-radius: 5%;
  }
  `
  });
  
  document.addEventListener("mousemove", (event) => {
    const centerLeft = window.innerWidth / 2 - 200;
    const centerRight = window.innerWidth / 2 + 200;
  
    if (event.clientY < 200 && event.clientX > centerLeft && event.clientX < centerRight) {
      document.getElementById("tv-dashy").classList.add("visible");
    } else {
      document.getElementById("tv-dashy").classList.remove("visible");
    }
  });
  
  (function() {
      'use strict';
  
      function createElementFromHTML(htmlString) {
          var div = document.createElement('div');
          div.innerHTML = htmlString.trim();
  
          // Change this to div.childNodes to support multiple top-level nodes.
          return div.firstChild;
      }
  
      const html = `
  <div id="tv-dashy">
    <a href="http://localhost:4321">
  <svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 122.88 112.07" style="enable-background:new 0 0 122.88 112.07" xml:space="preserve"><style type="text/css">.st0{fill-rule:evenodd;clip-rule:evenodd;}</style><g><path class="st0" d="M61.44,0L0,60.18l14.99,7.87L61.04,19.7l46.85,48.36l14.99-7.87L61.44,0L61.44,0z M18.26,69.63L18.26,69.63 L61.5,26.38l43.11,43.25h0v0v42.43H73.12V82.09H49.49v29.97H18.26V69.63L18.26,69.63L18.26,69.63z"/></g></svg>
    </a>
  </div>
  `
  
      const homeButton = createElementFromHTML(html);
      document.getElementsByTagName("body")[0].append(homeButton);
  
  })();