/**
 * API客户端
 * 用于与后端API进行通信
 */

class ApiClient {
    constructor() {
        this.baseUrl = '/api';
        this.token = localStorage.getItem('authToken');
        this.userId = localStorage.getItem('userId');
    }

    /**
     * 设置认证令牌
     */
    setAuthToken(token) {
        this.token = token;
        localStorage.setItem('authToken', token);
    }

    /**
     * 设置用户ID
     */
    setUserId(userId) {
        this.userId = userId;
        localStorage.setItem('userId', userId);
    }

    /**
     * 清除认证信息
     */
    clearAuth() {
        this.token = null;
        this.userId = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('userId');
    }

    /**
     * 获取请求头
     */
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json',
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        if (this.userId) {
            headers['X-User-Id'] = this.userId;
        }

        return headers;
    }

    /**
     * 发送请求
     */
    async request(method, endpoint, data = null) {
        const url = `${this.baseUrl}${endpoint}`;
        const options = {
            method: method,
            headers: this.getHeaders(),
        };

        if (data) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);
            const result = await response.json();

            if (!response.ok) {
                throw new Error(result.message || 'Request failed');
            }

            return result;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    /**
     * GET请求
     */
    get(endpoint) {
        return this.request('GET', endpoint);
    }

    /**
     * POST请求
     */
    post(endpoint, data) {
        return this.request('POST', endpoint, data);
    }

    /**
     * PUT请求
     */
    put(endpoint, data) {
        return this.request('PUT', endpoint, data);
    }

    /**
     * DELETE请求
     */
    delete(endpoint) {
        return this.request('DELETE', endpoint);
    }

    // 用户相关API

    /**
     * 用户注册
     */
    async register(userData) {
        return this.post('/auth/register', userData);
    }

    /**
     * 用户登录
     */
    async login(username, password) {
        // 使用POST请求体而不是查询参数
        return this.post('/auth/login', { username, password });
    }

    /**
     * 用户登出
     */
    async logout() {
        const result = this.post('/auth/logout', {});
        this.clearAuth();
        return result;
    }

    /**
     * 获取用户信息
     */
    async getUserInfo(userId) {
        return this.get(`/users/${userId}`);
    }

    /**
     * 更新用户信息
     */
    async updateUserInfo(userId, userData) {
        return this.put(`/users/${userId}`, userData);
    }

    // 配方相关API

    /**
     * 创建配方
     */
    async createRecipe(recipeData) {
        return this.post('/recipes', recipeData);
    }

    /**
     * 获取配方详情
     */
    async getRecipe(recipeId) {
        return this.get(`/recipes/${recipeId}`);
    }

    /**
     * 更新配方
     */
    async updateRecipe(recipeId, recipeData) {
        return this.put(`/recipes/${recipeId}`, recipeData);
    }

    /**
     * 删除配方
     */
    async deleteRecipe(recipeId) {
        return this.delete(`/recipes/${recipeId}`);
    }

    /**
     * 获取公开配方列表
     */
    async getPublicRecipes(page = 0, size = 20) {
        return this.get(`/recipes?page=${page}&size=${size}`);
    }

    /**
     * 获取用户的配方
     */
    async getUserRecipes(userId, page = 0, size = 20) {
        return this.get(`/recipes/my?userId=${userId}&page=${page}&size=${size}`);
    }

    /**
     * 根据心情获取配方
     */
    async getRecipesByMood(mood, page = 0, size = 20) {
        return this.get(`/recipes/mood/${mood}?page=${page}&size=${size}`);
    }

    /**
     * 搜索配方
     */
    async searchRecipes(keyword, page = 0, size = 20) {
        return this.get(`/recipes/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    }

    /**
     * 获取热门配方
     */
    async getPopularRecipes(page = 0, size = 20) {
        return this.get(`/recipes/popular?page=${page}&size=${size}`);
    }

    /**
     * 获取最新配方
     */
    async getLatestRecipes(page = 0, size = 20) {
        return this.get(`/recipes/latest?page=${page}&size=${size}`);
    }

    /**
     * 获取推荐配方
     */
    async getRecommendedRecipes(userId, page = 0, size = 20) {
        return this.get(`/recipes/recommended?userId=${userId}&page=${page}&size=${size}`);
    }

    /**
     * 生成配方
     */
    async generateRecipe(recipeData) {
        return this.post('/recipes/generate', recipeData);
    }

    // 社交功能API

    /**
     * 点赞配方
     */
    async likeRecipe(recipeId) {
        return this.post(`/recipes/${recipeId}/like`, {});
    }

    /**
     * 取消点赞配方
     */
    async unlikeRecipe(recipeId) {
        return this.delete(`/recipes/${recipeId}/like`);
    }

    /**
     * 收藏配方
     */
    async favoriteRecipe(recipeId) {
        return this.post(`/recipes/${recipeId}/favorite`, {});
    }

    /**
     * 取消收藏配方
     */
    async unfavoriteRecipe(recipeId) {
        return this.delete(`/recipes/${recipeId}/favorite`);
    }

    /**
     * 评分配方
     */
    async rateRecipe(recipeId, rating, comment = null) {
        const params = new URLSearchParams({ rating: rating.toString() });
        if (comment) {
            params.append('comment', comment);
        }
        return this.post(`/recipes/${recipeId}/rate?${params.toString()}`, {});
    }

    /**
     * 获取用户的收藏配方
     */
    async getUserFavoriteRecipes(userId, page = 0, size = 20) {
        return this.get(`/recipes/favorites?userId=${userId}&page=${page}&size=${size}`);
    }

    /**
     * 获取用户的浏览历史
     */
    async getUserViewHistory(userId, page = 0, size = 20) {
        return this.get(`/recipes/history?userId=${userId}&page=${page}&size=${size}`);
    }

    /**
     * 分享配方
     */
    async shareRecipe(recipeId) {
        return this.get(`/recipes/${recipeId}/share`);
    }

    // 材料相关API

    /**
     * 获取材料列表
     */
    async getIngredients(type = null, page = 0, size = 50) {
        let url = `/ingredients?page=${page}&size=${size}`;
        if (type) {
            url += `&type=${type}`;
        }
        return this.get(url);
    }

    /**
     * 搜索材料
     */
    async searchIngredients(keyword, page = 0, size = 20) {
        return this.get(`/ingredients/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    }

    // 工具方法

    /**
     * 格式化日期
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    /**
     * 格式化时间
     */
    formatTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleTimeString('zh-CN', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // ==========================================
    // 评论回复系统 API
    // ==========================================

    async getRecipeComments(recipeId, page = 0, size = 20) {
        return this.get(`/comments/recipe/${recipeId}?page=${page}&size=${size}`);
    }

    async createComment(commentData) {
        return this.post('/comments', commentData);
    }

    async replyToComment(commentId, content) {
        return this.post(`/comments/${commentId}/reply`, { content });
    }

    async likeComment(commentId) {
        return this.post(`/comments/${commentId}/like`, {});
    }

    async unlikeComment(commentId) {
        return this.delete(`/comments/${commentId}/like`);
    }

    async deleteComment(commentId) {
        return this.delete(`/comments/${commentId}`);
    }

    // ==========================================
    // 用户主页 API
    // ==========================================

    async getUserProfile(userId) {
        return this.get(`/users/${userId}/profile`);
    }

    async getUserActivities(userId, page = 0, size = 20) {
        return this.get(`/users/${userId}/activities?page=${page}&size=${size}`);
    }

    async getFlavorProfile(userId) {
        return this.get(`/users/${userId}/flavor-profile`);
    }

    // ==========================================
    // 配方版本管理 API
    // ==========================================

    async getRecipeVersions(recipeId) {
        return this.get(`/recipes/${recipeId}/versions`);
    }

    async saveRecipeVersion(recipeId, summary) {
        return this.post(`/recipes/${recipeId}/versions?summary=${encodeURIComponent(summary || '手动保存')}`, {});
    }

    async restoreVersion(recipeId, versionNumber) {
        return this.post(`/recipes/${recipeId}/versions/${versionNumber}/restore`, {});
    }

    async diffVersions(recipeId, v1, v2) {
        return this.get(`/recipes/${recipeId}/versions/diff?v1=${v1}&v2=${v2}`);
    }

    // ==========================================
    // 推荐系统 API
    // ==========================================

    async getCollaborativeRecommendations(limit = 10) {
        return this.get(`/recommendations/collaborative?limit=${limit}`);
    }

    async getSeasonalRecommendations(limit = 10) {
        return this.get(`/recommendations/seasonal?limit=${limit}`);
    }

    async getFoodPairings(recipeId) {
        return this.get(`/recommendations/food-pairings/${recipeId}`);
    }

    async recordInteraction(recipeId, type) {
        return this.post(`/recommendations/interactions?recipeId=${recipeId}&type=${type}`, {});
    }

    // ==========================================
    // 文件上传 API
    // ==========================================

    async uploadImage(file) {
        const formData = new FormData();
        formData.append('file', file);
        const headers = {};
        if (this.token) headers['Authorization'] = `Bearer ${this.token}`;
        if (this.userId) headers['X-User-Id'] = this.userId;

        const url = `${this.baseUrl}/uploads/image`;
        const response = await fetch(url, {
            method: 'POST',
            headers,
            body: formData
        });
        return response.json();
    }

    async uploadRecipeImage(recipeId, file) {
        const formData = new FormData();
        formData.append('file', file);
        const headers = {};
        if (this.token) headers['Authorization'] = `Bearer ${this.token}`;
        if (this.userId) headers['X-User-Id'] = this.userId;

        const url = `${this.baseUrl}/uploads/recipe/${recipeId}/image`;
        const response = await fetch(url, {
            method: 'POST',
            headers,
            body: formData
        });
        return response.json();
    }

    // ==========================================
    // 配方导入导出
    // ==========================================

    async exportRecipe(recipeId) {
        return this.get(`/recipes/${recipeId}`);
    }

    downloadJSON(data, filename) {
        const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        URL.revokeObjectURL(url);
    }

    async importRecipe(jsonData) {
        return this.post('/recipes', jsonData);
    }

    /**
     * 检查用户是否登录
     */
    isAuthenticated() {
        return !!this.token && !!this.userId;
    }

    /**
     * 获取当前用户ID
     */
    getCurrentUserId() {
        return this.userId;
    }
}

// 创建全局API客户端实例
const apiClient = new ApiClient();

// 导出API客户端
if (typeof module !== 'undefined' && module.exports) {
    module.exports = apiClient;
} else if (typeof window !== 'undefined') {
    window.apiClient = apiClient;
}