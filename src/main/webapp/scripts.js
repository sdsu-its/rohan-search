/**
 * OnSubmit handler for search requests
 */
function doSearch() {
    var term = document.getElementById("search").value;
    update('search_results', "Searching..." +
        "<br/>" +
        "<br/>" +
        "This may take a moment, depending on how board your search term is.");
    get("./rest/search?t=" + term, true);
}

/**
 * Make an HTTP GET Request
 *
 * @param url (String) URL to which the request should be made
 * @param callback (Boolean) If the Return Information is desired
 */
function get(url, callback) {
    var xmlHttp = new XMLHttpRequest();
    if (callback) {
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4) {
                update('search_results', xmlHttp.responseText);
            }
        };
    }

    xmlHttp.open('GET', url);
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
    origContent.innerHTML = text;
}

//noinspection JSUnusedGlobalSymbols
/**
 * Get the user's email to send them the streaming ticket
 *
 * @param name (String) Name of the file for which the ticket is being requested.
 * @param path (String) Path of the file for which the ticket is being requested.
 */
function getEmail(name, path, attach) {
    var email = prompt("Enter your Email");
    if (email != null && email != " ") {
        get("./rest/email?name=" + name + "&path=" + path + "&email=" + email + "&attach=" + attach, false);
        alert("Sent Streaming Ticket to " + email);
    }
    else {
        alert("No valid email was entered. Aborting!")
    }
}