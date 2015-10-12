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

### See the [Wiki](https://github.com/sdsu-its/rohan-search/wiki) for API Usage Information.
