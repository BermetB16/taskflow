# TaskFlow API

REST API для управления задачами с приоритетами, дедлайнами, тегами и статистикой.

## Технологии
- Java 25
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Swagger UI (springdoc-openapi)
- Maven

## Запуск
1. Открыть проект в IntelliJ IDEA
2. Запустить `TaskflowApplication.java`
3. API доступен на `http://localhost:8000`

## Полезные ссылки
- Swagger UI: `http://localhost:8000/swagger-ui.html`
- H2 Console: `http://localhost:8000/h2` (JDBC URL: `jdbc:h2:file:./taskflow-db`, User: `sa`, Password: пусто)

## Эндпоинты

| Метод | URL | Описание |
|---|---|---|
| GET | /api/todos | Список задач (фильтры: status, priority, tag, sort) |
| POST | /api/todos | Создать задачу |
| GET | /api/todos/{id} | Получить задачу |
| PATCH | /api/todos/{id} | Обновить задачу |
| POST | /api/todos/{id}/complete | Завершить задачу |
| DELETE | /api/todos/{id} | Удалить задачу |
| GET | /api/todos/{id}/history | История изменений |
| GET | /api/stats | Статистика |
| GET | /api/tags | Все теги |

## ООП-принципы

- **Инкапсуляция**: поля private, доменные методы `isOverdue()`, `markDone()` в `Todo`
- **Наследование**: `TodoNotFoundException extends RuntimeException`, репозитории наследуют `JpaRepository`
- **Полиморфизм**: Spring Data генерирует реализации интерфейсов, Comparator в сервисе
- **Абстракция**: слои Controller → Service → Repository → Entity
- **DI**: зависимости через конструктор
- **Records**: все DTO — неизменяемые record-классы