<script setup>
import wnetwork from '@/wnetwork';
import * as echarts from 'echarts';
import LineMdListIndentedReversed from '~icons/line-md/list-indented-reversed?width=28px&height=28px';

</script>
<template>
    <div class="analysis_container">
        <div class="analysis_title">
            <LineMdListIndentedReversed/>
            分析图表：
            <div class="analysis_test">
                v0.7 BETA
            </div>
        </div>
        <div ref="chart" class="analysis_chart"></div>
    </div>
</template>
<style scoped>
.analysis_container{
    flex-direction: column;
    width: 100%;
    display: flex;
    border-width: 5px;
    border-style: solid;
    align-items: center;
    border-color: transparent;
    background-image: linear-gradient(to right, #fff, #fff), linear-gradient(-45deg, #fcda9f,#f99178,#0799f9);
    background-clip: padding-box, border-box;
    background-origin: padding-box, border-box;
    border-radius: 10px;
    padding: 8px 12px;
    box-sizing: border-box;
    margin-top: 10px;
}
.analysis_title{
    gap: 8px;
    display: flex;
    align-items: center;
    padding-bottom: 7px;
    width: 100%;
    font-weight: 600;
}
.analysis_test{
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
                    type: 'value',
                    axisLabel:{
                        formatter:function(value){
                            return formatDateLined(value);
                        }
                    }
                },
                legend: {
                    data: ["最小值", "平均值", "最大值"]
                },
                grid:{
                    left: 40,
                    right: 0,
                    top: 30,
                    bottom: 20
                },
                series: [{
                    name: "最小值",
                    data: minData,
                    type: 'line',
                    smooth: true,
                    lineStyle:{
                        width:2
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
                    lineStyle:{
                        width:3
                    },
                    areaStyle:{},
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
                    lineStyle:{
                        width:2
                    },
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
                            color: "#F00",
                            width:2
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
                    start: nowTimePos < response.data.data.startTime ? 0 : (nowTimePos - response.data.data.startTime - 100) / timePosDuration * 100,
                    end: nowTimePos > response.data.data.endTime ? 0 : (nowTimePos - response.data.data.startTime + 100) / timePosDuration * 100,
                    show: true,
                    handleSize: 8
                }],
                tooltip: {
                    trigger:"axis"
                }
            });
        });
    }
};
</script>