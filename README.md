# Hive Studio Client

Клиентская часть курсового проекта `Hive Studio`, написанная на `Jetpack Compose`.

## Что реализовано

- отдельный экран `Вход / Регистрация`
- экран `Профиль` с редактированием имени, аватара, города, контакта и описания
- публичный каталог всех битов
- поиск, сортировка и фильтры в каталоге
- экран `Мои биты`
- создание и редактирование бита
- экран аналитики бита
- MP3-превью
- загрузка реальной обложки и `mp3`
- хранение сессии продюсера

## Технологии

- `Kotlin`
- `Jetpack Compose`
- `Navigation Compose`
- `Retrofit`
- `OkHttp`

## Серверный адрес

Клиент использует локальный адрес эмулятора Android:

[`ApiConfig.kt`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-client/app/src/main/java/com/hivestudio/data/remote/ApiConfig.kt)

```kotlin
const val BASE_URL: String = "http://10.0.2.2:8081/"
```

Это значит:

- сервер должен быть запущен на Mac
- эмулятор будет обращаться к нему через `10.0.2.2`

## Запуск

Открывай в Android Studio именно этот проект:

[`/Users/matthew/AndroidStudioProjects/kurs/hive-studio-client`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-client)

Дальше:

1. Дождись `Gradle Sync`
2. Запусти Android-эмулятор
3. Запусти `app`

Проверка сборки через терминал:

```bash
cd /Users/matthew/AndroidStudioProjects/kurs/hive-studio-client
./gradlew :app:compileDebugKotlin
```

## Что проверить в приложении

1. `Каталог`
2. `Статистика`
3. `Мои биты`
4. `Профиль`
5. отдельный экран `Вход / Регистрация`
6. загрузку и редактирование бита
7. аналитику и MP3-превью

## Полезные ссылки

- Демонстрационный сценарий: [`../hive-studio-server/docs/demo-flow.md`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-server/docs/demo-flow.md)
