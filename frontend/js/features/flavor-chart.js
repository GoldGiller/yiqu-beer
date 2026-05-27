/**
 * 风味雷达图 - ECharts 可视化模块
 */
class FlavorChart {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.chart = null;
    }

    async load(userId) {
        try {
            const resp = await apiClient.getFlavorProfile(userId);
            if (resp.success) {
                this.render(resp.data);
            }
        } catch (e) {
            this.container.innerHTML = '<p class="text-gray-400 text-center py-8">加载风味数据失败</p>';
        }
    }

    render(data) {
        if (!this.container) return;

        if (this.chart) this.chart.dispose();

        // 使用全局 echarts（CDN 加载）
        if (typeof echarts === 'undefined') {
            this.container.innerHTML = '<p class="text-gray-400 text-center py-8">图表库加载中...</p>';
            return;
        }

        this.chart = echarts.init(this.container);
        const option = {
            tooltip: {
                trigger: 'item',
                backgroundColor: 'rgba(26, 26, 26, 0.9)',
                borderColor: '#d4af37',
                textStyle: { color: '#f8f8ff' }
            },
            legend: {
                bottom: 0,
                textStyle: { color: '#f8f8ff', fontSize: 12 },
                data: ['我的风味', '平台均值']
            },
            radar: {
                center: ['50%', '50%'],
                radius: '65%',
                indicator: [
                    { name: '甜度', max: 10 },
                    { name: '酸度', max: 10 },
                    { name: '酒精度', max: 10 },
                    { name: '果味', max: 10 }
                ],
                axisName: { color: '#f5deb3', fontSize: 13 },
                shape: 'polygon',
                splitArea: {
                    areaStyle: { color: ['rgba(212, 175, 55, 0.05)', 'rgba(212, 175, 55, 0.1)'] }
                },
                axisLine: { lineStyle: { color: 'rgba(212, 175, 55, 0.3)' } },
                splitLine: { lineStyle: { color: 'rgba(212, 175, 55, 0.2)' } }
            },
            series: [{
                type: 'radar',
                name: '我的风味',
                data: [{
                    value: [
                        data.avgSweetness || 0,
                        data.avgSourness || 0,
                        data.avgAlcohol || 0,
                        data.avgFruitiness || 0
                    ],
                    name: '我的风味'
                }],
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: { color: '#d4af37', width: 2 },
                areaStyle: { color: 'rgba(212, 175, 55, 0.2)' },
                itemStyle: { color: '#d4af37' }
            }, {
                type: 'radar',
                name: '平台均值',
                data: [{
                    value: [5, 5, 5, 5],
                    name: '平台均值'
                }],
                symbol: 'diamond',
                symbolSize: 5,
                lineStyle: { color: '#b87333', width: 1.5, type: 'dashed' },
                areaStyle: { color: 'rgba(184, 115, 51, 0.1)' },
                itemStyle: { color: '#b87333' }
            }]
        };

        this.chart.setOption(option);
        window.addEventListener('resize', () => this.chart && this.chart.resize());

        // 主导风味文本
        if (data.dominantFlavor) {
            const infoEl = document.getElementById('flavor-info');
            if (infoEl) {
                infoEl.innerHTML = `
                    <p class="text-amber-300 font-medium">你的风味偏好: <span class="text-amber-400">${data.dominantFlavor}</span></p>
                    <p class="text-gray-400 text-sm mt-1">基于 ${data.totalRecipesCreated || 0} 个配方分析</p>`;
            }
        }
    }

    dispose() {
        if (this.chart) {
            this.chart.dispose();
            this.chart = null;
        }
    }
}

window.FlavorChart = FlavorChart;
