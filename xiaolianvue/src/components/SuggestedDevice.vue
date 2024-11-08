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
var timeText = defineModel('suggestedDevice_timeText')
timeText.value = formatDate(new Date().getTime() - props.tme)
var timer = setInterval(function () {
    timeText.value = formatDate(new Date().getTime() - props.tme)
}, 1000)
</script>

<template>
    <div class="suggested_device_container">
        <div class="suggested_device_id">
            {{ props.id }}
        </div>
        <div class="suggested_device_time">
            {{ timeText }}
        </div>
    </div>
</template>

<style>
.suggested_device_container {
    display: flex;
    border-radius: 3px;
    box-shadow: 2px solid black;
}

.suggested_device_id {
    padding-right: 6px;
    font-weight: 600;

}
</style>