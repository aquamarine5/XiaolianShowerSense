<script setup>
import { ref, computed } from 'vue';
import MapWasher from './MapWasher.vue';


const props = defineProps(
    ["roadData", 'avgWashTime', 'isready']
)
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
const roadDisplayTime = ref(0)
const roadDisplayValue = ref(0)
function refreshTime() {
    if (!props.isready) return 0;
    let value = 0;
    function calculatedValue(element) {
        if (element == null) return
        if (element.status == 2) {
            let predictedValue = props.avgWashTime / 1000 - (new Date().getTime() / 1000 - element.time / 1000)
            value += predictedValue < 0 ? 5 * 60 : predictedValue
        }
        else if (element.status == 1) {
            value += 0
        } else {
            value += 3 * 60
        }
    }
    props.roadData[0].forEach(element => {
        calculatedValue(element)
    });
    props.roadData[1].forEach(element => {
        calculatedValue(element)
    });
    roadDisplayTime.value = value
    let result = value / (70 * 60)
    roadDisplayValue.value = result <= 1 ? result : 1
}
refreshTime()
setInterval(refreshTime, 1000);

const startColor = { r: 65, g: 179, b: 73 }; // 起始颜色：绿色
const endColor = { r: 244, g: 62, b: 6 }; // 结束颜色：红色

const interpolateColor = (start, end, factor) => {
    const result = {
        r: Math.round(start.r + factor * (end.r - start.r)),
        g: Math.round(start.g + factor * (end.g - start.g)),
        b: Math.round(start.b + factor * (end.b - start.b))
    };
    return `rgb(${result.r}, ${result.g}, ${result.b})`;
};
const roadDisplayColor = computed(() => {
    return interpolateColor(startColor, endColor, roadDisplayValue.value);
});
</script>

<template>
    <div class="road_container" v-if="props.isready">
        <div class="road_neighbours">
            <MapWasher v-for="(washer, id) in props.roadData[0]" :key="id" :deviceInfo="washer"
                :avgWashTime="props.avgWashTime" />
        </div>
        <div class="road_display" :style="{ backgroundColor: roadDisplayColor }">
            <div class="road_tips">
                {{ formatDate(roadDisplayTime) }}
            </div>
        </div>
        <div class="road_neighbours">
            <MapWasher v-for="(washer, id) in props.roadData[1]" :key="id" :deviceInfo="washer"
                :avgWashTime="props.avgWashTime" />
        </div>
    </div>
</template>

<style>
@import "../../font.css";
.road_neighbours {
    display: flex;
    gap: 5px;
    justify-content: space-evenly;
}

.road_display {
    transition: background-color 0.3s ease-in-out;
    color: #FFF;
    border-radius: 999px 400px 400px 999px;
}

.road_tips {
    font-family: "Gilroy";
    text-align: end;
    padding-right: 6px;
    font-size: smaller;
}
</style>