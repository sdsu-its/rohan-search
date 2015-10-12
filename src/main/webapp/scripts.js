/**
 * OnSubmit handler for search requests
 */
function doSearch() {
    var term = document.getElementById("search").value;
    get("./rest/search?t=" + term, true, false);
}

/**
 * Make an HTTP GET Request
 *
 * @param url (String) URL to which the request should be made
 * @param callback (Boolean) If the Return Information is desired
 * @param async (Boolean) Can the task be done in the background?
 */
function get(url, callback, async) {
    var xmlHttp = new XMLHttpRequest();
    if (callback) {
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4) {
                update('search_results', xmlHttp.responseText);
            }
        };
    }

    xmlHttp.open('GET', url, async); // true for asynchronous
    xmlHttp.send();
}

/**
 * Update the contents of a <div> with a shadow-root.
 *
 * @param id (String) ID of the Div to update
 * @param text (String) content to insert.
 */
function update(id, text) {
    // Get the <div> above with its content
    var origContent = document.getElementById(id);
    // Create the first shadow root
    var shadowroot = origContent.createShadowRoot();
    shadowroot.innerHTML = text;
}

/**
 * Get the user's email to send them the streaming ticket
 *
 * @param name (String) Name of the file for which the ticket is being requested.
 * @param path (String) Path of the file for which the ticket is being requested.
 */
function getEmail(name, path) {
    var email = prompt("Enter your Email");
    get("./rest/email?name=" + name + "&path=" + path + "&email=" + email, false, true);
    alert("Sent Streaming Ticket to " + email);
}