/**
 * 配方导入/导出 & 版本管理模块
 */
class RecipeToolkit {
    constructor(recipeId) {
        this.recipeId = recipeId;
    }

    // 导出为 JSON
    async exportJSON() {
        try {
            const resp = await apiClient.exportRecipe(this.recipeId);
            if (resp.success) {
                apiClient.downloadJSON(resp.data, `recipe-${this.recipeId}.json`);
            }
        } catch (e) {
            alert('导出失败');
        }
    }

    // 从文件导入
    static async importFromFile(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const data = JSON.parse(e.target.result);
                    if (!data.name) {
                        reject(new Error('无效的配方文件格式'));
                        return;
                    }
                    resolve(data);
                } catch (err) {
                    reject(new Error('JSON解析失败'));
                }
            };
            reader.onerror = () => reject(new Error('文件读取失败'));
            reader.readAsText(file);
        });
    }

    // 版本管理
    async loadVersions(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;
        try {
            const resp = await apiClient.getRecipeVersions(this.recipeId);
            if (resp.success && resp.data && resp.data.length > 0) {
                container.innerHTML = this.renderVersionList(resp.data);
                this.bindVersionEvents(container);
            } else {
                container.innerHTML = '<p class="text-gray-500 text-sm py-4">暂无历史版本</p>';
            }
        } catch (e) {
            container.innerHTML = '<p class="text-gray-500 text-sm py-4">加载版本失败</p>';
        }
    }

    renderVersionList(versions) {
        let html = '<div class="space-y-3">';
        versions.forEach((v, i) => {
            const latest = i === 0 ? ' <span class="text-xs text-amber-400">(最新)</span>' : '';
            html += `
            <div class="glass-card p-3 flex items-center justify-between">
                <div>
                    <span class="text-amber-200 font-medium text-sm">v${v.versionNumber}</span>${latest}
                    <span class="text-gray-500 text-xs ml-2">${this.formatTime(v.createdAt)}</span>
                    ${v.creatorName ? `<span class="text-gray-500 text-xs ml-2">by ${v.creatorName}</span>` : ''}
                    ${v.changeSummary ? `<p class="text-gray-400 text-xs mt-1">${v.changeSummary}</p>` : ''}
                </div>
                <div class="flex gap-2">
                    <button class="restore-version-btn px-3 py-1 text-xs bg-amber-700/50 hover:bg-amber-600 rounded transition"
                        data-recipe="${v.recipeId}" data-version="${v.versionNumber}">回滚</button>
                </div>
            </div>`;
        });
        html += '</div>';
        return html;
    }

    bindVersionEvents(container) {
        container.querySelectorAll('.restore-version-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                if (!confirm(`确定回滚到 v${btn.dataset.version}？当前版本将自动保存。`)) return;
                try {
                    await apiClient.restoreVersion(btn.dataset.recipe, parseInt(btn.dataset.version));
                    alert('回滚成功！');
                    this.loadVersions(container.id);
                } catch (e) {
                    alert('回滚失败');
                }
            });
        });
    }

    formatTime(dateStr) {
        if (!dateStr) return '';
        return new Date(dateStr).toLocaleString('zh-CN');
    }
}

window.RecipeToolkit = RecipeToolkit;
