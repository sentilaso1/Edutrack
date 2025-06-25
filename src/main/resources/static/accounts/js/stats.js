let revenueChart;
let currentPeriod = /*[[${currentPeriod}]]*/ 'week';

// Initialize charts on page load
document.addEventListener('DOMContentLoaded', function () {
        initializeRevenueChart();
        addAnimationDelays();
});

function addAnimationDelays() {
        const elements = document.querySelectorAll('.animate-fade-in');
        elements.forEach((el, index) => {
                el.style.animationDelay = `${index * 0.1}s`;
        });
}

function changePeriod(period) {
        currentPeriod = period;

        // Update active button with loading state
        const clickedBtn = event.target.closest('.period-btn');
        const originalContent = clickedBtn.innerHTML;

        // Update active button
        document.querySelectorAll('.period-btn').forEach(btn => {
                btn.classList.remove('active');
        });
        clickedBtn.classList.add('active');

        // Show loading
        clickedBtn.innerHTML = '<span class="loading-spinner"></span>';

        // Update data
        updateDashboardData(period).finally(() => {
                clickedBtn.innerHTML = originalContent;
        });
}

function updateDashboardData(period) {
        const promises = [
                updateSummaryStats(period),
                updateRevenueChart(period),
                updateTopMentors(period)
        ];

        return Promise.all(promises);
}

function updateSummaryStats(period) {
        return fetch(`/manager/api/summary-stats?period=${period}`)
                .then(response => response.json())
                .then(data => {
                        // Animate number changes
                        animateValue('totalRevenue', data.totalRevenue, formatCurrency);
                        animateValue('totalMentors', data.totalMentors);
                        animateValue('avgRevenuePerMentor', data.avgRevenuePerMentor / 1000000, (val) => `${val.toFixed(1)}M₫`);

                        document.getElementById('revenueGrowth').textContent = `+${data.revenueGrowth.toFixed(1)}% so với tháng trước`;
                        updateGrowthIndicator('revenueGrowth', data.revenueGrowth);
                })
                .catch(error => {
                        console.error('Error updating summary stats:', error);
                        showNotification('Không thể cập nhật dữ liệu thống kê', 'error');
                });
}

function animateValue(elementId, targetValue, formatter = null) {
        const element = document.getElementById(elementId);
        const startValue = parseFloat(element.textContent.replace(/[^\d.-]/g, '')) || 0;
        const duration = 1000;
        const startTime = Date.now();

        function updateValue() {
                const now = Date.now();
                const progress = Math.min((now - startTime) / duration, 1);
                const currentValue = startValue + (targetValue - startValue) * easeOutCubic(progress);

                element.textContent = formatter ? formatter(currentValue) : Math.round(currentValue);

                if (progress < 1) {
                        requestAnimationFrame(updateValue);
                }
        }

        updateValue();
}

function easeOutCubic(t) {
        return 1 - Math.pow(1 - t, 3);
}

function updateGrowthIndicator(elementId, value) {
        const element = document.getElementById(elementId);
        element.className = value >= 0 ? 'growth-positive' : 'growth-negative';
}

function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'error' ? 'danger' : type} position-fixed`;
        notification.style.cssText = `
top: 20px;
right: 20px;
z-index: 9999;
min-width: 300px;
animation: slideInRight 0.3s ease-out;
`;
        notification.innerHTML = `
