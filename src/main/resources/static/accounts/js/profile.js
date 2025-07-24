// Hàm chuyển tab
function showTab(tabId) {
        document.querySelectorAll('.tab-content').forEach(tab => tab.classList.add('hidden'));
        document.querySelectorAll('.tab-button').forEach(button => {
            button.classList.remove('text-blue-600', 'border-blue-600');
            button.classList.add('text-gray-600', 'border-transparent');
        });
    
        const tabToShow = document.getElementById(tabId);
        if (tabToShow) {
            tabToShow.classList.remove('hidden');
        }
    
        const button = document.querySelector(`button[onclick="showTab('${tabId}')"]`);
        if (button) {
            button.classList.add('text-blue-600', 'border-blue-600');
        }
    }
    
    // Đảm bảo DOM đã sẵn sàng trước khi truy cập phần tử
    window.addEventListener('DOMContentLoaded', function () {
        const avatarInput = document.getElementById('avatar-input');
        if (avatarInput) {
            avatarInput.addEventListener('change', function (event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        const preview = document.getElementById('avatar-preview');
                        if (preview) {
                            preview.src = e.target.result;
                        }
                    };
                    reader.readAsDataURL(file);
                }
            });
        }
    
        // Xử lý chuyển tab theo hash trên URL
        const hash = window.location.hash.replace('#', '');
        const validTabs = ['profile', 'edit', 'avatar'];
        const tabToShow = validTabs.includes(hash) ? hash : 'profile';
        showTab(tabToShow);
    });
    