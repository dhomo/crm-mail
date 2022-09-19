FROM nginx:1.15.2-alpine

EXPOSE 80

COPY nginx-default.conf /etc/nginx/conf.d/default.conf
COPY ./dist /usr/share/nginx/html

CMD nginx -g 'daemon off;'

