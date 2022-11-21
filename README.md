
Microservice based webmail client built with ReactJS and Spring.

Based Isotope Mail Client, by Marc Nuri.

## Introduction

This webmail client is still in a very early stage, use at your own risk.

## TL;DR

If you just want to check out the current status of the project you can deploy the application
using the example docker-compose.

Если вы просто хотите проверить текущий статус проекта, вы можете развернуть приложение, используя пример docker-compose.

* **docker-compose.yml** для "продакшен" развертывания, с поддержкой tls
* **docker-compose.noTLS.yml** для локального запуска.
* **docker-compose.spring_local.yml** для локального запуска и разработки серверной части, серверную часть(спринг) 
необходимо запускать на хост системе. Не забудьте прокинуть server.env

Just run the following commands:

```
git clone https://github.com/dhomo/crm-mail.git
cd crm-mail
cp server.env.sample server.env
docker-compose up --detach --file docker-compose.noTLS.yml
```
for windows:
```
git clone https://github.com/dhomo/crm-mail.git
cd crm-mail
copy server.env.sample server.env
docker-compose up --detach --file docker-compose.noTLS.yml
```
**server.env** Of course, the values for the variables (password and salt) must be changed.

Чтоб почистить за собой образы сборки
```
docker image prune -f
```

If you have changed something in the code, then run:

```
docker-compose up --build --detach --file docker-compose.noTLS.yml
```

Point your browser to [localhost](http://localhost) and login using the credentials of your mailserver.

## License

 [Apache 2.0 Licensed](./LICENSE).

