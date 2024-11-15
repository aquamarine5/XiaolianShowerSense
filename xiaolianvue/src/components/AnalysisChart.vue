<script setup>
import wnetwork from '@/wnetwork';
import * as echarts from 'echarts';

</script>
<template>
    <div ref="chart" style="width: 100%; height: 400px;"></div>
</template>
<style scoped>
/* 你的样式代码 */
</style>
<script>
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
export default {
    mounted() {
        wnetwork.get("/api/analysis?residenceId=" + sessionStorage.getItem("residenceId")).then(response => {
            console.log(response.data);
            let xAxisData = [];
            let minData = [];
            let avgData = [];
            let maxData = [];
            for (let i = response.data.data.startTime; i <= response.data.data.endTime; i % 100 == 50 ? i += 50 : i += 10) {
                let strTimePos = i.toString();
                xAxisData.push(strTimePos.substring(1, 3) + "时" + strTimePos.substring(3, 5) + "分");
                minData.push(response.data.data[i.toString()].minv);
                avgData.push(response.data.data[i.toString()].avgv);
                maxData.push(response.data.data[i.toString()].maxv);
            }

            function getRoundedMinutes() {
                const now = new Date();
                const minutes = now.getMinutes();
                const roundedMinutes = Math.floor(minutes / 10) * 10;
                return roundedMinutes;
            }

            let nowTime = new Date();
            let nowTimePos = 80000 + nowTime.getHours() * 100 + getRoundedMinutes();
            let timePosDuration = response.data.data.endTime - response.data.data.startTime;
            var chart = echarts.init(this.$refs.chart);
            chart.setOption({
                xAxis: {
                    type: 'category',
                    data: xAxisData
                },
                yAxis: {
                    type: 'value'
                },
                series: [{
                    name: "最小值",
                    data: minData,
                    type: 'line',
                    smooth: true,
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
                    tooltip: {
                        valueFormatter: function (value) {
                            return formatDate(value);
                        }
                    },
                    markLine: {
                        symbol: "none",
                        label: {
                            show: true,
                            position: "end",
                            formatter:"当前时间"
                        },
                        lineStyle: {
                            type: "solid",
                            color: "#333333"
                        },
                        data: [{
                            name: "当前时间",
                            xAxis: nowTime.getHours() + "时" + getRoundedMinutes() + "分"
                        }]
                    }
                }],
                dataZoom: [{
                    type: 'inside',
                    filterMode: 'filter',
                    animation: true,
                    minSpan: 20,
                    maxSpan: 100,
                    start: nowTimePos < response.data.data.startTime ? 0 : (nowTimePos - response.data.data.startTime - 100) / timePosDuration * 100,
                    end: nowTimePos > response.data.data.endTime ? 0 : (nowTimePos - response.data.data.startTime + 100) / timePosDuration * 100,
                    show: true,
                    handleSize: 8
                }],
                tooltip: {
                    trigger:"axis"
                }
                // 其他图表配置
            });
        });
    }
};
</script>