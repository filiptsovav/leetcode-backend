# LeetCode Planner — Backend

## Описание проекта
LeetCodePlanner — это больше чем просто трекер решенных задач. Это персональный цифровой тренер для подготовки к техническим собеседованиям. Наше видение — превратить хаотичный и часто стрессовый процесс решения задач на LeetCode в структурированный, измеримый и предсказуемый путь к успеху. Мы видим мир, где каждый разработчик может эффективно подготовиться к своей dream job, сосредоточившись на качестве обучения, а не на рутинном управлении своим прогрессом.

## Стек технологий
Бэкенд (серверная часть): Java 17+ с использованием Spring Boot 3.x - обеспечит надежную, масштабируемую и безопасную серверную часть с богатой экосистемой
База данных: MySQL 8.x - реляционная база данных для хранения пользовательских данных, планов занятий и статистики
API коммуникация: REST API между фронтендом и бэкендом с форматом JSON
Синхронизация с LeetCode: Spring WebClient для асинхронных HTTP запросов к LeetCode API

## Роли пользователей и варианты использования
### Роли:
1. **Гость** — может просматривать публичную информацию.
<img width="766" height="817" alt="image" src="https://github.com/lesiayarm/leetcode_project/tree/main/UMLDiagrams/Guest.svg" />
2. **Авторизованный пользователь** — имеет доступ к личному кабинету и базовым функциям.
   <img width="766" height="817" alt="image" src="https://github.com/lesiayarm/leetcode_project/tree/main/UMLDiagrams/User.svg" />
3. **Администратор** — управляет пользователями, контентом и настройками системы.
   <img width="766" height="817" alt="image" src="https://github.com/lesiayarm/leetcode_project/tree/main/UMLDiagrams/Admin.svg" />

### Диаграмма вариантов использования:
![Use Case Diagram](https://github.com/lesiayarm/leetcode_project/tree/main/UMLDiagrams)
<img width="766" height="817" alt="image" src="https://github.com/lesiayarm/leetcode_project/tree/main/UMLDiagrams/MainDiag.svg" />




## Схема базы данных
### ER-диаграмма:
![ER Diagram](link/to/er-diagram.png)

## API
### Основные endpoints:
- `GET /api/users` — список пользователей
- `POST /api/auth/login` — авторизация
- `PUT /api/users/{id}` — обновление данных пользователя
- `DELETE /api/users/{id}` — удаление пользователя

Полная документация API доступна по ссылке: [Swagger UI](http://localhost:8000/docs)
