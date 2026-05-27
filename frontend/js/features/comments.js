/**
 * 评论回复系统 - 前端交互模块
 * 支持楼中楼、折叠展开、点赞、删除
 */
class CommentSystem {
    constructor(containerId, recipeId) {
        this.container = document.getElementById(containerId);
        this.recipeId = recipeId;
        this.currentPage = 0;
        this.currentUserId = apiClient.getCurrentUserId();
    }

    async load(page = 0) {
        this.currentPage = page;
        try {
            const resp = await apiClient.getRecipeComments(this.recipeId, page, 20);
            if (resp.success) {
                this.render(resp.data);
            }
        } catch (e) {
            this.container.innerHTML = '<p class="text-gray-400 text-center py-8">加载评论失败</p>';
        }
    }

    render(data) {
        if (!data || !data.content || data.content.length === 0) {
            this.container.innerHTML = `
                <div class="text-center py-12 text-gray-400">
                    <p class="text-4xl mb-3">💬</p>
                    <p>暂无评论，来说点什么吧</p>
                </div>`;
            return;
        }

        let html = '';
        data.content.forEach(comment => {
            html += this.renderComment(comment, 0);
        });

        // pagination
        if (data.totalPages > 1) {
            html += this.renderPagination(data);
        }

        this.container.innerHTML = html;
        this.bindEvents();
    }

    renderComment(comment, depth) {
        const isDeleted = comment.isDeleted;
        const avatar = comment.avatar || '👤';
        const name = comment.nickname || comment.username || '匿名';
        const content = isDeleted ? '<span class="text-gray-500 italic">该评论已删除</span>' : this.escapeHtml(comment.content);
        const time = this.formatTime(comment.createdAt);
        const likeCount = comment.likeCount || 0;
        const liked = comment.likedByMe ? 'text-amber-400' : 'text-gray-400';
        const marginLeft = depth > 0 ? 'ml-10' : '';

        let html = `
        <div class="comment-item ${marginLeft} mb-4" data-comment-id="${comment.id}">
            <div class="glass-card p-4">
                <div class="flex items-start gap-3">
                    <div class="flex-shrink-0 w-10 h-10 rounded-full bg-amber-900/30 flex items-center justify-center text-lg">
                        ${avatar}
                    </div>
                    <div class="flex-1 min-w-0">
                        <div class="flex items-center gap-2 mb-1">
                            <span class="font-medium text-amber-200 text-sm">${name}</span>
                            <span class="text-gray-500 text-xs">${time}</span>
                        </div>
                        <div class="text-gray-200 text-sm mb-2">${content}</div>
                        ${!isDeleted ? `
                        <div class="flex items-center gap-4 text-xs">
                            <button class="comment-like-btn ${liked} hover:text-amber-400 transition" data-id="${comment.id}">
                                👍 <span class="like-count">${likeCount}</span>
                            </button>
                            <button class="comment-reply-btn text-gray-400 hover:text-amber-400 transition" data-id="${comment.id}">
                                💬 回复
                            </button>
                            ${comment.userId == this.currentUserId ? `
                            <button class="comment-delete-btn text-gray-500 hover:text-red-400 transition" data-id="${comment.id}">
                                🗑️ 删除
                            </button>` : ''}
                        </div>
                        <div class="comment-reply-form hidden mt-3" data-parent="${comment.id}">
                            <textarea rows="2" class="w-full bg-gray-800 border border-amber-700/30 rounded-lg p-2 text-sm text-gray-200 resize-none"
                                placeholder="回复 ${name}..."></textarea>
                            <div class="flex justify-end gap-2 mt-2">
                                <button class="cancel-reply-btn px-3 py-1 text-xs text-gray-400 hover:text-white transition">取消</button>
                                <button class="submit-reply-btn px-4 py-1 text-xs bg-amber-600 hover:bg-amber-500 rounded-lg transition" data-parent="${comment.id}">回复</button>
                            </div>
                        </div>` : ''}
                    </div>
                </div>
            </div>
        </div>`;

        // 渲染子回复
        if (comment.replies && comment.replies.length > 0) {
            comment.replies.forEach(reply => {
                html += this.renderComment(reply, 1);
            });
        }

        return html;
    }

    renderPagination(data) {
        const pages = [];
        for (let i = 0; i < data.totalPages; i++) {
            const active = i === this.currentPage ? 'bg-amber-600' : 'bg-gray-700 hover:bg-gray-600';
            pages.push(`<button class="page-btn px-3 py-1 text-xs rounded ${active} transition" data-page="${i}">${i + 1}</button>`);
        }
        return `<div class="flex justify-center gap-2 mt-6">${pages.join('')}</div>`;
    }

    bindEvents() {
        // 点赞
        this.container.querySelectorAll('.comment-like-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                e.preventDefault();
                const id = btn.dataset.id;
                const liked = btn.classList.contains('text-amber-400');
                try {
                    if (liked) {
                        await apiClient.unlikeComment(id);
                    } else {
                        await apiClient.likeComment(id);
                    }
                    this.load(this.currentPage);
                } catch (err) {}
            });
        });

        // 展开回复表单
        this.container.querySelectorAll('.comment-reply-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                const form = this.container.querySelector(`.comment-reply-form[data-parent="${id}"]`);
                if (form) form.classList.remove('hidden');
            });
        });

        // 取消回复
        this.container.querySelectorAll('.cancel-reply-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const form = btn.closest('.comment-reply-form');
                if (form) form.classList.add('hidden');
            });
        });

        // 提交回复
        this.container.querySelectorAll('.submit-reply-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const parentId = btn.dataset.parent;
                const form = btn.closest('.comment-reply-form');
                const textarea = form.querySelector('textarea');
                const content = textarea.value.trim();
                if (!content) return;
                try {
                    await apiClient.replyToComment(parentId, content);
                    textarea.value = '';
                    form.classList.add('hidden');
                    this.load(this.currentPage);
                } catch (err) {
                    alert('回复失败');
                }
            });
        });

        // 删除
        this.container.querySelectorAll('.comment-delete-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                if (!confirm('确定删除这条评论吗？')) return;
                try {
                    await apiClient.deleteComment(btn.dataset.id);
                    this.load(this.currentPage);
                } catch (err) {
                    alert('删除失败');
                }
            });
        });

        // 分页
        this.container.querySelectorAll('.page-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this.load(parseInt(btn.dataset.page));
            });
        });
    }

    formatTime(dateStr) {
        if (!dateStr) return '';
        const d = new Date(dateStr);
        const now = new Date();
        const diff = now - d;
        if (diff < 60000) return '刚刚';
        if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
        if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
        return d.toLocaleDateString('zh-CN');
    }

    escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }
}

// 评论发布区域
function initCommentForm(recipeId, onSuccess) {
    const form = document.getElementById('comment-form');
    const textarea = document.getElementById('comment-input');
    const submitBtn = document.getElementById('comment-submit');

    if (!form || !textarea || !submitBtn) return;

    submitBtn.addEventListener('click', async () => {
        const content = textarea.value.trim();
        if (!content) { alert('请输入评论内容'); return; }
        try {
            await apiClient.createComment({ recipeId, content });
            textarea.value = '';
            if (onSuccess) onSuccess();
        } catch (err) {
            alert('评论失败');
        }
    });
}

window.CommentSystem = CommentSystem;
window.initCommentForm = initCommentForm;
