/**
 * 意趣调酒平台 - 可复用导航栏组件
 * 在页面中添加 <div id="nav-container"></div> 即可使用
 */
class NavBar {
    constructor() {
        this.links = [
            { href: '/pages/index.html', label: '调酒创作', icon: '🍸' },
            { href: '/pages/recipes.html', label: '配方库', icon: '📖' },
            { href: '/pages/profile.html', label: '个人主页', icon: '👤' },
            { href: '/pages/assistant.html', label: 'AI助手', icon: '🤖' },
        ];
        this.currentPath = window.location.pathname;
    }

    render() {
        const container = document.getElementById('nav-container');
        if (!container) return;

        const nav = document.createElement('nav');
        nav.className = 'fixed top-0 w-full z-50 glass-card';

        const isLoggedIn = localStorage.getItem('authToken');

        nav.innerHTML = `
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="flex justify-between items-center h-16">
                    <div class="flex items-center">
                        <a href="/pages/index.html" class="text-xl font-bold" style="color: var(--accent-gold);">
                            🍸 意趣调酒平台
                        </a>
                    </div>
                    <div class="flex space-x-2">
                        ${this.links.map(link => {
                            const isActive = this.currentPath.includes(link.href.split('/').pop());
                            return `<a href="${link.href}" class="px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700 transition"
                                style="color: ${isActive ? 'var(--accent-gold)' : 'var(--text-gold)'};">
                                ${link.icon} ${link.label}
                            </a>`;
                        }).join('')}
                        ${isLoggedIn
                            ? `<button id="nav-logout-btn" class="px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700 transition"
                                style="color: var(--text-gold);">🚪 退出</button>`
                            : `<a href="/pages/login.html" class="px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700 transition"
                                style="color: var(--text-gold);">🔑 登录</a>`
                        }
                    </div>
                </div>
            </div>
        `;

        container.appendChild(nav);

        if (isLoggedIn) {
            document.getElementById('nav-logout-btn')?.addEventListener('click', () => {
                localStorage.removeItem('authToken');
                localStorage.removeItem('userId');
                window.location.href = '/pages/login.html';
            });
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new NavBar().render();
});
