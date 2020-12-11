[![Build Status](https://travis-ci.org/peryza/AAA.svg?branch=master)](https://travis-ci.org/github/peryza/AAA)
[![codecov](https://peryza.github.io/AAA/)](https://peryza.github.io/AAA/)

# [AAA](https://github.com/peryza/AAA.git)
Проект для реализации Аутентификации, Авторизации и Аккаунтинга

## Наборы требований 
1. [План по первому набору требований.](ROADMAP1.md)
2. [План по второму набору требований.](ROADMAP2.md)

## Build & Run & Test
+ Build.sh - скрипт для сборки приложения 
+ Run.sh - скрипт для запуска приложения
+ Test.sh - скрипт для тестирования приложения

## Сборка проекта
Для того чтобы собрать проект, в консоли прописывается командп BUILD.sh
Также для сборки можно использовать такую команду 

```kotlinc src -cp kotlinx-cli-0.2.1.jar -include-runtime -d app.jar```

## Запуск приложения 
Чтобы запустить собранное приложение, в консоли нужно выполнить команду RUN.sh ( ваши параметры)

```java -jar app.jar (ваши параметры)```

## Тестирование приложение
Для того, чтобы протестировать собранное приложение, пропишите в командной строке TEST.sh
