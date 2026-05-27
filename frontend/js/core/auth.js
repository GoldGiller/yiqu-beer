/**
 * 认证相关逻辑
 */

class AuthManager {
    constructor() {
        this.apiClient = window.apiClient || new ApiClient();
        this.currentTab = 'login';
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.setupP5Background();
        this.checkExistingAuth();
    }
    
    setupEventListeners() {
        // 标签切换
        document.getElementById('login-tab').addEventListener('click', () => {
            this.switchTab('login');
        });
        
        document.getElementById('register-tab').addEventListener('click', () => {
            this.switchTab('register');
        });
        
        // 表单提交
        document.getElementById('login-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });
        
        document.getElementById('register-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleRegister();
        });
        
        // 游客登录
        document.getElementById('guest-login').addEventListener('click', () => {
            this.handleGuestLogin();
        });
    }
    
    switchTab(tabName) {
        this.currentTab = tabName;
        
        // 更新标签样式
        document.getElementById('login-tab').classList.toggle('active', tabName === 'login');
        document.getElementById('register-tab').classList.toggle('active', tabName === 'register');
        
        // 显示/隐藏表单
        document.getElementById('login-form').classList.toggle('hidden', tabName !== 'login');
        document.getElementById('register-form').classList.toggle('hidden', tabName !== 'register');
        
        // 清空消息
        this.clearMessages();
    }
    
    async handleLogin() {
        const username = document.getElementById('login-username').value.trim();
        const password = document.getElementById('login-password').value;
        
        if (!username || !password) {
            this.showMessage('login-message', '请填写所有必填字段', 'error');
            return;
        }
        
        const submitButton = document.querySelector('#login-form button[type="submit"]');
        this.setButtonLoading(submitButton, true);
        
        try {
            const response = await this.apiClient.login(username, password);
            
            if (response.success) {
                // 假设返回的数据中包含用户信息和token
                if (response.user) {
                    localStorage.setItem('username', response.user.username);
                    localStorage.setItem('nickname', response.user.nickname || response.user.username);
                    localStorage.setItem('avatar', response.user.avatar || '');
                }
                
                this.showMessage('login-message', '登录成功！正在跳转...', 'success');
                
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                this.showMessage('login-message', response.message || '登录失败', 'error');
            }
        } catch (error) {
            console.error('登录失败:', error);
            this.showMessage('login-message', '登录失败，请检查网络连接', 'error');
        } finally {
            this.setButtonLoading(submitButton, false);
        }
    }
    
    async handleRegister() {
        const username = document.getElementById('register-username').value.trim();
        const email = document.getElementById('register-email').value.trim();
        const password = document.getElementById('register-password').value;
        const confirmPassword = document.getElementById('register-confirm-password').value;
        const nickname = document.getElementById('register-nickname').value.trim();
        
        // 验证输入
        if (!username || !email || !password) {
            this.showMessage('register-message', '请填写所有必填字段', 'error');
            return;
        }
        
        if (password.length < 6) {
            this.showMessage('register-message', '密码长度至少为6位', 'error');
            return;
        }
        
        if (password !== confirmPassword) {
            this.showMessage('register-message', '两次输入的密码不一致', 'error');
            return;
        }
        
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            this.showMessage('register-message', '请输入正确的邮箱格式', 'error');
            return;
        }
        
        const submitButton = document.querySelector('#register-form button[type="submit"]');
        this.setButtonLoading(submitButton, true);
        
