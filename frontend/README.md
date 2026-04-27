# Scoring App Frontend

Одностраничный фронтенд для существующих сервисов без изменений бэкенда.

## Запуск

```powershell
npm run dev
```

По умолчанию интерфейс доступен на `http://localhost:5173`.

Встроенный Node-сервер отдает статические файлы и проксирует API:

- `/api/auth/*` -> `http://localhost:8080`
- `/api/scoring` -> `http://localhost:8081`
- `/api/applications/*` -> `http://localhost:8081`

Это позволяет отправлять токен в заголовке `Authorization` и не менять CORS-настройки Java-сервисов.
