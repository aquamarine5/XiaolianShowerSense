<script setup lang="js">

const props = defineProps({
    name: {
        type: String
    },
    id: {
        type: Number
    },
    status: {
        type: Number
    },
    tme: {
        type: Number
    },
    wtime: {
        type: Number
    }
})
function formatDate(t) {
    var seconds = Math.floor((t / 1000) % 60),
        minutes = Math.floor((t / (1000 * 60)) % 60),
        hours = Math.floor((t / (1000 * 60 * 60)) % 24)

    hours = (hours < 10) ? "0" + hours : hours
    minutes = (minutes < 10) ? "0" + minutes : minutes
    seconds = (seconds < 10) ? "0" + seconds : seconds
    if (hours == "00")
        return minutes + " 分 " + seconds + " 秒"

    return hours + " 时 " + minutes + " 分 " + seconds + " 秒"
}
function formatColor(s) {
    if (s == 1) return '#A9A9A9'
    else if (s == 2) return '#00F'
    else return '#FF0'
}
function parseStatus() {
    if (props.status == 1) {
        tipstext.value = "距离上次启用过去了："
        time.value = formatDate(new Date().getTime() - props.wtime)
    }
    else if (props.status == 2) {
        tipstext.value = "已经洗了："
        time.value = formatDate(new Date().getTime() - props.tme)
    } else {
        tipstext.value = "Oops! 已故障。"
    }
}
var tipstext = defineModel('tipstext')
tipstext.value = "已经洗了："
var time = defineModel('time')
var timer;
parseStatus()
timer = setInterval(function () {
    parseStatus()
}, 1000)
</script>

<template>
    <div class="device_container">
        <div class="device_container_top">
            <div class="device_status" :style="{ 'background-color': formatColor(props.status) }"></div>
            <div class="device_id">{{ props.id }}</div>
            <div class="device_name">{{ props.name }}</div>
        </div>
        <div class="device_time">
            <div class="device_time_text">{{ tipstext }}</div>
            <div class="device_time_dynamic">{{ time }}</div>
        </div>
    </div>
</template>

<script lang="js">

export default {
    data() {
        return {
            timer: null,
            time: 0
        }
    }
}
</script>

<style>
.device_id {
    font-weight: 600;
    padding-right: 10px;
}

.device_name {
    color: rgb(75, 75, 75)
}

.device_container {
    height: 50px;
}

.device_status {
    height: 13px;
    width: 13px;
    border-radius: 3px;
    margin-right: 10px;
}

.device_container_top {
    display: flex;
    align-items: center;
    padding-bottom: 1px;
}

.device_time {
    display: flex;
    align-items: center;
}
</style>