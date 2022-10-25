
Microservice based webmail client built with ReactJS and Spring.

Based Isotope Mail Client main contributors are:- Marc Nuri

## Introduction

This webmail client is still in a very early stage, use at your own risk.

## TL;DR

If you just want to check out the current status of the project you can deploy the application
using the example docker-compose.

Just run the following commands:

```
git clone https://github.com/dhomo/crm-mail.git
cp server.env.sample server.env
docker-compose up -d
```
for windows:
```
git clone https://github.com/dhomo/crm-mail.git
cd crm-mail
copy server.env.sample server.env
docker-compose up -d
```

### server.env 
Of course, the values for the variables (password and salt) must be set new.


If you have changed something in the code, then run:

```
docker-compose up --build -d
```

Point your browser to [localhost](http://localhost) and login using the credentials of your mailserver.

## License

Isotope is [Apache 2.0 Licensed](./LICENSE).

