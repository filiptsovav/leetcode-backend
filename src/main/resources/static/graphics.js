const ctxAvgTime = document.getElementById('avgTimeChart').getContext('2d');
    const ctxFirstAttempt = document.getElementById('firstAttemptChart').getContext('2d');
    const ctxTagsStats = document.getElementById('tagsStatsChart').getContext('2d');

    let avgTimeChart, firstAttemptChart, tagsStatsChart;

const difficultyStats = JSON.parse(/*[[${difficultyStats}]]*/ '{}');
const topicStats = JSON.parse(/*[[${topicStats}]]*/ '{}');
const dayOfWeekStats = JSON.parse(/*[[${dayOfWeekStats}]]*/ '{}');
const avgTimeData = [5, 10, 15];
const firstAttemptData = [15, 20];
console.log("THIS IS CONSOLE LOGS IN CLIENTS PART");
console.log("Difficulty Stats:", difficultyStats);
console.log("Topic Stats:", topicStats);
console.log("Day of Week Stats:", dayOfWeekStats);
console.log("Average Time Data:", avgTimeData);
console.log("First Attempt Data:", firstAttemptData);
    function updateStatistics() {
        renderCharts();
    }

    function renderCharts() {
        // Удаляем рекурсивный вызов
        if (avgTimeData.length === 0 || firstAttemptData.length === 0) {
            console.error("Данные для графиков пустые!");
            return; // Прерываем выполнение функции
        }

        // Уничтожаем предыдущие графики, если они существуют
        if (avgTimeChart) avgTimeChart.destroy();
        if (firstAttemptChart) firstAttemptChart.destroy();
        if (tagsStatsChart) tagsStatsChart.destroy();

        // График среднего времени решения
        avgTimeChart = new Chart(ctxAvgTime, {
            type: 'pie',
            data: {
                labels: ['Easy', 'Medium', 'Hard'],
                datasets: [{
                    label: 'Среднее время решения',
                    data: avgTimeData,
                    backgroundColor: ['#2ecc71', '#f1c40f', '#e74c3c'],
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });

        // График решения с первого раза
        firstAttemptChart = new Chart(ctxFirstAttempt, {
            type: 'doughnut',
            data: {
                labels: ['С первого раза', 'Не с первого раза'],
                datasets: [{
                    label: 'Процент решения с первого раза',
                    data: firstAttemptData,
                    backgroundColor: ['#3498db', '#95a5a6'],
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }
