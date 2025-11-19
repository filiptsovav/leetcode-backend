const express = require('express');
const app = express();
const path = require('path');

// Папка для статических файлов
app.use(express.static(path.join(__dirname, 'public')));

// Пример данных задач для разных пользователей
const userTasks = {
    user1: [
        {
            title: 'Задача 1',
            difficulty: 'easy',
            description: 'Краткое описание задачи 1.',
            tags: ['Алгоритмы', 'Математика']
        },
        {
            title: 'Задача 2',
            difficulty: 'medium',
            description: 'Краткое описание задачи 2.',
            tags: ['Структуры данных', 'Поиск']
        }
    ],
    user2: [
        {
            title: 'Задача 3',
            difficulty: 'hard',
            description: 'Краткое описание задачи 3.',
            tags: ['Динамическое программирование']
        },
        {
            title: 'Задача 4',
            difficulty: 'easy',
            description: 'Краткое описание задачи 4.',
            tags: ['Геометрия']
        }
    ],
    // Добавьте больше пользователей и их задачи по мере необходимости
};

// Пример функции для получения задач пользователя
function getUserTasks(userId) {
    return userTasks[userId] || [];
}

// Маршрут для получения задач
app.get('/api/tasks', (req, res) => {
    // Здесь вы должны определить, как вы получаете ID пользователя
    const userId = req.query.userId || 'user1'; // Замените на реальный способ получения ID пользователя
    const tasks = getUserTasks(userId);
    res.json(tasks);
});

// Маршрут для страницы предложений задач
app.get('/taskSuggestion', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'taskSuggestion.html'));
});

// Запуск сервера
const PORT = process.env.PORT || 8080; // Установите порт на 8080
app.listen(PORT, () => {
    console.log(`Сервер запущен на http://localhost:${PORT}`);
});