FROM node:22-alpine as build

WORKDIR /app

# Copiar package files primeiro (para cache de camadas)
COPY package*.json ./

# Instalar dependências
RUN npm install --legacy-peer-deps

COPY . .

# Argumentos de build
ARG REACT_APP_API_URL=http://backend:8080/api
ENV REACT_APP_API_URL=$REACT_APP_API_URL

# Build da aplicação Vite
RUN npm run build

# Verificar se o build foi criado
RUN ls -la /app/dist/

# Servidor Nginx para produção
FROM nginx:alpine

# Copiar da pasta dist
COPY --from=build /app/dist /usr/share/nginx/html

# Configuração do nginx para React Router
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]