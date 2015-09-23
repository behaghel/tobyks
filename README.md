Motivation
==========

Starting the Enterprise Shell revolution!

Getting Started
===============

You need 3 environment variables to be able to run it:
- SLACK_TOKEN: to connect to Slack, obtain it from slack.com
- SLACK_WEBSOCKET_ID: a random base64 string
- GITHUB_TOKEN: to access GitHub APIs, create one from your account

You also need a mongo instance running locally (default port, no auth
…for now!).

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

Fix issse while building 
============
For building the project you need to clone https://github.com/softprops/hubcat and change the scala and scalatest version.

In  build.sbt change 
scalaVersion := "2.11.7"
and 
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

Also you need to publishLocal to create repos in ivy locally