        try {
            const userData = {
                username,
                email,
                password,
                nickname: nickname || username
            };
            
            const response = await this.apiClient.register(userData);
            
            if (response.success) {
                this.showMessage('register-message', '注册成功！请登录', 'success');
                
                // 清空表单
                document.getElementById('register-form').reset();
                
                // 切换到登录标签
                setTimeout(() => {
                    this.switchTab('login');
                }, 2000);
            } else {
                this.showMessage('register-message', response.message || '注册失败', 'error');
            }
        } catch (error) {
            console.error('注册失败:', error);
            this.showMessage('register-message', '注册失败，请检查网络连接', 'error');
        } finally {
            this.setButtonLoading(submitButton, false);
        }
    }
    
    handleGuestLogin() {
        // 创建游客账户信息
        const guestUser = {
            id: 'guest',
            username: '游客',
            nickname: '游客',
            role: 'GUEST'
        };
        
        // 保存游客信息
        localStorage.setItem('userId', guestUser.id);
        localStorage.setItem('username', guestUser.username);
        localStorage.setItem('nickname', guestUser.nickname);
        localStorage.setItem('isGuest', 'true');
        
        // 设置API客户端
        this.apiClient.setUserId(guestUser.id);
        
        this.showMessage('login-message', '游客登录成功！', 'success');
        
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1000);
    }
    
    checkExistingAuth() {
        // 检查是否已有认证信息
        const token = localStorage.getItem('authToken');
        const userId = localStorage.getItem('userId');
        
        if (token && userId) {
            this.apiClient.setAuthToken(token);
            this.apiClient.setUserId(userId);
            
            // 尝试验证token有效性
            this.validateToken();
        }
    }
    
    async validateToken() {
        try {
            // 这里可以调用一个验证token的API
            // 如果token有效，直接跳转到主页
            const userInfo = await this.apiClient.getUserInfo(this.apiClient.getCurrentUserId());
            
            if (userInfo.success) {
                // Token有效，跳转到主页
                window.location.href = 'index.html';
            } else {
                // Token无效，清除本地存储
                this.clearAuth();
            }
        } catch (error) {
            // 验证失败，清除本地存储
            this.clearAuth();
        }
    }
    
    clearAuth() {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
        localStorage.removeItem('nickname');
        localStorage.removeItem('avatar');
        localStorage.removeItem('isGuest');
        
        this.apiClient.clearAuth();
    }
    
    setButtonLoading(button, loading) {
        if (loading) {
            button.disabled = true;
            button.innerHTML = button.innerHTML.replace(/登录|注册/, '处理中...');
        } else {
            button.disabled = false;
            button.innerHTML = button.innerHTML.replace(/处理中.../, this.currentTab === 'login' ? '登录' : '注册');
        }
    }
    
    showMessage(elementId, message, type) {
        const messageElement = document.getElementById(elementId);
        if (!messageElement) return;
        
        messageElement.innerHTML = `<div class="${type}-message">${message}</div>`;
        
        // 3秒后自动清除消息
        setTimeout(() => {
            this.clearMessage(elementId);
        }, 3000);
    }
    
    clearMessage(elementId) {
        const messageElement = document.getElementById(elementId);
        if (messageElement) {
            messageElement.innerHTML = '';
        }
    }
    
    clearMessages() {
        this.clearMessage('login-message');
        this.clearMessage('register-message');
    }
    
    setupP5Background() {
        // P5.js背景动画
        if (typeof p5 !== 'undefined') {
            new p5((p) => {
                let particles = [];
                
                p.setup = () => {
                    const canvas = p.createCanvas(p.windowWidth, p.windowHeight);
                    canvas.parent('p5-container');
                    canvas.id('p5-canvas');
                    
                    // 创建粒子
                    for (let i = 0; i < 40; i++) {
                        particles.push({
                            x: p.random(p.width),
                            y: p.random(p.height),
                            vx: p.random(-0.3, 0.3),
                            vy: p.random(-0.3, 0.3),
                            size: p.random(2, 8),
                            opacity: p.random(0.1, 0.25)
                        });
                    }
                };
                
                p.draw = () => {
                    p.clear();
                    
                    particles.forEach(particle => {
                        p.fill(212, 175, 55, particle.opacity * 255);
                        p.noStroke();
                        p.circle(particle.x, particle.y, particle.size);
                        
                        particle.x += particle.vx;
                        particle.y += particle.vy;
                        
                        if (particle.x < 0 || particle.x > p.width) particle.vx *= -1;
                        if (particle.y < 0 || particle.y > p.height) particle.vy *= -1;
                    });
                };
                
                p.windowResized = () => {
                    p.resizeCanvas(p.windowWidth, p.windowHeight);
                };
            });
        }
    }
}

// 初始化认证管理器
document.addEventListener('DOMContentLoaded', () => {
    new AuthManager();
});