FROM node:12 as build
WORKDIR /react-app
COPY package*.json .
RUN yarn install --network-timeout 100000
COPY . .
RUN yarn run build


FROM nginx:stable-alpine
COPY nginx-default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /react-app/dist /usr/share/nginx/html
EXPOSE 9009