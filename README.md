Motivation
==========

Starting the Enterprise Shell revolution!

Getting Started
===============

You need 3 environment variables to be able to run it:
- SLACK_TOKEN: to connect to Slack, obtain it from slack.com
- SLACK_WEBSOCKET_ID: a random base64 string
- GITHUB_TOKEN: to access GitHub APIs, create one from your account

Then `sbt run`!

Maintainer
==========
Hubert Behaghel <behaghel@gmail.com> [@behaghel](http://twitter.com/behaghel)

Contributing
============

It is recommended to install the GitHub CLI [hub](https://hub.github.com/).

```bash
hub clone behaghel/tobyks
cd tobyks
git checkout -b my-feature
# code a lot with thorough awesome tests
sbt test
git commit -m "Genius feature!"
hub fork
hub push YOUR_GITHUB_LOGIN my-feature
hub pull-request
```