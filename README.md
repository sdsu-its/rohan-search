ROHAN Search
============

### Required Components
- Server running [ROHAN Crawler](https://github.com/sdsu-its/rohan-crawler) on a Cron Job
- Postgres DB
- TomCat Server
- Email Server (Gmail or Mandrill)

## Usage
All the environment variables are stored in ```config.properites```, a sample of which is provided in ```config_sample.properties```.

The DB connector is written to talk directly with a Heroku Postgres Database via SSL (which allows for usage outside of the Herkou Cloud).

### Endpoints
Package includes an echo endpoint for testing which can be reached at /rest/echo?m=Hi!

Searching can be done 2 ways, making a GET request to /rest/search?q=replace_me which returns a HTML page, where as making a POST request to /rest/search with query=replace_me as a Form Encoded Application in the POST Body will return a JSON Array with the files that match the search query.
