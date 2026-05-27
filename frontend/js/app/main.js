/**
 * 调酒模拟器主要逻辑 - 更新版本
 * 集成后端API
 */

class CocktailMakerApp {
    constructor() {
        this.apiClient = window.apiClient;
        this.selectedIngredients = {
            base: null,
            juice: null,
            syrup: null,
            garnish: null
        };
        this.selectedMood = null;
        this.preferences = {
            sweetness: 5,
            sourness: 5,
            alcohol: 5,
            fruitiness: 5
        };
        this.availableIngredients = [];
        this.currentUser = null;
        
        this.init();
    }
    
    async init() {
        try {
            await this.loadIngredients();
            this.setupEventListeners();
            this.initializeSliders();
            this.setupP5Background();
            this.checkAuthentication();
            this.updatePreview();
        } catch (error) {
            console.error('应用初始化失败:', error);
            this.showNotification('应用初始化失败，请刷新页面重试', 'error');
        }
    }
    
    async loadIngredients() {
        try {
            const response = await this.apiClient.getIngredients();
            if (response.success && response.data) {
                this.availableIngredients = response.data.content || [];
                this.renderIngredients();
            }
        } catch (error) {
            console.error('加载材料失败:', error);
            // 使用默认材料数据
            this.loadDefaultIngredients();
        }
    }
    
    loadDefaultIngredients() {
        // 默认材料数据
        this.availableIngredients = [
            // 基酒
            { id: 1, name: '伏特加', type: 'BASE', emoji: '🍸', description: '无色无味的蒸馏酒' },
            { id: 2, name: '威士忌', type: 'BASE', emoji: '🥃', description: '用谷物酿造的烈酒' },
            { id: 3, name: '朗姆酒', type: 'BASE', emoji: '🏴‍☠️', description: '用甘蔗糖蜜酿造' },
            { id: 4, name: '金酒', type: 'BASE', emoji: '🌿', description: '加入杜松子等香料' },
            { id: 5, name: '龙舌兰', type: 'BASE', emoji: '🌵', description: '墨西哥特产' },
            { id: 6, name: '白兰地', type: 'BASE', emoji: '🍇', description: '用葡萄酿造' },
            
            // 果汁
            { id: 7, name: '柠檬汁', type: 'JUICE', emoji: '🍋', description: '新鲜柠檬榨汁' },
            { id: 8, name: '青柠汁', type: 'JUICE', emoji: '🟢', description: '青柠榨汁' },
            { id: 9, name: '橙汁', type: 'JUICE', emoji: '🍊', description: '新鲜橙子榨汁' },
            { id: 10, name: '西柚汁', type: 'JUICE', emoji: '🌸', description: '西柚榨汁' },
            
            // 糖浆
            { id: 11, name: '简单糖浆', type: 'SYRUP', emoji: '🍯', description: '糖水1:1比例' },
            { id: 12, name: '香草糖浆', type: 'SYRUP', emoji: '🌸', description: '香草风味' },
            { id: 13, name: '榛果糖浆', type: 'SYRUP', emoji: '🌰', description: '榛果风味' },
            { id: 14, name: '红石榴糖浆', type: 'SYRUP', emoji: '🍒', description: '红石榴风味' }
        ];
        this.renderIngredients();
    }
    
    renderIngredients() {
        const container = document.querySelector('.glass-card');
        if (!container) return;
        
        // 清空现有内容
        const existingSections = container.querySelectorAll('.mb-6');
        existingSections.forEach(section => section.remove());
        
        // 按类型分组渲染材料
        const types = ['BASE', 'JUICE', 'SYRUP', 'GARNISH'];
        const typeNames = {
            'BASE': '基酒',
            'JUICE': '果汁',
            'SYRUP': '糖浆',
            'GARNISH': '装饰物'
        };
        
        types.forEach(type => {
            const ingredients = this.availableIngredients.filter(ing => ing.type === type);
            if (ingredients.length === 0) return;
            
            const section = document.createElement('div');
            section.className = 'mb-6';
            section.innerHTML = `
                <h3 class="text-lg font-semibold mb-3" style="color: var(--accent-gold);">${typeNames[type]}</h3>
                <div class="grid grid-cols-2 gap-3" data-type="${type.toLowerCase()}">
                    ${ingredients.map(ingredient => `
                        <div class="ingredient-card glass-card p-3 text-center" 
                             data-ingredient-id="${ingredient.id}" 
                             data-ingredient-name="${ingredient.name}"
                             data-ingredient-type="${type.toLowerCase()}"
                             data-ingredient-emoji="${ingredient.emoji}">
                            <div class="text-2xl mb-2">${ingredient.emoji}</div>
                            <div class="text-sm">${ingredient.name}</div>
                            <div class="text-xs mt-1" style="color: var(--text-gold);">${ingredient.description}</div>
                        </div>
                    `).join('')}
                </div>
            `;
            
            container.appendChild(section);
        });
        
        // 重新绑定事件
        this.bindIngredientEvents();
    }
    
