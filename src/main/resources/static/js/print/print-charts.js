(function () {
    'use strict';

    const CHART_TEXT = {
        family: 'Inter, Arial, sans-serif',
        color: '#374151',
        legend: {
            size: 13,
            weight: '400',
            padding: 8,
            boxWidth: 8,
            boxHeight: 8
        },
        axisY: {
            size: 13,
            weight: '400',
            padding: 6
        },
        axisX: {
            size: 13,
            weight: '400',
            lineHeight: 1.25,
            padding: 6
        },
        axisXBar: {
            size: 13,
            padding: 4
        },
        dataLabel: {
            bar: { size: 13, weight: '600', color: null },
            line: {
                size: 13,
                weight: '600',
                useSeriesColor: true,
                minDistanceY: 18,
                minDistanceX: 55,
                positions: [
                    { dy: -8, anchor: 'top' },
                    { dy: 14, anchor: 'bottom' },
                    { dy: -20, anchor: 'top' },
                    { dy: 26, anchor: 'bottom' }
                ]
            }
        },
        grid: {
            color: '#D1D5DB',
            lineWidth: 1,
            borderDash: [3, 3]
        },
        axisBorder: {
            width: 1
        }
    };

    const CHART_SERIES = {
        colors: [
            '#37A2EB',
            '#FF6485',
            '#4CC0C0',
            '#FFA040',
            '#9A66FF',
            '#FFCD56',
            '#CACBCF'
        ],
        lineColor: '#FFCD56',
        bar: {
            opacity: 0.8,
            borderColor: 'transparent',
            borderWidth: 0,
            categoryPercentage: 0.78,
            barPercentage: 0.88
        },
        line: {
            borderWidth: 2,
            pointRadius: 4,
            pointBorderWidth: 0,
            tension: 0
        },
        dataLabelOffset: {
            bar: 4,
            line: 8
        }
    };

    const CHART_PLOT_HEIGHT_PX = 300;
    const CHART_SPLIT_PLOT_HEIGHT_PX = CHART_PLOT_HEIGHT_PX / 3;
    const CHART_LEGEND_HEIGHT = 28;
    const DEFAULT_CHART_DEVICE_PIXEL_RATIO = 4;

    function getChartDevicePixelRatio() {
        const param = new URLSearchParams(window.location.search).get('chartDpr');
        const value = parseFloat(param);
        if (Number.isFinite(value) && value >= 1 && value <= 4) {
            return value;
        }
        return DEFAULT_CHART_DEVICE_PIXEL_RATIO;
    }

    function chartFont(size, weight, lineHeight) {
        const font = {
            family: CHART_TEXT.family,
            size: size,
            weight: weight || '400'
        };
        if (lineHeight != null) {
            font.lineHeight = lineHeight;
        }
        return font;
    }

    function gridStyle() {
        return {
            display: true,
            color: CHART_TEXT.grid.color,
            lineWidth: CHART_TEXT.grid.lineWidth,
            borderDash: CHART_TEXT.grid.borderDash,
            borderDashOffset: 0,
            drawOnChartArea: true,
            tickLength: 0
        };
    }

    function seriesColor(index) {
        return CHART_SERIES.colors[index % CHART_SERIES.colors.length];
    }

    function buildLineDatasetStyle(color) {
        return {
            borderColor: color,
            backgroundColor: color,
            pointBackgroundColor: color,
            pointBorderColor: color,
            pointBorderWidth: CHART_SERIES.line.pointBorderWidth,
            pointRadius: CHART_SERIES.line.pointRadius,
            borderWidth: CHART_SERIES.line.borderWidth,
            tension: CHART_SERIES.line.tension
        };
    }

    function withSeriesOpacity(color, alpha) {
        if (!color || alpha == null || alpha >= 1) {
            return color;
        }
        const normalized = String(color).trim();
        if (normalized.startsWith('#')) {
            const hex = normalized.slice(1);
            if (hex.length === 6) {
                const r = parseInt(hex.slice(0, 2), 16);
                const g = parseInt(hex.slice(2, 4), 16);
                const b = parseInt(hex.slice(4, 6), 16);
                return 'rgba(' + r + ', ' + g + ', ' + b + ', ' + alpha + ')';
            }
        }
        return color;
    }

    function buildBarDatasetStyle(color) {
        return {
            backgroundColor: withSeriesOpacity(color, CHART_SERIES.bar.opacity),
            borderColor: CHART_SERIES.bar.borderColor,
            borderWidth: CHART_SERIES.bar.borderWidth
        };
    }

    let readyResolve;
    const readyPromise = new Promise(function (resolve) {
        readyResolve = resolve;
    });

    function formatRuDecimal(value) {
        if (value == null || Number.isNaN(value)) {
            return '—';
        }
        return value.toLocaleString('ru-RU', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
        });
    }

    function formatChartLabel(value, decimalPlaces) {
        if (value == null || Number.isNaN(value)) {
            return '—';
        }
        const places = decimalPlaces > 0 ? decimalPlaces : 0;
        if (places > 0) {
            return value.toLocaleString('ru-RU', {
                minimumFractionDigits: places,
                maximumFractionDigits: places
            });
        }
        return Math.round(value).toLocaleString('ru-RU', {
            maximumFractionDigits: 0
        });
    }

    function readChartDecimalPlaces(sectionElement) {
        const parsed = parseInt(sectionElement.dataset.chartDecimals, 10);
        return Number.isFinite(parsed) && parsed > 0 ? parsed : 0;
    }

    function parseNullableNumber(value) {
        if (value == null || value === '') {
            return null;
        }
        const parsed = Number(value);
        return Number.isNaN(parsed) ? null : parsed;
    }

    function readIndicators(sectionElement) {
        return Array.from(sectionElement.querySelectorAll('.print-section__chart-data .indicator-block'))
            .map(function (block, index) {
                return {
                    label: block.dataset.label || '',
                    seriesType: (block.dataset.seriesType || 'BAR').toUpperCase(),
                    values: [
                        parseNullableNumber(block.dataset.two),
                        parseNullableNumber(block.dataset.prev),
                        parseNullableNumber(block.dataset.current)
                    ],
                    color: block.dataset.color || seriesColor(index)
                };
            });
    }

    function resolveChartPeriods(periodLabels, indicators) {
        if (indicators.length === 0) {
            return { periodLabels: periodLabels, indicators: indicators };
        }

        if (periodLabels.length === 2) {
            const hasTwoPeriodsAgo = indicators.some(function (indicator) {
                return indicator.values[0] != null;
            });
            if (!hasTwoPeriodsAgo) {
                return {
                    periodLabels: periodLabels,
                    indicators: indicators.map(function (indicator) {
                        return {
                            label: indicator.label,
                            seriesType: indicator.seriesType,
                            color: indicator.color,
                            values: indicator.values.slice(-2)
                        };
                    })
                };
            }
        }

        if (periodLabels.length < 3) {
            return { periodLabels: periodLabels, indicators: indicators };
        }

        const hasTwoPeriodsAgo = indicators.some(function (indicator) {
            return indicator.values[0] != null;
        });

        if (hasTwoPeriodsAgo) {
            return { periodLabels: periodLabels, indicators: indicators };
        }

        return {
            periodLabels: periodLabels.slice(1),
            indicators: indicators.map(function (indicator) {
                return {
                    label: indicator.label,
                    seriesType: indicator.seriesType,
                    color: indicator.color,
                    values: indicator.values.slice(1)
                };
            })
        };
    }

    function updateChartTitle(sectionElement, periodLabels) {
        const titleElement = sectionElement.querySelector('.print-section__chart-title');
        if (!titleElement || periodLabels.length < 2) {
            return;
        }
        titleElement.textContent = 'Динамика показателей ('
            + periodLabels[0]
            + '–'
            + periodLabels[periodLabels.length - 1]
            + ' гг.)';
    }

    function maxOfValues(values) {
        const filtered = values.filter(function (v) { return v != null; });
        if (filtered.length === 0) {
            return 0;
        }
        return Math.max.apply(null, filtered);
    }

    function computeNiceStep(roughStep) {
        if (!Number.isFinite(roughStep) || roughStep <= 0) {
            return 1;
        }
        const magnitude = Math.pow(10, Math.floor(Math.log10(roughStep)));
        const normalized = roughStep / magnitude;
        let nice = 10;
        if (normalized <= 1) {
            nice = 1;
        } else if (normalized <= 2) {
            nice = 2;
        } else if (normalized <= 5) {
            nice = 5;
        }
        return nice * magnitude;
    }

    function measureChartChromeHeight(chartType, barLabelLineCount, includeLegend) {
        const layout = buildLayoutPadding(chartType, barLabelLineCount);
        let categoryAxisHeight;
        if (chartType === 'BAR') {
            const lines = Math.max(1, barLabelLineCount);
            categoryAxisHeight = CHART_TEXT.axisXBar.padding + lines * 14 + 4;
        } else {
            categoryAxisHeight = 6 + 13 + 6;
        }
        const legendHeight = includeLegend === false ? 0 : CHART_LEGEND_HEIGHT;
        return layout.top + layout.bottom + categoryAxisHeight + legendHeight;
    }

    function resolvePlotHeight(sectionElement) {
        if (sectionElement && sectionElement.classList.contains('print-section--split-panel')) {
            return CHART_SPLIT_PLOT_HEIGHT_PX;
        }
        return CHART_PLOT_HEIGHT_PX;
    }

    function applyChartHeight(canvas, chartType, barLabelLineCount, plotHeight, includeLegend) {
        const totalHeight = plotHeight + measureChartChromeHeight(chartType, barLabelLineCount, includeLegend);
        setChartElementHeight(canvas, totalHeight);
    }

    function setChartElementHeight(canvas, totalHeight) {
        const chartContainer = canvas.closest('.print-chart');
        const isPrintDocument = document.body.classList.contains('print-document');
        const heightValue = isPrintDocument
            ? (totalHeight * 25.4 / 96).toFixed(2) + 'mm'
            : totalHeight + 'px';

        canvas.style.setProperty('height', heightValue, 'important');
        if (chartContainer) {
            chartContainer.style.height = heightValue;
        }
    }

    function buildFixedPlotHeightPlugin(plotHeight) {
        return {
            id: 'fixedPlotHeight',
            afterLayout: function (chart) {
                if (chart.$plotHeightAdjusted || !chart.chartArea || chart.chartArea.height <= 0) {
                    return;
                }

                const delta = plotHeight - chart.chartArea.height;
                if (Math.abs(delta) <= 2) {
                    chart.$plotHeightAdjusted = true;
                    return;
                }

                setChartElementHeight(chart.canvas, chart.height + delta);
                chart.$plotHeightAdjusted = true;
                chart.resize();
            }
        };
    }

    function snapDecimalStep(roughStep) {
        const candidates = [0.01, 0.02, 0.05, 0.1, 0.2, 0.25, 0.5, 1, 2, 5, 10];
        for (let i = 0; i < candidates.length; i++) {
            if (candidates[i] >= roughStep) {
                return candidates[i];
            }
        }
        return computeNiceStep(roughStep);
    }

    function computeValueAxis(values, forBarChart, decimalPlaces) {
        const max = maxOfValues(values);
        const coefficientScale = decimalPlaces > 0;
        const headroom = forBarChart ? 1.05 : 1.08;

        if (max <= 0) {
            const stepSize = coefficientScale ? 0.5 : 2;
            return { max: stepSize * 4, stepSize: stepSize, intervals: 4 };
        }

        const paddedMax = max * headroom;
        const roughStep = paddedMax / 5;
        const stepSize = coefficientScale
            ? snapDecimalStep(roughStep)
            : computeNiceStep(roughStep);

        const intervals = Math.max(1, Math.ceil(paddedMax / stepSize));

        return {
            max: intervals * stepSize,
            stepSize: stepSize,
            intervals: intervals
        };
    }

    function basePrintOptions() {
        return {
            devicePixelRatio: getChartDevicePixelRatio(),
            responsive: true,
            maintainAspectRatio: false,
            animation: false,
            interaction: {
                mode: 'nearest',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    align: 'center',
                    labels: {
                        usePointStyle: true,
                        pointStyle: 'circle',
                        boxWidth: CHART_TEXT.legend.boxWidth,
                        boxHeight: CHART_TEXT.legend.boxHeight,
                        padding: CHART_TEXT.legend.padding,
                        color: CHART_TEXT.color,
                        font: chartFont(CHART_TEXT.legend.size, CHART_TEXT.legend.weight)
                    }
                },
                tooltip: {
                    enabled: false
                }
            }
        };
    }

    function resolveTickFont(fontOptions) {
        const options = fontOptions || {};
        if (typeof Chart !== 'undefined' && Chart.helpers) {
            if (typeof Chart.helpers.toFont === 'function') {
                return Chart.helpers.toFont(options).string;
            }
            if (typeof Chart.helpers.toFontString === 'function') {
                return Chart.helpers.toFontString(options);
            }
        }
        const size = options.size || 10;
        const family = options.family || CHART_TEXT.family;
        const weight = options.weight || 'normal';
        return weight + ' ' + size + 'px ' + family;
    }

    function wrapLabelLines(label, maxWidth, ctx) {
        const words = String(label || '').trim().split(/\s+/).filter(Boolean);
        if (words.length === 0) {
            return [''];
        }

        const lines = [];
        let line = '';

        words.forEach(function (word) {
            const testLine = line ? line + ' ' + word : word;
            if (ctx.measureText(testLine).width <= maxWidth) {
                line = testLine;
                return;
            }
            if (line) {
                lines.push(line);
            }
            line = word;
        });

        if (line) {
            lines.push(line);
        }

        return lines;
    }

    function measureSlotWidth(chart, labelCount) {
        const count = Math.max(1, labelCount);
        const chartWidth = chart.chartArea && chart.chartArea.width > 0
            ? chart.chartArea.width
            : chart.width;
        if (!chartWidth) {
            return 80;
        }
        return (chartWidth / count) * 0.9;
    }

    function estimateBarLabelLineCount(canvas, rawLabels, fontOptions) {
        if (!canvas || rawLabels.length === 0) {
            return 1;
        }

        const ctx = canvas.getContext('2d');
        if (!ctx) {
            return 2;
        }

        ctx.save();
        ctx.font = resolveTickFont(fontOptions);
        const canvasWidth = canvas.clientWidth || canvas.offsetWidth || 600;
        const maxWidth = ((canvasWidth * 0.82) / rawLabels.length) * 0.9;
        const lineCount = rawLabels.reduce(function (max, label) {
            return Math.max(max, wrapLabelLines(label, maxWidth, ctx).length);
        }, 1);
        ctx.restore();

        return lineCount;
    }

    function buildCategoryScale(chartType, barLabelsOnX) {
        const needsOffset = chartType === 'LINE' || chartType === 'BAR' || chartType === 'MIXED';
        const indicatorLabelsOnX = chartType === 'BAR' && barLabelsOnX !== false;
        const xText = indicatorLabelsOnX ? CHART_TEXT.axisXBar : CHART_TEXT.axisX;
        const tickFont = chartFont(xText.size, CHART_TEXT.axisX.weight, CHART_TEXT.axisX.lineHeight);
        const ticks = {
            color: CHART_TEXT.color,
            font: tickFont,
            padding: xText.padding,
            maxRotation: 0,
            minRotation: 0,
            autoSkip: false,
            align: 'center'
        };

        if (indicatorLabelsOnX) {
            ticks.callback = function (tickValue) {
                const label = this.getLabelForValue(tickValue);
                if (!label) {
                    return '';
                }

                const chart = this.chart;
                if (!chart || !chart.ctx) {
                    return label;
                }

                const ctx = chart.ctx;
                ctx.save();
                ctx.font = resolveTickFont(this.options.font);
                const maxWidth = measureSlotWidth(chart, chart.data.labels.length);
                const lines = wrapLabelLines(label, maxWidth, ctx);
                ctx.restore();

                return lines.length === 1 ? lines[0] : lines;
            };
        }

        return {
            offset: needsOffset,
            grid: Object.assign({ offset: needsOffset }, gridStyle()),
            border: { display: true, color: CHART_TEXT.color, width: CHART_TEXT.axisBorder.width },
            ticks: ticks
        };
    }

    function buildLayoutPadding(chartType, barLabelLineCount) {
        if (chartType === 'LINE') {
            return { top: 20, right: 16, left: 16, bottom: 0 };
        }
        if (chartType === 'BAR') {
            const lineCount = barLabelLineCount > 0 ? barLabelLineCount : 1;
            return {
                top: 18,
                right: 12,
                left: 8,
                bottom: Math.max(0, (lineCount - 1) * 12)
            };
        }
        if (chartType === 'MIXED') {
            return { top: 20, right: 12, left: 8, bottom: 0 };
        }
        return { top: 20, right: 8, left: 4, bottom: 0 };
    }

    function buildBarChartDataByPeriods(periodLabels, indicator) {
        const values = indicator.values.length === periodLabels.length
            ? indicator.values
            : indicator.values.slice(-periodLabels.length);

        return {
            labels: periodLabels,
            labelLineCount: 1,
            datasets: [{
                type: 'bar',
                label: indicator.label,
                data: values,
                backgroundColor: periodLabels.map(function (_periodLabel, periodIndex) {
                    return withSeriesOpacity(seriesColor(periodIndex), CHART_SERIES.bar.opacity);
                }),
                borderColor: CHART_SERIES.bar.borderColor,
                borderWidth: CHART_SERIES.bar.borderWidth,
                yAxisID: 'y'
            }]
        };
    }

    function buildBarChartDataByIndicators(periodLabels, indicators, canvas) {
        const rawLabels = indicators.map(function (indicator) {
            return indicator.label;
        });
        const tickFont = chartFont(CHART_TEXT.axisXBar.size, CHART_TEXT.axisX.weight, CHART_TEXT.axisX.lineHeight);

        return {
            labels: rawLabels,
            labelLineCount: estimateBarLabelLineCount(canvas, rawLabels, tickFont),
            datasets: periodLabels.map(function (periodLabel, periodIndex) {
                const color = seriesColor(periodIndex);
                return Object.assign({
                    type: 'bar',
                    label: periodLabel,
                    data: indicators.map(function (indicator) {
                        return indicator.values[periodIndex];
                    }),
                    yAxisID: 'y'
                }, buildBarDatasetStyle(color));
            })
        };
    }

    function buildDatalabelsPlugin(decimalPlaces) {
        return {
            display: function (context) {
                if (context.dataset.data[context.dataIndex] == null) {
                    return false;
                }
                return context.dataset.type !== 'line';
            },
            align: function () {
                return 'top';
            },
            anchor: function () {
                return 'end';
            },
            offset: function (context) {
                return context.dataset.type === 'bar'
                    ? CHART_SERIES.dataLabelOffset.bar
                    : CHART_SERIES.dataLabelOffset.line;
            },
            clip: false,
            color: function (context) {
                if (context.dataset.type === 'line' && CHART_TEXT.dataLabel.line.useSeriesColor) {
                    return context.dataset.borderColor;
                }
                return CHART_TEXT.dataLabel.bar.color || CHART_TEXT.color;
            },
            font: function (context) {
                const style = context.dataset.type === 'line'
                    ? CHART_TEXT.dataLabel.line
                    : CHART_TEXT.dataLabel.bar;
                return chartFont(style.size, style.weight);
            },
            formatter: function (value) {
                return formatChartLabel(value, decimalPlaces);
            }
        };
    }

    function lineLabelColor(dataset) {
        const lineStyle = CHART_TEXT.dataLabel.line;
        if (lineStyle.useSeriesColor) {
            return dataset.borderColor;
        }
        return lineStyle.color || CHART_TEXT.color;
    }

    function measureLabelBox(ctx, text, x, y, anchor, fontSize) {
        const width = ctx.measureText(text).width;
        const height = fontSize * 1.25;
        if (anchor === 'top') {
            return {
                x: x,
                y: y,
                xLeft: x - width / 2,
                xRight: x + width / 2,
                yTop: y - height,
                yBottom: y
            };
        }
        return {
            x: x,
            y: y,
            xLeft: x - width / 2,
            xRight: x + width / 2,
            yTop: y,
            yBottom: y + height
        };
    }

    function labelBoxesCollide(a, b, minDistanceY, minDistanceX) {
        return Math.abs(a.y - b.y) < minDistanceY
            && Math.abs(a.x - b.x) < minDistanceX;
    }

    function labelBoxInChartArea(box, chartArea, padding) {
        const pad = padding || 4;
        return box.yTop >= chartArea.top + pad
            && box.yBottom <= chartArea.bottom - pad
            && box.xLeft >= chartArea.left + pad
            && box.xRight <= chartArea.right - pad;
    }

    function collectLineLabelPoints(chart, decimalPlaces) {
        const points = [];

        chart.data.datasets.forEach(function (dataset, datasetIndex) {
            if (dataset.type !== 'line') {
                return;
            }

            const meta = chart.getDatasetMeta(datasetIndex);
            if (!meta.visible) {
                return;
            }

            meta.data.forEach(function (element, dataIndex) {
                const value = dataset.data[dataIndex];
                if (value == null || !element || element.skip) {
                    return;
                }

                points.push({
                    x: element.x,
                    y: element.y,
                    text: formatChartLabel(value, decimalPlaces),
                    color: lineLabelColor(dataset)
                });
            });
        });

        points.sort(function (a, b) {
            if (Math.abs(a.x - b.x) > 1) {
                return a.x - b.x;
            }
            return a.y - b.y;
        });

        return points;
    }

    function buildSmartLineLabelsPlugin(decimalPlaces) {
        const lineStyle = CHART_TEXT.dataLabel.line;
        const font = chartFont(lineStyle.size, lineStyle.weight);
        const positions = lineStyle.positions || [
            { dy: -CHART_SERIES.dataLabelOffset.line, anchor: 'top' },
            { dy: CHART_SERIES.dataLabelOffset.line + 6, anchor: 'bottom' }
        ];

        return {
            id: 'smartLineLabels',
            afterDatasetsDraw: function (chart) {
                const chartArea = chart.chartArea;
                if (!chartArea) {
                    return;
                }

                const ctx = chart.ctx;
                const fontString = resolveTickFont(font);
                const placedLabels = [];
                const points = collectLineLabelPoints(chart, decimalPlaces);

                ctx.save();
                ctx.font = fontString;

                points.forEach(function (point) {
                    let placed = false;

                    for (let i = 0; i < positions.length; i++) {
                        const position = positions[i];
                        const labelY = point.y + position.dy;
                        const box = measureLabelBox(
                            ctx,
                            point.text,
                            point.x,
                            labelY,
                            position.anchor,
                            lineStyle.size
                        );

                        const collides = placedLabels.some(function (placedLabel) {
                            return labelBoxesCollide(
                                box,
                                placedLabel,
                                lineStyle.minDistanceY,
                                lineStyle.minDistanceX
                            );
                        });

                        if (!collides && labelBoxInChartArea(box, chartArea)) {
                            ctx.fillStyle = point.color;
                            ctx.textAlign = 'center';
                            ctx.textBaseline = position.anchor === 'top' ? 'bottom' : 'top';
                            ctx.fillText(point.text, point.x, labelY);
                            placedLabels.push(box);
                            placed = true;
                            break;
                        }
                    }

                    if (!placed) {
                        const fallback = positions[0];
                        const labelY = point.y + fallback.dy;

                        ctx.fillStyle = point.color;
                        ctx.textAlign = 'center';
                        ctx.textBaseline = fallback.anchor === 'top' ? 'bottom' : 'top';
                        ctx.fillText(point.text, point.x, labelY);
                        placedLabels.push(measureLabelBox(
                            ctx,
                            point.text,
                            point.x,
                            labelY,
                            fallback.anchor,
                            lineStyle.size
                        ));
                    }
                });

                ctx.restore();
            }
        };
    }

    function buildValueScale(min, max, stepSize, axisColor, withGrace, decimalPlaces) {
        const scale = {
            min: min,
            max: max,
            grid: Object.assign({}, gridStyle()),
            border: {
                display: true,
                color: axisColor || CHART_TEXT.color,
                width: CHART_TEXT.axisBorder.width
            },
            ticks: {
                stepSize: stepSize,
                color: axisColor || CHART_TEXT.color,
                font: chartFont(CHART_TEXT.axisY.size, CHART_TEXT.axisY.weight),
                padding: CHART_TEXT.axisY.padding,
                callback: function (value) {
                    return formatChartLabel(value, decimalPlaces);
                }
            }
        };
        if (withGrace && decimalPlaces <= 0) {
            scale.grace = '4%';
        }
        return scale;
    }

    function buildScales(chartType, barValues, lineValues, hasDualAxis, decimalPlaces, primaryAxis, barLabelsOnX) {
        const isBarPrimary = chartType === 'BAR' || chartType === 'MIXED';
        const primaryValues = chartType === 'LINE' ? lineValues : barValues;
        const resolvedPrimaryAxis = primaryAxis || computeValueAxis(primaryValues, isBarPrimary, decimalPlaces);

        const scales = {
            x: buildCategoryScale(chartType, barLabelsOnX),
            y: Object.assign(
                { position: 'left' },
                buildValueScale(0, resolvedPrimaryAxis.max, resolvedPrimaryAxis.stepSize, null, isBarPrimary, decimalPlaces)
            )
        };

        if (hasDualAxis) {
            const lineAxis = computeValueAxis(lineValues, false, decimalPlaces);
            scales.y1 = Object.assign(
                { position: 'right', grid: { drawOnChartArea: false } },
                buildValueScale(0, lineAxis.max, lineAxis.stepSize, CHART_SERIES.lineColor, true, decimalPlaces)
            );
        }

        return scales;
    }

    function createPrintChart(canvas, chartType, periodLabels, indicators, decimalPlaces, plotHeight, hideLegend) {
        const barIndicators = indicators.filter(function (item) {
            return item.seriesType !== 'LINE';
        });
        const lineIndicators = indicators.filter(function (item) {
            return item.seriesType === 'LINE';
        });

        const barValues = barIndicators.flatMap(function (item) { return item.values; });
        const lineValues = lineIndicators.length > 0
            ? lineIndicators.flatMap(function (item) { return item.values; })
            : indicators.flatMap(function (item) { return item.values; });

        const hasDualAxis = chartType === 'MIXED' && barIndicators.length > 0 && lineIndicators.length > 0;
        const isBarPrimary = chartType === 'BAR' || chartType === 'MIXED';
        const primaryValues = chartType === 'LINE' ? lineValues : barValues;
        const primaryAxis = computeValueAxis(primaryValues, isBarPrimary, decimalPlaces);
        const barLabelsOnX = chartType !== 'BAR' || indicators.length > 1;
        let chartLabels = periodLabels;
        let datasets = [];
        let barLabelLineCount = 0;
        hideLegend = hideLegend === true;

        if (chartType === 'BAR') {
            if (indicators.length === 1) {
                const barData = buildBarChartDataByPeriods(periodLabels, indicators[0]);
                chartLabels = barData.labels;
                datasets = barData.datasets;
                barLabelLineCount = barData.labelLineCount;
                hideLegend = true;
            } else {
                const barData = buildBarChartDataByIndicators(periodLabels, indicators, canvas);
                chartLabels = barData.labels;
                datasets = barData.datasets;
                barLabelLineCount = barData.labelLineCount;
            }
        } else if (chartType === 'LINE') {
            indicators.forEach(function (indicator) {
                datasets.push(Object.assign({
                    type: 'line',
                    label: indicator.label,
                    data: indicator.values,
                    spanGaps: false,
                    yAxisID: 'y'
                }, buildLineDatasetStyle(indicator.color)));
            });
        } else {
            barIndicators.forEach(function (indicator) {
                datasets.push(Object.assign({
                    type: 'bar',
                    label: indicator.label,
                    data: indicator.values,
                    yAxisID: 'y'
                }, buildBarDatasetStyle(indicator.color)));
            });
            lineIndicators.forEach(function (indicator) {
                datasets.push(Object.assign({
                    type: 'line',
                    label: indicator.label,
                    data: indicator.values,
                    spanGaps: false,
                    yAxisID: hasDualAxis ? 'y1' : 'y'
                }, buildLineDatasetStyle(CHART_SERIES.lineColor)));
            });
        }

        applyChartHeight(canvas, chartType, barLabelLineCount, plotHeight, !hideLegend);

        const options = basePrintOptions();
        if (hideLegend) {
            options.plugins.legend.display = false;
        }
        options.layout = {
            padding: buildLayoutPadding(chartType, barLabelLineCount)
        };
        options.datasets = {
            bar: {
                categoryPercentage: CHART_SERIES.bar.categoryPercentage,
                barPercentage: CHART_SERIES.bar.barPercentage
            }
        };
        options.plugins.datalabels = buildDatalabelsPlugin(decimalPlaces);
        options.scales = buildScales(
            chartType,
            barValues,
            lineValues,
            hasDualAxis,
            decimalPlaces,
            primaryAxis,
            barLabelsOnX
        );

        const plugins = [buildFixedPlotHeightPlugin(plotHeight)];
        if (chartType === 'LINE' || chartType === 'MIXED') {
            plugins.push(buildSmartLineLabelsPlugin(decimalPlaces));
        }

        return new Chart(canvas, {
            type: chartType === 'MIXED' ? 'bar' : (chartType === 'LINE' ? 'line' : 'bar'),
            data: {
                labels: chartLabels,
                datasets: datasets
            },
            options: options,
            plugins: plugins
        });
    }

    function initSection(sectionElement) {
        const canvas = sectionElement.querySelector('.print-section__chart-canvas');
        if (!canvas) {
            return null;
        }

        const chartType = (sectionElement.dataset.chartType || 'BAR').toUpperCase();
        const periodLabels = (sectionElement.dataset.periodLabels || '')
            .split(',')
            .map(function (label) { return label.trim(); })
            .filter(Boolean);

        const indicators = readIndicators(sectionElement);
        if (indicators.length === 0 || periodLabels.length === 0) {
            return null;
        }

        const resolved = resolveChartPeriods(periodLabels, indicators);
        updateChartTitle(sectionElement, resolved.periodLabels);
        const plotHeight = resolvePlotHeight(sectionElement);
        const hideLegend = sectionElement.classList.contains('print-section--split-panel');

        return createPrintChart(
            canvas,
            chartType,
            resolved.periodLabels,
            resolved.indicators,
            readChartDecimalPlaces(sectionElement),
            plotHeight,
            hideLegend
        );
    }

    function registerPlugins() {
        if (typeof Chart === 'undefined') {
            return false;
        }

        if (window.ChartDataLabels) {
            Chart.register(window.ChartDataLabels);
        }

        return true;
    }

    function isPdfExportEnabled(paramName) {
        var params = new URLSearchParams(window.location.search);
        if (!params.has(paramName)) {
            return true;
        }
        return params.get(paramName) !== 'false';
    }

    function freezeSplitPanelChartsForPrint() {
        if (!document.body.classList.contains('print-document')) {
            return;
        }

        document.querySelectorAll('.print-section--split-panel .print-section__chart-canvas').forEach(function (canvas) {
            if (!(canvas instanceof HTMLCanvasElement)) {
                return;
            }

            var chart = typeof Chart !== 'undefined' ? Chart.getChart(canvas) : null;
            var dataUrl;
            try {
                dataUrl = chart ? chart.toBase64Image('image/png', 1) : canvas.toDataURL('image/png');
            } catch (error) {
                return;
            }

            if (chart) {
                chart.destroy();
            }

            var image = document.createElement('img');
            image.src = dataUrl;
            image.alt = '';
            image.className = 'print-section__chart-canvas print-section__chart-image';
            image.style.width = '100%';
            image.style.height = 'auto';
            image.style.display = 'block';
            canvas.replaceWith(image);
        });
    }

    function applyPdfExportOptions() {
        if (!document.body.classList.contains('print-document')) {
            return;
        }

        if (!isPdfExportEnabled('tables')) {
            document.querySelectorAll('.print-chapter--summary').forEach(function (chapter) {
                chapter.remove();
            });
        }

        if (!isPdfExportEnabled('charts')) {
            document.querySelectorAll('.print-chart, .print-section__chart-title').forEach(function (element) {
                element.remove();
            });
        }

        if (!isPdfExportEnabled('recommendations')) {
            document.querySelectorAll('.print-indicator-card__recommendation').forEach(function (element) {
                element.remove();
            });
        }

        if (!isPdfExportEnabled('comment')) {
            document.querySelectorAll('.print-chapter--comment').forEach(function (chapter) {
                chapter.remove();
            });
        }

        if (!isPdfExportEnabled('esg')) {
            document.querySelectorAll('.print-chapter--esg').forEach(function (chapter) {
                chapter.remove();
            });
        }
    }

    function init() {
        if (!registerPlugins()) {
            readyResolve();
            return readyPromise;
        }

        document.querySelectorAll('.print-section').forEach(function (sectionElement) {
            initSection(sectionElement);
        });

        requestAnimationFrame(function () {
            requestAnimationFrame(readyResolve);
        });

        return readyPromise;
    }

    window.PrintCharts = {
        init: init,
        ready: readyPromise,
        text: CHART_TEXT,
        series: CHART_SERIES,
        CHART_PLOT_HEIGHT_PX: CHART_PLOT_HEIGHT_PX,
        CHART_SPLIT_PLOT_HEIGHT_PX: CHART_SPLIT_PLOT_HEIGHT_PX
    };

    function markPrintReady() {
        if (window.PrintCharts && window.PrintCharts.ready) {
            window.PrintCharts.ready.then(function () {
                freezeSplitPanelChartsForPrint();
                requestAnimationFrame(function () {
                    document.body.setAttribute('data-print-ready', 'true');
                });
            });
            return;
        }
        freezeSplitPanelChartsForPrint();
        document.body.setAttribute('data-print-ready', 'true');
    }

    document.addEventListener('DOMContentLoaded', function () {
        applyPdfExportOptions();
        init();
        markPrintReady();
    });
})();
