FROM alpine:latest

# Установите OpenJDK 17 и другие необходимые пакеты
RUN apk update && apk add --no-cache openjdk17

# Установите Git, g++, make и другие необходимые пакеты
RUN apk add --no-cache git g++ make

# Клонирование репозитория woff2 и сборка
RUN git clone --recursive https://github.com/google/woff2.git && \
    cd woff2 && \
    make clean all

# Копирование собранного файла в /bin
RUN cp /woff2/woff2_compress /bin/woff2_compress

# Задайте рабочую директорию для приложения
WORKDIR /app

# Копирование вашего Java-приложения и ресурсов
COPY target/backend-service-1.0.jar .

COPY src/main/resources/temp ./temp
COPY src/main/resources/fonts ./fonts
COPY src/main/resources/static ./static
COPY src/main/resources/templates ./templates

# Задайте переменную окружения JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/default-jvm

# Задайте порт для приложения
ENV PORT=8080

# Откройте порт, который будет слушать ваше приложение
EXPOSE $PORT

# Запуск вашего Java-приложения
CMD ["java", "-jar", "backend-service-1.0.jar"]