    bindIngredientEvents() {
        document.querySelectorAll('.ingredient-card').forEach(card => {
            card.addEventListener('click', (e) => {
                this.selectIngredient(e.currentTarget);
            });
        });
    }
    
    checkAuthentication() {
        if (this.apiClient.isAuthenticated()) {
            this.currentUser = {
                id: this.apiClient.getCurrentUserId(),
                username: localStorage.getItem('username') || 'User'
            };
            this.updateAuthUI();
        }
    }
    
    updateAuthUI() {
        // 更新UI显示用户已登录状态
        const authSection = document.querySelector('.auth-section');
        if (authSection && this.currentUser) {
            authSection.innerHTML = `
                <div class="flex items-center space-x-2">
                    <span class="text-sm" style="color: var(--text-gold);">欢迎, ${this.currentUser.username}</span>
                    <button id="logout-btn" class="text-sm px-3 py-1 rounded" 
                            style="background: var(--accent-gold); color: var(--primary-dark);">
                        退出
                    </button>
                </div>
            `;
            
            document.getElementById('logout-btn').addEventListener('click', () => {
                this.logout();
            });
        }
    }
    
    async logout() {
        try {
            await this.apiClient.logout();
            this.currentUser = null;
            window.location.href = 'login.html';
        } catch (error) {
            console.error('退出登录失败:', error);
            this.showNotification('退出登录失败', 'error');
        }
    }
    
    setupEventListeners() {
        // 材料选择事件
        this.bindIngredientEvents();
        
        // 心情选择事件
        document.querySelectorAll('.mood-button').forEach(button => {
            button.addEventListener('click', (e) => {
                this.selectMood(e.currentTarget);
            });
        });
        
        // 调酒按钮事件
        const mixButton = document.getElementById('mix-button');
        if (mixButton) {
            mixButton.addEventListener('click', () => {
                this.mixCocktail();
            });
        }
        
        // 滑块事件
        ['sweetness', 'sourness', 'alcohol', 'fruitiness'].forEach(id => {
            const slider = document.getElementById(id);
            const valueDisplay = document.getElementById(id + '-value');
            
            if (slider && valueDisplay) {
                slider.addEventListener('input', (e) => {
                    const value = e.target.value;
                    valueDisplay.textContent = value;
                    this.preferences[id] = parseInt(value);
                    this.updatePreview();
                });
            }
        });
    }
    
    initializeSliders() {
        ['sweetness', 'sourness', 'alcohol', 'fruitiness'].forEach(id => {
            const slider = document.getElementById(id);
            const valueDisplay = document.getElementById(id + '-value');
            if (slider && valueDisplay) {
                valueDisplay.textContent = slider.value;
            }
        });
    }
    
    selectIngredient(card) {
        const ingredientId = card.dataset.ingredientId;
        const ingredientName = card.dataset.ingredientName;
        const ingredientType = card.dataset.ingredientType;
        const ingredientEmoji = card.dataset.ingredientEmoji;
        
        // 清除同类型的其他选择
        document.querySelectorAll(`[data-ingredient-type="${ingredientType}"]`).forEach(c => {
            c.classList.remove('selected');
        });
        
        // 选中当前卡片
        card.classList.add('selected');
        this.selectedIngredients[ingredientType] = {
            id: ingredientId,
            name: ingredientName,
            emoji: ingredientEmoji
        };
        
        // 添加选择动画
        if (window.anime) {
            anime({
                targets: card,
                scale: [1, 1.1, 1.05],
                duration: 300,
                easing: 'easeOutElastic(1, .8)'
            });
        }
        
        this.updatePreview();
        this.playSelectSound();
    }
    
