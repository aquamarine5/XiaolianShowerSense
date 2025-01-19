<script setup>
import wnetwork from '@/wnetwork';
import * as echarts from 'echarts';
import LineMdListIndentedReversed from '~icons/line-md/list-indented-reversed?width=28px&height=28px';
import LineMdBellAlertTwotoneLoop from '~icons/line-md/bell-alert-twotone-loop?width=28px&height=28px';
import LineMdChatAlertTwotone from '~icons/line-md/chat-alert-twotone?width=8px&height=8px';
import { nextTick, ref } from 'vue';

</script>
<template>
    <div class="analysis_container" v-if="isDataReady">
        <div class="analysis_title">
            <LineMdListIndentedReversed class="analysis_icon" />
            分析图表：
            <div class="analysis_test">
                v0.7 BETA
            </div>
        </div>
        <div v-if="isShowAnalysis" class="analysis_view">
            <div ref="chart" class="analysis_chart"></div>
            <div class="analysis_tips">
                可以拖动缩放查看更多数据哦🧐
            </div>
            <div v-if="!isAnalysisEnabled" class="analysis_disabled">
                <LineMdChatAlertTwotone class="analysis_icon" />
                当前分析功能已停用，可能由于当前为假期时间。
            </div>
        </div>
        <div class="analysis_error" v-else>
            <LineMdBellAlertTwotoneLoop class="analysis_icon" />
            <div class="analysis_error_tips">
                没有地图数据，也是没办法分析数据的呢😢
            </div>
        </div>
    </div>
</template>
<style scoped>
.analysis_disabled {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 6px;
    font-size: smaller;
    background-color: rgb(255, 230, 0);
    padding: 5px;
    border-radius: 8px;
}

.analysis_icon {
    min-height: 28px;
    min-width: 28px;
}

.analysis_tips {
    font-size: small;
    color: #666;
    text-align: center;
}

.analysis_error {
    display: flex;
    align-items: center;
    width: 100%;
    gap: 10px;

    background-color: rgb(255, 230, 0);
    padding: 5px 8px;
    margin: 1px 5px;
    border-radius: 8px;
}

.analysis_view {
    width: 100%;
}