<i class="fas fa-${type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
<span>${message}</span>
<button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
`;

        document.body.appendChild(notification);

        // Auto remove after 5 seconds
        setTimeout(() => {
                if (notification.parentElement) {
                        notification.style.animation = 'slideOutRight 0.3s ease-in';
                        setTimeout(() => notification.remove(), 300);
                }
        }, 5000);
}

function initializeRevenueChart() {
        const ctx = document.getElementById('revenueChart').getContext('2d');

        revenueChart = new Chart(ctx, {
                type: 'line',
                data: {
                        labels: [],
                        datasets: [{
                                label: 'Doanh Thu (₫)',
                                data: [],
                                borderColor: '#667eea',
                                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                                borderWidth: 3,
                                fill: true,
                                tension: 0.4,
                                pointBackgroundColor: '#667eea',
                                pointBorderColor: '#ffffff',
                                pointBorderWidth: 2,
                                pointRadius: 6,
                                pointHoverRadius: 8,
                                pointHoverBackgroundColor: '#764ba2',
                                pointHoverBorderColor: '#ffffff',
                                pointHoverBorderWidth: 3
                        }]
                },
                options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                                legend: {
                                        display: true,
                                        position: 'top',
                                        labels: {
                                                usePointStyle: true,
                                                padding: 20,
                                                font: {
                                                        size: 14,
                                                        weight: '500'
                                                }
                                        }
                                },
                                tooltip: {
                                        backgroundColor: 'rgba(0,0,0,0.8)',
                                        titleColor: '#ffffff',
                                        bodyColor: '#ffffff',
                                        borderColor: '#667eea',
                                        borderWidth: 1,
                                        cornerRadius: 10,
                                        displayColors: false,
                                        callbacks: {
                                                label: function (context) {
                                                        return `Doanh thu: ${formatCurrency(context.raw)}`;
                                                }
                                        }
                                }
                        },
                        scales: {
                                x: {
                                        grid: {
                                                display: false
                                        },
                                        ticks: {
                                                font: {
                                                        size: 12,
                                                        weight: '500'
                                                }
                                        }
                                },
                                y: {
                                        beginAtZero: true,
                                        grid: {
                                                color: 'rgba(0,0,0,0.1)',
                                                borderDash: [5, 5]
                                        },
                                        ticks: {
                                                callback: function (value) {
                                                        return formatCurrency(value);
                                                },
                                                font: {
                                                        size: 12,
                                                        weight: '500'
                                                }
                                        }
                                }
                        },
                        interaction: {
                                intersect: false,
                                mode: 'index'
                        },
                        animation: {
                                duration: 2000,
                                easing: 'easeOutCubic'
                        }
                }
        });

        // Load initial data
        updateRevenueChart(currentPeriod);
}

function updateRevenueChart(period) {
        return fetch(`/manager/api/revenue-chart?period=${period}`)
                .then(response => response.json())
                .then(data => {
                        const labels = data.map(item => item.label);
                        const revenues = data.map(item => item.revenue);

                        revenueChart.data.labels = labels;
                        revenueChart.data.datasets[0].data = revenues;
                        revenueChart.update('active');
                })
                .catch(error => {
                        console.error('Error updating revenue chart:', error);
                        // Fallback to mock data
                        loadMockRevenueData(period);
                });
}

function loadMockRevenueData(period) {
        let labels, data;

        switch (period) {
                case 'week':
                        labels = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];
                        data = [120000000, 150000000, 180000000, 200000000, 170000000, 220000000, 190000000];
                        break;
                case 'month':
                        labels = ['Tuần 1', 'Tuần 2', 'Tuần 3', 'Tuần 4'];
                        data = [800000000, 950000000, 1100000000, 1200000000];
                        break;
                case 'quarter':
                        labels = ['Tháng 1', 'Tháng 2', 'Tháng 3'];
                        data = [2500000000, 2800000000, 3200000000];
                        break;
                case 'year':
                        labels = ['Q1', 'Q2', 'Q3', 'Q4'];
                        data = [8500000000, 9200000000, 9800000000, 10500000000];
                        break;
                default:
                        labels = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];
                        data = [120000000, 150000000, 180000000, 200000000, 170000000, 220000000, 190000000];
        }

        revenueChart.data.labels = labels;
        revenueChart.data.datasets[0].data = data;
        revenueChart.update('active');
}

function updateTopMentors(period) {
        return fetch(`/manager/api/top-mentors?period=${period}`)
                .then(response => response.json())
                .then(data => {
                        const container = document.getElementById('topMentorsList');
                        container.innerHTML = '';

                        data.forEach((mentor, index) => {
                                const mentorElement = document.createElement('div');
                                mentorElement.className = 'mentor-item';
                                mentorElement.style.opacity = '0';
                                mentorElement.style.transform = 'translateX(-20px)';

                                mentorElement.innerHTML = `
            <div class="d-flex align-items-center">
                <span class="badge bg-primary">${index + 1}</span>
                <span>${mentor.mentorName}</span>
            </div>
            <strong>${mentor.formattedRevenue}</strong>
        `;

                                container.appendChild(mentorElement);

                                // Animate in
                                setTimeout(() => {
                                        mentorElement.style.transition = 'all 0.3s ease-out';
                                        mentorElement.style.opacity = '1';
                                        mentorElement.style.transform = 'translateX(0)';
                                }, index * 100);
                        });
                })
                .catch(error => {
                        console.error('Error updating top mentors:', error);
                        showNotification('Không thể cập nhật danh sách mentor hàng đầu', 'error');
                });
}

function formatCurrency(amount) {
        if (amount == null) return '0₫';
        return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND',
                maximumFractionDigits: 0
        }).format(amount);
}

// Add smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                        target.scrollIntoView({
                                behavior: 'smooth',
                                block: 'start'
                        });
                }
        });
});

// Add loading states for cards
function addLoadingState(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
                element.style.opacity = '0.5';
                element.style.pointerEvents = 'none';
        }
}

function removeLoadingState(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
                element.style.opacity = '1';
                element.style.pointerEvents = 'auto';
        }
}

// Add keyboard navigation for period selector
document.addEventListener('keydown', function (e) {
        if (e.ctrlKey || e.metaKey) {
                const periodMap = {
                        '1': 'week',
                        '2': 'month',
                        '3': 'quarter',
                        '4': 'year'
                };

                if (periodMap[e.key]) {
                        e.preventDefault();
                        const button = document.querySelector(`[onclick="changePeriod('${periodMap[e.key]}')"]`);
                        if (button) {
                                button.click();
                        }
                }
        }
});

// Add intersection observer for animations
const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
                if (entry.isIntersecting) {
                        entry.target.style.animationPlayState = 'running';
                }
        });
}, observerOptions);

// Observe all animated elements
document.querySelectorAll('.animate-fade-in').forEach(el => {
        el.style.animationPlayState = 'paused';
        observer.observe(el);
});

// Add CSS for notifications
const style = document.createElement('style');
style.textContent = `
@keyframes slideInRight {
from {
    transform: translateX(100%);
    opacity: 0;
}
to {
    transform: translateX(0);
    opacity: 1;
}
}

@keyframes slideOutRight {
from {
    transform: translateX(0);
    opacity: 1;
}
to {
    transform: translateX(100%);
    opacity: 0;
}
}

.btn-close {
background: none;
border: none;
font-size: 1.2rem;
opacity: 0.7;
cursor: pointer;
margin-left: 10px;
}

.btn-close:hover {
opacity: 1;
}
`;
document.head.appendChild(style);

// Initialize tooltips if Bootstrap is available
if (typeof bootstrap !== 'undefined') {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
        });
}

// Add performance monitoring
window.addEventListener('load', function () {
        if ('performance' in window) {
                const loadTime = Math.round(performance.now());
                console.log(`Dashboard loaded in ${loadTime}ms`);

                if (loadTime > 3000) {
                        showNotification('Dashboard đã tải xong nhưng hơi chậm. Hãy kiểm tra kết nối mạng.', 'warning');
                }
        }
});