    selectMood(button) {
        const mood = button.dataset.mood;
        
        // 清除其他心情选择
        document.querySelectorAll('.mood-button').forEach(b => {
            b.classList.remove('selected');
        });
        
        // 选中当前心情
        button.classList.add('selected');
        this.selectedMood = mood;
        
        // 添加选择动画
        if (window.anime) {
            anime({
                targets: button,
                scale: [1, 1.1, 1],
                duration: 400,
                easing: 'easeOutElastic(1, .6)'
            });
        }
        
        this.updatePreview();
        this.playSelectSound();
    }
    
    updatePreview() {
        const previewContent = document.getElementById('preview-content');
        const selectedIngredientsDiv = document.getElementById('selected-ingredients');
        const currentMoodDiv = document.getElementById('current-mood');
        
        if (!previewContent || !selectedIngredientsDiv || !currentMoodDiv) return;
        
        // 更新已选材料显示
        let ingredientsHtml = '';
        const hasSelection = Object.values(this.selectedIngredients).some(ing => ing !== null);
        
        if (hasSelection) {
            Object.entries(this.selectedIngredients).forEach(([type, ingredient]) => {
                if (ingredient) {
                    ingredientsHtml += `<div class="flex items-center space-x-2 text-sm">
                        <span>${ingredient.emoji}</span>
                        <span>${ingredient.name}</span>
                    </div>`;
                }
            });
        } else {
            ingredientsHtml = '<div class="text-sm" style="color: var(--text-gold);">暂无选择</div>';
        }
        
        selectedIngredientsDiv.innerHTML = ingredientsHtml;
        
        // 更新心情显示
        if (this.selectedMood) {
            const moodInfo = this.getMoodInfo(this.selectedMood);
            currentMoodDiv.innerHTML = `${moodInfo.emoji} ${moodInfo.name}`;
        } else {
            currentMoodDiv.textContent = '未选择';
        }
        
        // 更新预览区域内容
        if (hasSelection && this.selectedMood) {
            const moodInfo = this.getMoodInfo(this.selectedMood);
            previewContent.innerHTML = `
                <div class="text-6xl mb-4">${moodInfo.emoji}</div>
                <p class="text-lg mb-2">${moodInfo.cocktailName}</p>
                <p class="text-sm" style="color: var(--text-gold);">${moodInfo.description}</p>
            `;
            previewContent.classList.add('glow-effect');
        } else {
            previewContent.innerHTML = `
                <div class="text-6xl mb-4">🍸</div>
                <p class="text-lg mb-2">选择材料和心情</p>
                <p class="text-sm" style="color: var(--text-gold);">开始创造你的专属饮品</p>
            `;
            previewContent.classList.remove('glow-effect');
        }
    }
    
    getMoodInfo(mood) {
        const moodMap = {
            happy: { emoji: '😊', name: '开心', cocktailName: '阳光鸡尾酒', description: '明亮欢快的口感' },
            sad: { emoji: '😔', name: '失落', cocktailName: '慰藉之饮', description: '温暖治愈的味道' },
            excited: { emoji: '🎉', name: '兴奋', cocktailName: '狂欢特调', description: '充满活力的口感' },
            romantic: { emoji: '💕', name: '浪漫', cocktailName: '爱情魔药', description: '甜蜜迷人的味道' },
            tired: { emoji: '😴', name: '疲惫', cocktailName: '放松时光', description: '舒缓身心的口感' },
            celebrating: { emoji: '🥂', name: '庆祝', cocktailName: '庆典香槟', description: '奢华庆祝的味道' }
        };
        return moodMap[mood] || { emoji: '🍸', name: '未知', cocktailName: '神秘饮品', description: '未知的味道' };
    }
    