.analysis_container {
    flex-direction: column;
    width: 100%;
    display: flex;
    border-width: 5px;
    border-style: solid;
    align-items: center;
    border-color: transparent;
    background-image: linear-gradient(to right, #fff, #fff), linear-gradient(-30deg, #fcda9f, #0799f988);
    background-clip: padding-box, border-box;
    background-origin: padding-box, border-box;
    border-radius: 10px;
    padding: 8px 12px;
    box-sizing: border-box;
    margin-top: 10px;
}

.analysis_title {
    gap: 8px;
    display: flex;
    align-items: center;
    padding-bottom: 1px;
    width: 100%;
    font-weight: 600;
}

.analysis_test {
    font-family: "Gilroy", sans-serif;
    cursor: help;
    color: white;
    font-weight: 600;
    font-size: small;
    border-radius: 20px;
    padding: 2px 5px;
    background-image: linear-gradient(135deg, #1BF1FD, #E4A6E3, #F3EFCC);
}

.analysis_chart {
    width: 100%;
    height: 350px;
}
</style>
<script>
const isAnalysisEnabled = ref(true)
const isDataReady = ref(false);
const isShowAnalysis = ref(true);
function formatDate(t) {
    var seconds = Math.floor((t) % 60),
        minutes = Math.floor((t / 60) % 60),
        hours = Math.floor((t / (60 * 60)) % 24)

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds
    if (hours == "00")
        return minutes + " 分 " + seconds + " 秒"
    return hours + " 时 " + minutes + " 分 " + seconds + " 秒"
}
function formatDateLined(t) {
    var seconds = Math.floor((t) % 60),
        minutes = Math.floor((t / 60) % 60),
        hours = Math.floor((t / (60 * 60)) % 24)

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds
    if (hours == "00")
        return minutes + "分\n" + seconds + "秒"
    return hours + "时\n" + minutes + "分\n" + seconds + "秒"
}
export default {
    mounted() {
        wnetwork.get("/api/analysis?residenceId=" + sessionStorage.getItem("residenceId")).then(response => {
            console.log(response.data);
            isDataReady.value = true;
            if (response.data.data == "x") {
                isShowAnalysis.value = false;
                return;
            }
            isAnalysisEnabled.value = response.data.data.isEnabled
            let xAxisData = [];
            let minData = [];
            let avgData = [];
            let maxData = [];
            for (let i = response.data.data.startTime; i <= response.data.data.endTime; i % 100 == 50 ? i += 50 : i += 10) {
                let strTimePos = i.toString();
                xAxisData.push(strTimePos.substring(1, 3) + " : " + strTimePos.substring(3, 5));
                minData.push(response.data.data[strTimePos].minv);
                avgData.push(response.data.data[strTimePos].avgv);
                maxData.push(response.data.data[strTimePos].maxv);
            }

            function getRoundedMinutes() {
                const now = new Date();
                const minutes = now.getMinutes();
                const roundedMinutes = Math.round(minutes / 10) * 10;
                return roundedMinutes;
            }

            let nowTime = new Date();
            let nowTimePos = 80000 + nowTime.getHours() * 100 + getRoundedMinutes();
            let timePosDuration = response.data.data.endTime - response.data.data.startTime;
            nextTick(() => {
                var chart = echarts.init(this.$refs.chart);
                chart.setOption({
                    xAxis: {
                        type: 'category',
                        data: xAxisData
                    },
                    yAxis: {
                        type: 'value',
                        axisLabel: {
                            formatter: function (value) {
                                return formatDateLined(value);
                            }
                        },
                        max: (value) => {
                            return value.max > 3600 ? value.max : 3600;
                        },
                        scale: true
                    },
                    legend: {
                        data: ["最小值", "平均值", "最大值"],
                    },
                    grid: {
                        left: 40,
                        right: 0,
                        top: 38,
                        bottom: 20
                    },
                    series: [{
                        name: "最小值",
                        data: minData,
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 2
                        },
                        tooltip: {
                            valueFormatter: function (value) {
                                return formatDate(value);
                            }
                        }
                    }, {
                        name: "平均值",
                        data: avgData,
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 3
                        },
                        areaStyle: {},
                        tooltip: {
                            valueFormatter: function (value) {
                                return formatDate(value);
                            }
                        }
                    }, {
                        name: "最大值",
                        data: maxData,
                        type: 'line',
                        smooth: true,
                        lineStyle: {
                            width: 2
                        },
                        tooltip: {
                            valueFormatter: function (value) {
                                return formatDate(value);
                            }
                        },
                    }, {
                        "name": "当前时间",
                        type: "line",
                        markLine: {
                            symbol: "none",
                            label: {
                                show: true,
                                position: "end",
                                formatter: "当前时间"
                            },
                            lineStyle: {
                                type: "solid",
                                color: "#F00",
                                width: 2
                            },
                            data: [{
                                name: "当前时间",
                                xAxis: nowTime.getHours() + " : " + getRoundedMinutes()
                            }]
                        }
                    }],
                    dataZoom: [{
                        type: 'inside',
                        filterMode: 'filter',
                        animation: true,
                        minSpan: 20,
                        maxSpan: 100,
                        start: nowTimePos < response.data.data.startTime || nowTimePos > response.data.data.endTime ? 0 : (nowTimePos - response.data.data.startTime - 80) / timePosDuration * 100,
                        end: nowTimePos < response.data.data.startTime || nowTimePos > response.data.data.endTime ? 100 : (nowTimePos - response.data.data.startTime + 80) / timePosDuration * 100,
                        show: true,
                        handleSize: 8
                    }],
                    tooltip: {
                        trigger: "axis"
                    }
                });
            });
        })
    }
};
</script>