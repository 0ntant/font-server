#!/bin/bash

# Перебираем все TTF-шрифты в текущей директории
for ttf_font in *.ttf; do
    if [ -f "$ttf_font" ]; then
        # Формируем имя WOFF2-шрифта, заменяя расширение .ttf на .woff2
        woff2_font="${ttf_font%.ttf}.woff2"

        # Выполняем преобразование TTF в WOFF2
        woff2_compress "$ttf_font"

        # Проверяем успешность преобразования
        if [ $? -eq 0 ]; then
            echo "Преобразовано: $ttf_font -> $woff2_font"
            # Удаляем исходный TTF-шрифт
            rm "$ttf_font"
        else
            echo "Ошибка при преобразовании $ttf_font в $woff2_font"
        fi
    fi
done

echo "Преобразование завершено."