    async mixCocktail() {
        // 检查是否登录
        if (!this.apiClient.isAuthenticated()) {
            this.showNotification('请先登录后再调酒', 'warning');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
            return;
        }
        
        // 检查是否选择了基础材料
        if (!this.selectedIngredients.base || !this.selectedMood) {
            this.showNotification('请先选择基酒和心情！', 'warning');
            return;
        }
        
        // 创建调酒动画
        this.showMixingAnimation();
        
        try {
            // 准备配方数据
            const recipeData = {
                name: this.generateCocktailName(),
                description: this.generateDescription(),
                mood: this.selectedMood.toUpperCase(),
                sweetness: this.preferences.sweetness,
                sourness: this.preferences.sourness,
                alcohol: this.preferences.alcohol,
                fruitiness: this.preferences.fruitiness,
                ingredients: this.prepareIngredientsData(),
                steps: this.generateSteps(),
                isPublic: true
            };
            
            // 调用API生成配方
            const response = await this.apiClient.generateRecipe(recipeData);
            
            if (response.success && response.data) {
                // 保存到sessionStorage并跳转到结果页面
                sessionStorage.setItem('currentRecipe', JSON.stringify(response.data));
                window.location.href = 'result.html';
            } else {
                this.showNotification('生成配方失败: ' + response.message, 'error');
            }
        } catch (error) {
            console.error('生成配方失败:', error);
            this.showNotification('生成配方失败，请重试', 'error');
        }
    }
    
    generateCocktailName() {
        const baseName = this.selectedIngredients.base ? this.selectedIngredients.base.name : '';
        const moodName = this.getMoodInfo(this.selectedMood).name;
        
        const prefixes = {
            '伏特加': ['水晶', '冰雪', '纯净'],
            '威士忌': ['琥珀', '经典', '醇厚'],
            '朗姆酒': ['热带', '海盗', '加勒比'],
            '金酒': ['森林', '清新', '杜松'],
            '龙舌兰': ['沙漠', '火焰', '墨西哥'],
            '白兰地': ['贵族', '优雅', '香槟']
        };
        
        const suffixes = {
            '开心': ['阳光', '微笑', '欢乐'],
            '失落': ['慰藉', '温暖', '治愈'],
            '兴奋': ['狂欢', '派对', '爆炸'],
            '浪漫': ['玫瑰', '爱情', '月光'],
            '疲惫': ['放松', '宁静', '安眠'],
            '庆祝': ['庆典', '香槟', '胜利']
        };
        
        const prefix = prefixes[baseName] ? prefixes[baseName][Math.floor(Math.random() * prefixes[baseName].length)] : '神秘';
        const suffix = suffixes[moodName] ? suffixes[moodName][Math.floor(Math.random() * suffixes[moodName].length)] : '特调';
        
        return prefix + suffix;
    }
    
    generateDescription() {
        const moodInfo = this.getMoodInfo(this.selectedMood);
        let description = moodInfo.description;
        
        // 根据偏好调整描述
        if (this.preferences.sweetness > 7) {
            description += '，甜美的口感让人心情愉悦';
        }
        if (this.preferences.sourness > 7) {
            description += '，酸爽的味道带来清新的体验';
        }
        if (this.preferences.alcohol > 7) {
            description += '，浓郁的酒精带来强烈的满足感';
        }
        if (this.preferences.fruitiness > 7) {
            description += '，丰富的果香充满整个口腔';
        }
        
        return description;
    }
    
    prepareIngredientsData() {
        const ingredients = [];
        
        // 添加基酒
        if (this.selectedIngredients.base) {
            const baseAmount = 45 + (this.preferences.alcohol - 5) * 5;
            ingredients.push({
                ingredient: { id: this.selectedIngredients.base.id },
                amount: `${baseAmount}ml`,
                unit: 'ml',
                orderNum: 1
            });
        }
        
        // 添加果汁
        if (this.selectedIngredients.juice) {
            const juiceAmount = 60 + (this.preferences.fruitiness - 5) * 10;
            ingredients.push({
                ingredient: { id: this.selectedIngredients.juice.id },
                amount: `${juiceAmount}ml`,
                unit: 'ml',
                orderNum: 2
            });
        }
        
        // 添加糖浆
        if (this.selectedIngredients.syrup) {
            const syrupAmount = 15 + (this.preferences.sweetness - 5) * 3;
            ingredients.push({
                ingredient: { id: this.selectedIngredients.syrup.id },
                amount: `${syrupAmount}ml`,
                unit: 'ml',
                orderNum: 3
            });
        }
        
        return ingredients;
    }
    
