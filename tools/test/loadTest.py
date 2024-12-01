import requests
import concurrent.futures
import random

# Базовый URL для GET-запроса 127.0.0.1 only pls :(
base_url = "http://127.0.0.1:8080/font/api/v1/get-font-bin/"
base_url1= "http://127.0.0.1:8080/tagCategory/api/v1/get-all"

# Количество запросов, которые вы хотите выполнить
num_requests = 1750

# Функция для отправки GET-запроса к base_url
def send_request_base_url(_):
    random_param = random.randint(1, 4)
    url = f"{base_url}{random_param}"
    response = requests.get(url)
    if response.status_code == 200:
        # Можете добавить проверки здесь, если нужно
        pass

# Функция для отправки GET-запроса к base_url1
def send_request_base_url1(_):
    response = requests.get(base_url1)
    if response.status_code == 200:
        # Можете добавить проверки здесь, если нужно
        pass

# Создание пула потоков для параллельной отправки запросов к base_url
with concurrent.futures.ThreadPoolExecutor(max_workers=num_requests) as executor:
    executor.map(send_request_base_url, range(num_requests))

# Создание пула потоков для параллельной отправки запросов к base_url1
with concurrent.futures.ThreadPoolExecutor(max_workers=num_requests) as executor1:
    executor1.map(send_request_base_url1, range(num_requests))

# Здесь нет необходимости в sleep, так как запросы отправляются асинхронно
