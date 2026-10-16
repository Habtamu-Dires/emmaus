# build Stage
FROM node:22 AS build-stage

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build --prod

# run time
FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build-stage /app/dist/emmaus_ui/browser /usr/share/nginx/html

COPY start.sh ./

RUN chmod +x start.sh

CMD ["sh","start.sh"]

EXPOSE 80