    generateSteps() {
        const steps = [
            {
                stepNumber: 1,
                description: '准备调酒器具：摇酒壶、量杯、酒杯',
                durationSeconds: 30
            },
            {
                stepNumber: 2,
                description: '在摇酒壶中加入冰块',
                durationSeconds: 10
            }
        ];
        
        // 添加每种材料的步骤
        let stepNumber = 3;
        if (this.selectedIngredients.base) {
            steps.push({
                stepNumber: stepNumber++,
                description: `量取适量${this.selectedIngredients.base.name}倒入摇酒壶`,
                durationSeconds: 15
            });
        }
        
        if (this.selectedIngredients.juice) {
            steps.push({
                stepNumber: stepNumber++,
                description: `量取适量${this.selectedIngredients.juice.name}倒入摇酒壶`,
                durationSeconds: 15
            });
        }
        
        if (this.selectedIngredients.syrup) {
            steps.push({
                stepNumber: stepNumber++,
                description: `量取适量${this.selectedIngredients.syrup.name}倒入摇酒壶`,
                durationSeconds: 15
            });
        }
        
        steps.push({
            stepNumber: stepNumber++,
            description: '盖紧摇酒壶，用力摇晃15-20秒',
            durationSeconds: 20
        });
        
        steps.push({
            stepNumber: stepNumber++,
            description: '将混合好的酒液过滤倒入冰镇的酒杯中',
            durationSeconds: 10
        });
        
        steps.push({
            stepNumber: stepNumber,
            description: '轻轻搅拌，即可享用你的专属饮品',
            durationSeconds: 5
        });
        
        return steps;
    }
    
    showMixingAnimation() {
        const mixButton = document.getElementById('mix-button');
        if (!mixButton) return;
        
        const originalText = mixButton.innerHTML;
        mixButton.innerHTML = '🌀 调酒中... 🌀';
        mixButton.disabled = true;
        
        // 按钮旋转动画
        if (window.anime) {
            anime({
                targets: mixButton,
                rotate: '1turn',
                duration: 2000,
                easing: 'easeInOutQuad'
            });
            
            // 预览区域动画
            const previewArea = document.querySelector('.preview-area');
            if (previewArea) {
                anime({
                    targets: previewArea,
                    scale: [1, 1.1, 1],
                    duration: 2000,
                    easing: 'easeInOutQuad'
                });
            }
        }
        
        setTimeout(() => {
            mixButton.innerHTML = originalText;
            mixButton.disabled = false;
        }, 2000);
    }
    
    playSelectSound() {
        // 播放选择音效（如果需要的话）
        // 这里可以添加音频播放逻辑
    }
    
    showNotification(message, type = 'info') {
        // 创建通知元素
        const notification = document.createElement('div');
        const bgColor = type === 'success' ? 'bg-green-600' : 
                       type === 'warning' ? 'bg-yellow-600' : 
                       type === 'error' ? 'bg-red-600' : 'bg-blue-600';
        
        notification.className = `fixed top-20 right-4 p-4 rounded-lg shadow-lg z-50 ${bgColor} text-white`;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        // 显示动画
        if (window.anime) {
            anime({
                targets: notification,
                translateX: [300, 0],
                opacity: [0, 1],
                duration: 300,
                easing: 'easeOutQuad'
            });
        }
        
        // 3秒后移除
        setTimeout(() => {
            if (window.anime) {
                anime({
                    targets: notification,
                    translateX: [0, 300],
                    opacity: [1, 0],
                    duration: 300,
                    easing: 'easeInQuad',
                    complete: () => {
                        if (document.body.contains(notification)) {
                            document.body.removeChild(notification);
                        }
                    }
                });
            } else {
                if (document.body.contains(notification)) {
                    document.body.removeChild(notification);
                }
            }
        }, 3000);
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
                    for (let i = 0; i < 50; i++) {
                        particles.push({
                            x: p.random(p.width),
                            y: p.random(p.height),
                            vx: p.random(-0.5, 0.5),
                            vy: p.random(-0.5, 0.5),
                            size: p.random(2, 6),
                            opacity: p.random(0.1, 0.3)
                        });
                    }
                };
                
                p.draw = () => {
                    p.clear();
                    
                    // 绘制粒子
                    particles.forEach(particle => {
                        p.fill(212, 175, 55, particle.opacity * 255);
                        p.noStroke();
                        p.circle(particle.x, particle.y, particle.size);
                        
                        // 更新位置
                        particle.x += particle.vx;
                        particle.y += particle.vy;
                        
                        // 边界检查
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

// 初始化应用
document.addEventListener('DOMContentLoaded', () => {
    window.cocktailApp = new CocktailMakerApp();
});